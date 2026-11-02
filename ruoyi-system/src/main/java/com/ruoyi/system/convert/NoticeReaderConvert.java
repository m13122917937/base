package com.ruoyi.system.convert;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ruoyi.system.model.bo.NoticeReaderBO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * 在领域内部适配原 Mapper 投影，SQL 和 Service 原契约不变。
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface NoticeReaderConvert {
    NoticeReaderConvert INSTANCE = Mappers.getMapper(NoticeReaderConvert.class);

    /**
     * 按固定 SQL 别名读取用户与阅读时间，以类型化结果替换边界 Map；不增删查询或执行写入。
     *
     * @param source 原 SQL 投影行
     * @return 相同字段的领域查询结果
     */
    @Mapping(target = "userId", source = "userId", qualifiedByName = "number")
    @Mapping(target = "userName", source = "userName", qualifiedByName = "text")
    @Mapping(target = "nickName", source = "nickName", qualifiedByName = "text")
    @Mapping(target = "deptName", source = "deptName", qualifiedByName = "text")
    @Mapping(target = "phonenumber", source = "phonenumber", qualifiedByName = "text")
    @Mapping(target = "readTime", source = "readTime", qualifiedByName = "date")
    NoticeReaderBO toBO(Map<String, Object> source);

    /**
     * 逐行转换原查询结果；分页元数据由 BoundaryPages 保持。
     */
    List<NoticeReaderBO> toBOList(List<Map<String, Object>> source);

    /**
     * 将 JDBC 数字投影统一为 Long，保留 null，不解析任意对象文本。
     */
    @Named("number")
    default Long number(Object value) {
        return value == null ? null : ((Number) value).longValue();
    }

    /**
     * 读取 JDBC 字符串，保留 null；错误投影类型直接报错，避免静默变更字段。
     */
    @Named("text")
    default String text(Object value) {
        return value == null ? null : (String) value;
    }

    /**
     * 保持 JDBC Date/Timestamp 的原始时间精度，不改变时区或格式。
     */
    @Named("date")
    default Date date(Object value) {
        return value == null ? null : (Date) value;
    }
}
