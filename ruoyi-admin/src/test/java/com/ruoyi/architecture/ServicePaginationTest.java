package com.ruoyi.architecture;

import java.util.List;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.exception.UtilException;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.mapper.SysConfigMapper;
import com.ruoyi.system.service.impl.SysConfigServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 验证无 HTTP 上下文的 Service 分页、默认值、排序校验和失败清理。
 */
class ServicePaginationTest {
    /**
     * 只有目标 Mapper 执行时才存在分页状态，返回原 Page 保留总数，Service 返回前清理线程变量。
     */
    @Test
    void pagesAtMapperWithoutHttpAndKeepsMetadata() {
        PageDomain query = new PageDomain();
        query.setPageNum(2);
        query.setPageSize(3);
        query.setOrderByColumn("configId");
        query.setIsAsc("descending");
        query.setReasonable(false);
        Page<SysConfig> result = new Page<>(2, 3);
        result.setTotal(87);
        SysConfigMapper mapper = mock(SysConfigMapper.class);
        when(mapper.selectList(any(com.baomidou.mybatisplus.core.conditions.Wrapper.class))).thenAnswer(invocation -> {
            Page<?> page = PageHelper.getLocalPage();
            assertEquals(2, page.getPageNum());
            assertEquals(3, page.getPageSize());
            assertEquals("config_id desc", page.getOrderBy());
            assertFalse(page.getReasonable());
            return result;
        });
        assertNull(PageHelper.getLocalPage());
        assertSame(result, service(mapper).selectConfigList(new SysConfig(), query));
        assertEquals(87, result.getTotal());
        assertNull(PageHelper.getLocalPage());
    }

    /**
     * Mapper 抛出异常时保留原失败，随后全量查询不受前一次分页影响。
     */
    @Test
    void clearsFailedQueryBeforeFollowingUnpagedQuery() {
        SysConfigMapper mapper = mock(SysConfigMapper.class);
        ServiceException failure = new ServiceException("查询失败");
        when(mapper.selectList(any(com.baomidou.mybatisplus.core.conditions.Wrapper.class))).thenThrow(failure).thenAnswer(invocation -> {
            assertNull(PageHelper.getLocalPage());
            return List.of(new SysConfig(), new SysConfig());
        });
        SysConfigServiceImpl service = service(mapper);
        assertSame(failure, assertThrows(ServiceException.class,
                () -> service.selectConfigList(new SysConfig(), new PageDomain())));
        assertNull(PageHelper.getLocalPage());
        assertEquals(2, service.selectConfigList(new SysConfig()).size());
    }

    /**
     * 缺省与非正页码、页大小均使用安全默认值；未发起 SQL 的回调同样清理尚未消耗的分页。
     */
    @Test
    void defaultsMissingAndNonPositiveValuesAndClearsUnusedPage() {
        PageDomain invalid = new PageDomain();
        invalid.setPageNum(-1);
        invalid.setPageSize(0);
        for (PageDomain query : List.of(new PageDomain(), invalid)) {
            assertEquals(List.of(), PageUtils.startPage(query, () -> {
                Page<?> page = PageHelper.getLocalPage();
                assertEquals(1, page.getPageNum());
                assertEquals(10, page.getPageSize());
                assertTrue(page.getReasonable());
                return List.of();
            }));
            assertNull(PageHelper.getLocalPage());
        }
    }

    /**
     * 非法排序在查询前失败，Mapper 不执行且线程变量清空，避免用户输入进入 SQL。
     */
    @Test
    void rejectsUnsafeOrderBeforeMapperCall() {
        PageDomain query = new PageDomain();
        query.setOrderByColumn("configId; drop table sys_config");
        SysConfigMapper mapper = mock(SysConfigMapper.class);
        assertThrows(UtilException.class, () -> service(mapper).selectConfigList(new SysConfig(), query));
        verifyNoInteractions(mapper);
        assertNull(PageHelper.getLocalPage());
    }

    /**
     * 显式 null 分页参数清除已有状态后执行全量查询，回调内不出现分页状态且不包装返回集合。
     */
    @Test
    void supportsExplicitUnpagedQuery() {
        List<String> result = List.of("first", "second");
        PageHelper.startPage(7, 1);
        assertSame(result, PageUtils.startPage(null, () -> {
            assertNull(PageHelper.getLocalPage());
            return result;
        }));
        assertNull(PageHelper.getLocalPage());
    }

    /**
     * 只注入测试 Mapper，构造无需 Spring 容器、数据库或 HTTP 请求的真实 Service。
     *
     * @param mapper 模拟持久化入口
     * @return 真实参数配置 Service
     */
    private SysConfigServiceImpl service(SysConfigMapper mapper) {
        SysConfigServiceImpl service = new SysConfigServiceImpl();
        ReflectionTestUtils.setField(service, "baseMapper", mapper);
        return service;
    }
}
