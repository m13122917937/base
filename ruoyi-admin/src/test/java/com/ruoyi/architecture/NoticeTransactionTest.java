package com.ruoyi.architecture;

import java.util.UUID;

import com.ruoyi.system.mapper.SysNoticeMapper;
import com.ruoyi.system.mapper.SysNoticeReadMapper;
import com.ruoyi.system.service.ISysNoticeService;
import com.ruoyi.system.service.impl.SysNoticeServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 用真实内存数据库和 Spring 事务代理验证公告删除的原子性，不连接业务数据库。
 */
class NoticeTransactionTest {
    /**
     * 在已读记录删除后制造公告删除失败，验证数据库事务将已读记录恢复且公告仍存在。
     */
    @Test
    void rollsBackReadCleanupWhenNoticeDeletionFails() {
        Fixture fixture = fixture();
        when(fixture.notices().deleteByIds(any(java.util.Collection.class))).thenThrow(new DataIntegrityViolationException("fixture-failure"));
        assertThrows(DataIntegrityViolationException.class, () -> fixture.service().deleteNoticeByIds(new Long[]{1L}));
        assertEquals(1, fixture.jdbc().queryForObject("select count(*) from sys_notice_read", Integer.class));
        assertEquals(1, fixture.jdbc().queryForObject("select count(*) from sys_notice", Integer.class));
    }

    /**
     * 成功时同时删除公告和已读记录；空集合在任何 SQL 执行之前拒绝，避免扩大删除范围。
     */
    @Test
    void deletesBothRecordsAndRejectsEmptyScope() {
        Fixture fixture = fixture();
        when(fixture.notices().deleteByIds(any(java.util.Collection.class))).thenAnswer(invocation -> fixture.jdbc().update("delete from sys_notice where notice_id = ?", ((java.util.Collection<?>) invocation.getArgument(0)).iterator().next()));
        assertEquals(1, fixture.service().deleteNoticeByIds(new Long[]{1L}));
        assertEquals(0, fixture.jdbc().queryForObject("select count(*) from sys_notice_read", Integer.class));
        assertEquals(0, fixture.jdbc().queryForObject("select count(*) from sys_notice", Integer.class));
        clearInvocations(fixture.notices(), fixture.reads());
        assertThrows(IllegalArgumentException.class, () -> fixture.service().deleteNoticeByIds(new Long[0]));
        verifyNoInteractions(fixture.notices(), fixture.reads());
    }

    /**
     * 建立独立 H2 表，将两个 Mapper 写操作绑定到同一数据源，并安装真实注解事务拦截器。
     *
     * @return 与线上数据无关的事务验收夹具
     */
    private Fixture fixture() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource("jdbc:h2:mem:notice_" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1", "sa", "");
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        jdbc.execute("create table sys_notice (notice_id bigint primary key comment '公告编号')");
        jdbc.execute("create table sys_notice_read (notice_id bigint comment '已读记录所属公告编号')");
        jdbc.update("insert into sys_notice values (1)");
        jdbc.update("insert into sys_notice_read values (1)");
        SysNoticeMapper notices = mock(SysNoticeMapper.class);
        SysNoticeReadMapper reads = mock(SysNoticeReadMapper.class);
        when(reads.delete(any(com.baomidou.mybatisplus.core.conditions.Wrapper.class))).thenAnswer(invocation -> jdbc.update("delete from sys_notice_read where notice_id = ?", 1L));
        SysNoticeServiceImpl target = new SysNoticeServiceImpl(notices, reads);
        ProxyFactory proxy = new ProxyFactory(target);
        proxy.addAdvice(new TransactionInterceptor(new DataSourceTransactionManager(dataSource), new AnnotationTransactionAttributeSource()));
        return new Fixture(jdbc, notices, reads, (ISysNoticeService) proxy.getProxy());
    }

    /**
     * 独立数据库、Mapper 和事务服务的测试夹具；构造器与访问器由编译器生成。
     */
    private record Fixture(JdbcTemplate jdbc, SysNoticeMapper notices, SysNoticeReadMapper reads,
                           ISysNoticeService service) {
    }
}
