package com.ruoyi.quartz.convert;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import com.ruoyi.common.utils.BoundaryPages;
import com.ruoyi.quartz.domain.SysJob;
import com.ruoyi.quartz.model.bo.SysJobBO;
import com.ruoyi.quartz.domain.SysJobLog;
import com.ruoyi.quartz.model.bo.SysJobLogBO;

/**
 * 领域对象与独立 BO 的编译期转换，不使用反射或 JSON 复制。
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface BoundaryConvert {
    BoundaryConvert INSTANCE = Mappers.getMapper(BoundaryConvert.class);

    /**
     * 按同名属性及嵌套类型转换为领域 BO，不执行持久化。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    SysJobBO toSysJobBO(SysJob source);

    /**
     * 将领域 BO 转换为 Service 内部类型，保留数据范围和审计字段。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    SysJob toSysJob(SysJobBO source);

    /**
     * 逐项转换列表；分页元数据由调用方 BoundaryPages 保留。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    List<SysJobBO> toSysJobBOList(List<SysJob> source);

    /**
     * 逐项转换为 Service 入参列表，不改变业务执行顺序。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    List<SysJob> toSysJobList(List<SysJobBO> source);

    /**
     * 按同名属性及嵌套类型转换为领域 BO，不执行持久化。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    SysJobLogBO toSysJobLogBO(SysJobLog source);

    /**
     * 将领域 BO 转换为 Service 内部类型，保留数据范围和审计字段。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    SysJobLog toSysJobLog(SysJobLogBO source);

    /**
     * 逐项转换列表；分页元数据由调用方 BoundaryPages 保留。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    List<SysJobLogBO> toSysJobLogBOList(List<SysJobLog> source);

    /**
     * 逐项转换为 Service 入参列表，不改变业务执行顺序。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    List<SysJobLog> toSysJobLogList(List<SysJobLogBO> source);
}
