package com.ruoyi.system.convert;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.model.query.SysConfigQuery;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.system.model.query.SysDictDataQuery;
import com.ruoyi.common.core.domain.entity.SysDictType;
import com.ruoyi.system.model.query.SysDictTypeQuery;
import com.ruoyi.system.domain.SysLogininfor;
import com.ruoyi.system.model.query.SysLogininforQuery;
import com.ruoyi.common.core.domain.entity.SysMenu;
import com.ruoyi.system.model.query.SysMenuQuery;
import com.ruoyi.system.domain.SysNotice;
import com.ruoyi.system.model.query.SysNoticeQuery;
import com.ruoyi.system.domain.SysOperLog;
import com.ruoyi.system.model.query.SysOperLogQuery;

/** Service 内的查询模型转换，使用编译期映射隔离实体上的其他属性。 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PersistenceQueryConvert {
    PersistenceQueryConvert INSTANCE = Mappers.getMapper(PersistenceQueryConvert.class);

    /** 按明确字段映射为持久化查询条件，不复制上下文或执行数据库操作。 */
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "configName", source = "configName")
    @Mapping(target = "configType", source = "configType")
    @Mapping(target = "configKey", source = "configKey")
    @Mapping(target = "beginTime", expression = "java(source.getParams().get(\"beginTime\"))")
    @Mapping(target = "endTime", expression = "java(source.getParams().get(\"endTime\"))")
    SysConfigQuery toSysConfigQuery(SysConfig source);

    /** 按明确字段映射为持久化查询条件，不复制上下文或执行数据库操作。 */
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "dictType", source = "dictType")
    @Mapping(target = "dictLabel", source = "dictLabel")
    @Mapping(target = "status", source = "status")
    SysDictDataQuery toSysDictDataQuery(SysDictData source);

    /** 按明确字段映射为持久化查询条件，不复制上下文或执行数据库操作。 */
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "dictName", source = "dictName")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "dictType", source = "dictType")
    @Mapping(target = "beginTime", expression = "java(source.getParams().get(\"beginTime\"))")
    @Mapping(target = "endTime", expression = "java(source.getParams().get(\"endTime\"))")
    SysDictTypeQuery toSysDictTypeQuery(SysDictType source);

    /** 按明确字段映射为持久化查询条件，不复制上下文或执行数据库操作。 */
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "ipaddr", source = "ipaddr")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "userName", source = "userName")
    @Mapping(target = "beginTime", expression = "java(source.getParams().get(\"beginTime\"))")
    @Mapping(target = "endTime", expression = "java(source.getParams().get(\"endTime\"))")
    SysLogininforQuery toSysLogininforQuery(SysLogininfor source);

    /** 按明确字段映射为持久化查询条件，不复制上下文或执行数据库操作。 */
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "menuName", source = "menuName")
    @Mapping(target = "visible", source = "visible")
    @Mapping(target = "status", source = "status")
    SysMenuQuery toSysMenuQuery(SysMenu source);

    /** 按明确字段映射为持久化查询条件，不复制上下文或执行数据库操作。 */
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "noticeTitle", source = "noticeTitle")
    @Mapping(target = "noticeType", source = "noticeType")
    @Mapping(target = "createBy", source = "createBy")
    SysNoticeQuery toSysNoticeQuery(SysNotice source);

    /** 按明确字段映射为持久化查询条件，不复制上下文或执行数据库操作。 */
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "operIp", source = "operIp")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "businessType", source = "businessType")
    @Mapping(target = "businessTypes", expression = "java(source.getBusinessTypes() == null || source.getBusinessTypes().length == 0 ? null : source.getBusinessTypes())")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "operName", source = "operName")
    @Mapping(target = "beginTime", expression = "java(source.getParams().get(\"beginTime\"))")
    @Mapping(target = "endTime", expression = "java(source.getParams().get(\"endTime\"))")
    SysOperLogQuery toSysOperLogQuery(SysOperLog source);

}
