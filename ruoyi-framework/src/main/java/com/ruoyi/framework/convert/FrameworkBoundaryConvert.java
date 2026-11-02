package com.ruoyi.framework.convert;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import com.ruoyi.common.utils.BoundaryPages;
import com.ruoyi.system.convert.BoundaryConvert;
import com.ruoyi.common.core.domain.model.LoginBody;
import com.ruoyi.framework.model.bo.LoginParam;
import com.ruoyi.common.core.domain.model.RegisterBody;
import com.ruoyi.framework.model.bo.RegisterParam;

/**
 * 领域对象与独立 BO 的编译期转换，不使用反射或 JSON 复制。
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, uses = BoundaryConvert.class)
public interface FrameworkBoundaryConvert {
    FrameworkBoundaryConvert INSTANCE = Mappers.getMapper(FrameworkBoundaryConvert.class);

    /**
     * 按同名属性及嵌套类型转换为领域 BO，不执行持久化。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    LoginParam toLoginParam(LoginBody source);

    /**
     * 将领域 BO 转换为 Service 内部类型，保留数据范围和审计字段。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    LoginBody toLoginBody(LoginParam source);

    /**
     * 逐项转换列表；分页元数据由调用方 BoundaryPages 保留。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    List<LoginParam> toLoginParamList(List<LoginBody> source);

    /**
     * 逐项转换为 Service 入参列表，不改变业务执行顺序。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    List<LoginBody> toLoginBodyList(List<LoginParam> source);

    /**
     * 按同名属性及嵌套类型转换为领域 BO，不执行持久化。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    RegisterParam toRegisterParam(RegisterBody source);

    /**
     * 将领域 BO 转换为 Service 内部类型，保留数据范围和审计字段。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    RegisterBody toRegisterBody(RegisterParam source);

    /**
     * 逐项转换列表；分页元数据由调用方 BoundaryPages 保留。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    List<RegisterParam> toRegisterParamList(List<RegisterBody> source);

    /**
     * 逐项转换为 Service 入参列表，不改变业务执行顺序。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    List<RegisterBody> toRegisterBodyList(List<RegisterParam> source);
}
