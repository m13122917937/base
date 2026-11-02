package com.ruoyi.system.convert;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import com.ruoyi.common.utils.BoundaryPages;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.system.model.bo.SysDeptBO;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.system.model.bo.SysDictDataBO;
import com.ruoyi.common.core.domain.entity.SysDictType;
import com.ruoyi.system.model.bo.SysDictTypeBO;
import com.ruoyi.common.core.domain.entity.SysMenu;
import com.ruoyi.system.model.bo.SysMenuBO;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.system.model.bo.SysRoleBO;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.system.model.bo.SysUserBO;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.model.bo.SysConfigBO;
import com.ruoyi.system.domain.SysLogininfor;
import com.ruoyi.system.model.bo.SysLogininforBO;
import com.ruoyi.system.domain.SysNotice;
import com.ruoyi.system.model.bo.SysNoticeBO;
import com.ruoyi.system.domain.SysOperLog;
import com.ruoyi.system.model.bo.SysOperLogBO;
import com.ruoyi.system.domain.SysUserRole;
import com.ruoyi.system.model.bo.SysUserRoleBO;
import com.ruoyi.system.domain.vo.MetaVo;
import com.ruoyi.system.model.bo.RouterMetaBO;
import com.ruoyi.system.domain.vo.RouterVo;
import com.ruoyi.system.model.bo.RouterBO;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.system.model.bo.LoginSessionBO;

/**
 * 领域对象与独立 BO 的编译期转换，不使用反射或 JSON 复制。
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface BoundaryConvert {
    BoundaryConvert INSTANCE = Mappers.getMapper(BoundaryConvert.class);

    /**
     * 按同名属性及嵌套类型转换为领域 BO，不执行持久化。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    SysDeptBO toSysDeptBO(SysDept source);

    /**
     * 将领域 BO 转换为 Service 内部类型，保留数据范围和审计字段。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    SysDept toSysDept(SysDeptBO source);

    /**
     * 逐项转换列表；分页元数据由调用方 BoundaryPages 保留。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    List<SysDeptBO> toSysDeptBOList(List<SysDept> source);

    /**
     * 逐项转换为 Service 入参列表，不改变业务执行顺序。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    List<SysDept> toSysDeptList(List<SysDeptBO> source);

    /**
     * 按同名属性及嵌套类型转换为领域 BO，不执行持久化。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    SysDictDataBO toSysDictDataBO(SysDictData source);

    /**
     * 将领域 BO 转换为 Service 内部类型，保留数据范围和审计字段。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    SysDictData toSysDictData(SysDictDataBO source);

    /**
     * 逐项转换列表；分页元数据由调用方 BoundaryPages 保留。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    List<SysDictDataBO> toSysDictDataBOList(List<SysDictData> source);

    /**
     * 逐项转换为 Service 入参列表，不改变业务执行顺序。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    List<SysDictData> toSysDictDataList(List<SysDictDataBO> source);

    /**
     * 按同名属性及嵌套类型转换为领域 BO，不执行持久化。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    SysDictTypeBO toSysDictTypeBO(SysDictType source);

    /**
     * 将领域 BO 转换为 Service 内部类型，保留数据范围和审计字段。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    SysDictType toSysDictType(SysDictTypeBO source);

    /**
     * 逐项转换列表；分页元数据由调用方 BoundaryPages 保留。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    List<SysDictTypeBO> toSysDictTypeBOList(List<SysDictType> source);

    /**
     * 逐项转换为 Service 入参列表，不改变业务执行顺序。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    List<SysDictType> toSysDictTypeList(List<SysDictTypeBO> source);

    /**
     * 按同名属性及嵌套类型转换为领域 BO，不执行持久化。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    SysMenuBO toSysMenuBO(SysMenu source);

    /**
     * 将领域 BO 转换为 Service 内部类型，保留数据范围和审计字段。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    SysMenu toSysMenu(SysMenuBO source);

    /**
     * 逐项转换列表；分页元数据由调用方 BoundaryPages 保留。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    List<SysMenuBO> toSysMenuBOList(List<SysMenu> source);

    /**
     * 逐项转换为 Service 入参列表，不改变业务执行顺序。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    List<SysMenu> toSysMenuList(List<SysMenuBO> source);

    /**
     * 按同名属性及嵌套类型转换为领域 BO，不执行持久化。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    SysRoleBO toSysRoleBO(SysRole source);

    /**
     * 将领域 BO 转换为 Service 内部类型，保留数据范围和审计字段。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    SysRole toSysRole(SysRoleBO source);

    /**
     * 逐项转换列表；分页元数据由调用方 BoundaryPages 保留。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    List<SysRoleBO> toSysRoleBOList(List<SysRole> source);

    /**
     * 逐项转换为 Service 入参列表，不改变业务执行顺序。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    List<SysRole> toSysRoleList(List<SysRoleBO> source);

    /**
     * 按同名属性及嵌套类型转换为领域 BO，不执行持久化。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    SysUserBO toSysUserBO(SysUser source);

    /**
     * 将领域 BO 转换为 Service 内部类型，保留数据范围和审计字段。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    SysUser toSysUser(SysUserBO source);

    /**
     * 逐项转换列表；分页元数据由调用方 BoundaryPages 保留。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    List<SysUserBO> toSysUserBOList(List<SysUser> source);

    /**
     * 逐项转换为 Service 入参列表，不改变业务执行顺序。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    List<SysUser> toSysUserList(List<SysUserBO> source);

    /**
     * 按同名属性及嵌套类型转换为领域 BO，不执行持久化。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    SysConfigBO toSysConfigBO(SysConfig source);

    /**
     * 将领域 BO 转换为 Service 内部类型，保留数据范围和审计字段。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    SysConfig toSysConfig(SysConfigBO source);

    /**
     * 逐项转换列表；分页元数据由调用方 BoundaryPages 保留。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    List<SysConfigBO> toSysConfigBOList(List<SysConfig> source);

    /**
     * 逐项转换为 Service 入参列表，不改变业务执行顺序。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    List<SysConfig> toSysConfigList(List<SysConfigBO> source);

    /**
     * 按同名属性及嵌套类型转换为领域 BO，不执行持久化。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    SysLogininforBO toSysLogininforBO(SysLogininfor source);

    /**
     * 将领域 BO 转换为 Service 内部类型，保留数据范围和审计字段。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    SysLogininfor toSysLogininfor(SysLogininforBO source);

    /**
     * 逐项转换列表；分页元数据由调用方 BoundaryPages 保留。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    List<SysLogininforBO> toSysLogininforBOList(List<SysLogininfor> source);

    /**
     * 逐项转换为 Service 入参列表，不改变业务执行顺序。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    List<SysLogininfor> toSysLogininforList(List<SysLogininforBO> source);

    /**
     * 按同名属性及嵌套类型转换为领域 BO，不执行持久化。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    SysNoticeBO toSysNoticeBO(SysNotice source);

    /**
     * 将领域 BO 转换为 Service 内部类型，保留数据范围和审计字段。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    SysNotice toSysNotice(SysNoticeBO source);

    /**
     * 逐项转换列表；分页元数据由调用方 BoundaryPages 保留。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    List<SysNoticeBO> toSysNoticeBOList(List<SysNotice> source);

    /**
     * 逐项转换为 Service 入参列表，不改变业务执行顺序。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    List<SysNotice> toSysNoticeList(List<SysNoticeBO> source);

    /**
     * 按同名属性及嵌套类型转换为领域 BO，不执行持久化。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    SysOperLogBO toSysOperLogBO(SysOperLog source);

    /**
     * 将领域 BO 转换为 Service 内部类型，保留数据范围和审计字段。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    SysOperLog toSysOperLog(SysOperLogBO source);

    /**
     * 逐项转换列表；分页元数据由调用方 BoundaryPages 保留。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    List<SysOperLogBO> toSysOperLogBOList(List<SysOperLog> source);

    /**
     * 逐项转换为 Service 入参列表，不改变业务执行顺序。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    List<SysOperLog> toSysOperLogList(List<SysOperLogBO> source);

    /**
     * 按同名属性及嵌套类型转换为领域 BO，不执行持久化。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    SysUserRoleBO toSysUserRoleBO(SysUserRole source);

    /**
     * 将领域 BO 转换为 Service 内部类型，保留数据范围和审计字段。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    SysUserRole toSysUserRole(SysUserRoleBO source);

    /**
     * 逐项转换列表；分页元数据由调用方 BoundaryPages 保留。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    List<SysUserRoleBO> toSysUserRoleBOList(List<SysUserRole> source);

    /**
     * 逐项转换为 Service 入参列表，不改变业务执行顺序。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    List<SysUserRole> toSysUserRoleList(List<SysUserRoleBO> source);

    /**
     * 按同名属性及嵌套类型转换为领域 BO，不执行持久化。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    RouterMetaBO toRouterMetaBO(MetaVo source);

    /**
     * 将领域 BO 转换为 Service 内部类型，保留数据范围和审计字段。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    MetaVo toMetaVo(RouterMetaBO source);

    /**
     * 逐项转换列表；分页元数据由调用方 BoundaryPages 保留。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    List<RouterMetaBO> toRouterMetaBOList(List<MetaVo> source);

    /**
     * 逐项转换为 Service 入参列表，不改变业务执行顺序。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    List<MetaVo> toMetaVoList(List<RouterMetaBO> source);

    /**
     * 按同名属性及嵌套类型转换为领域 BO，不执行持久化。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    RouterBO toRouterBO(RouterVo source);

    /**
     * 将领域 BO 转换为 Service 内部类型，保留数据范围和审计字段。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    RouterVo toRouterVo(RouterBO source);

    /**
     * 逐项转换列表；分页元数据由调用方 BoundaryPages 保留。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    List<RouterBO> toRouterBOList(List<RouterVo> source);

    /**
     * 逐项转换为 Service 入参列表，不改变业务执行顺序。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    List<RouterVo> toRouterVoList(List<RouterBO> source);

    /**
     * 按同名属性及嵌套类型转换为领域 BO，不执行持久化。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    @Mapping(target = "authorities", ignore = true)
    LoginSessionBO toLoginSessionBO(LoginUser source);

    /**
     * 将领域 BO 转换为 Service 内部类型，保留数据范围和审计字段。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    @Mapping(target = "authorities", ignore = true)
    LoginUser toLoginUser(LoginSessionBO source);

    /**
     * 逐项转换列表；分页元数据由调用方 BoundaryPages 保留。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    List<LoginSessionBO> toLoginSessionBOList(List<LoginUser> source);

    /**
     * 逐项转换为 Service 入参列表，不改变业务执行顺序。 @param source 输入数据 @return 转换结果，空输入返回空
     */
    List<LoginUser> toLoginUserList(List<LoginSessionBO> source);
}
