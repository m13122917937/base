package com.ruoyi.architecture;

import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ruoyi.system.convert.NoticeReaderConvert;
import com.ruoyi.system.domain.SysNotice;
import com.ruoyi.system.domain.SysNoticeRead;
import com.ruoyi.system.model.bo.NoticeReaderBO;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.resultset.DefaultResultSetHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/** 使用真实 Mapper XML 和 MyBatis 结果处理器验证不同 JDBC 时间表示，不连接业务数据库。 */
class NoticeReaderMappingTest {
    /** 模拟 DATETIME 的元数据及 getObject 返回 LocalDateTime，验证显式 JDBC 时间读取可安全转换为领域 Date。 */
    @Test
    void mapsLocalDateTimeDriverValuesToDate() throws Exception {
        LocalDateTime local = LocalDateTime.of(2026, 11, 2, 10, 20, 30, 123_000_000);
        Date expected = new Date(Timestamp.valueOf(local).getTime());
        NoticeReaderBO reader = NoticeReaderConvert.INSTANCE.toBO(readRow(local, Timestamp.valueOf(local)));
        assertEquals(expected, reader.readTime());
        assertEquals(12L, reader.userId());
    }

    /** 兼容以 Timestamp 提供时间的驱动，保留日期毫秒值及其他投影字段。 */
    @Test
    void preservesTimestampDriverValues() throws Exception {
        Timestamp timestamp = Timestamp.valueOf("2026-11-02 10:20:30.123");
        NoticeReaderBO reader = NoticeReaderConvert.INSTANCE.toBO(readRow(timestamp, timestamp));
        assertEquals(timestamp.getTime(), reader.readTime().getTime());
        assertEquals(12L, reader.userId());
    }

    /** 阅读时间为空时仍保留用户行，领域时间为空，不进行任意字符串解析。 */
    @Test
    void preservesNullReadTime() throws Exception {
        NoticeReaderBO reader = NoticeReaderConvert.INSTANCE.toBO(readRow(null, null));
        assertNull(reader.readTime());
        assertEquals(12L, reader.userId());
    }

    /**
     * 加载实际查询映射，模拟 JDBC 对同一时间列的对象读取与 Timestamp 读取，交给真实 MyBatis 生成 Map。
     * 驱动对象类型只影响自动映射；显式 Date 映射应使用 getTimestamp 并保留空值。
     *
     * @param driverValue 驱动通过 getObject 返回的对象
     * @param timestamp 驱动通过 getTimestamp 返回的对象
     * @return 与实际查询字段名一致的一行投影
     * @throws Exception XML 加载或 JDBC 结果处理失败
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> readRow(Object driverValue, Timestamp timestamp) throws Exception {
        Configuration configuration = new Configuration();
        configuration.getTypeAliasRegistry().registerAlias("SysNotice", SysNotice.class);
        configuration.getTypeAliasRegistry().registerAlias("SysNoticeRead", SysNoticeRead.class);
        String resource = "mapper/system/SysNoticeReadMapper.xml";
        try (InputStream input = new ClassPathResource(resource).getInputStream()) {
            new XMLMapperBuilder(input, configuration, resource, configuration.getSqlFragments()).parse();
        }
        ResultSetMetaData metadata = mock(ResultSetMetaData.class);
        when(metadata.getColumnCount()).thenReturn(2);
        when(metadata.getColumnLabel(1)).thenReturn("userId");
        when(metadata.getColumnType(1)).thenReturn(Types.BIGINT);
        when(metadata.getColumnClassName(1)).thenReturn(Long.class.getName());
        when(metadata.getColumnLabel(2)).thenReturn("readTime");
        when(metadata.getColumnType(2)).thenReturn(Types.TIMESTAMP);
        when(metadata.getColumnClassName(2)).thenReturn(
                driverValue == null ? LocalDateTime.class.getName() : driverValue.getClass().getName());
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getMetaData()).thenReturn(metadata);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getLong("userId")).thenReturn(12L);
        when(resultSet.getObject("readTime")).thenReturn(driverValue);
        when(resultSet.getObject("readTime", LocalDateTime.class)).thenReturn(
                driverValue instanceof LocalDateTime ? (LocalDateTime) driverValue : null);
        when(resultSet.getTimestamp("readTime")).thenReturn(timestamp);
        Statement statement = mock(Statement.class);
        when(statement.getResultSet()).thenReturn(resultSet);
        when(statement.getUpdateCount()).thenReturn(-1);
        MappedStatement mapped = configuration.getMappedStatement(
                "com.ruoyi.system.mapper.SysNoticeReadMapper.selectReadUsersByNoticeId");
        DefaultResultSetHandler handler = new DefaultResultSetHandler(mock(Executor.class), mapped, null,
                null, mapped.getBoundSql(Map.of("noticeId", 1L)), RowBounds.DEFAULT);
        List<Object> rows = handler.handleResultSets(statement);
        assertEquals(1, rows.size());
        return (Map<String, Object>) rows.get(0);
    }
}
