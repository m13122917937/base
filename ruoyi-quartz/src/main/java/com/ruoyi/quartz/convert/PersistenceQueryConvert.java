package com.ruoyi.quartz.convert;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import com.ruoyi.quartz.domain.SysJob;
import com.ruoyi.quartz.model.query.SysJobQuery;
import com.ruoyi.quartz.domain.SysJobLog;
import com.ruoyi.quartz.model.query.SysJobLogQuery;

/** Service 内的查询模型转换，使用编译期映射隔离实体上的其他属性。 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PersistenceQueryConvert {
    PersistenceQueryConvert INSTANCE = Mappers.getMapper(PersistenceQueryConvert.class);

    /** 按明确字段映射为持久化查询条件，不复制上下文或执行数据库操作。 */
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "jobName", source = "jobName")
    @Mapping(target = "jobGroup", source = "jobGroup")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "invokeTarget", source = "invokeTarget")
    SysJobQuery toSysJobQuery(SysJob source);

    /** 按明确字段映射为持久化查询条件，不复制上下文或执行数据库操作。 */
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "jobName", source = "jobName")
    @Mapping(target = "jobGroup", source = "jobGroup")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "invokeTarget", source = "invokeTarget")
    @Mapping(target = "beginTime", expression = "java(source.getParams().get(\"beginTime\"))")
    @Mapping(target = "endTime", expression = "java(source.getParams().get(\"endTime\"))")
    SysJobLogQuery toSysJobLogQuery(SysJobLog source);
}
