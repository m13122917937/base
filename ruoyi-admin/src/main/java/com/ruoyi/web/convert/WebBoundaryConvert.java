package com.ruoyi.web.convert;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import com.ruoyi.system.model.bo.*;
import com.ruoyi.web.form.system.*;
import com.ruoyi.web.vo.system.*;
import com.ruoyi.quartz.model.bo.*;
import com.ruoyi.web.form.quartz.*;
import com.ruoyi.web.vo.quartz.*;
import com.ruoyi.framework.model.bo.*;
import com.ruoyi.web.form.framework.*;

/**
 * Web Form/VO 与领域 BO 的编译期转换器，保持原字段和嵌套对象结构。
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface WebBoundaryConvert {
    WebBoundaryConvert INSTANCE = Mappers.getMapper(WebBoundaryConvert.class);

    /**
     * 将类型化已读用户查询结果映射为同字段 Web 响应，不修改原值。
     */
    NoticeReaderVO toNoticeReaderVO(NoticeReaderBO source);

    /**
     * 将 Web 入参转换为独立领域数据，不访问持久化或修改源对象。
     *
     * @param source Web 表单
     * @return 领域数据，null 输入返回 null
     */
    SysDeptBO toSysDeptBO(SysDeptForm source);

    /**
     * 将领域结果转换为 Web 响应，保留字段级序列化契约。
     *
     * @param source 领域结果
     * @return Web 视图，null 输入返回 null
     */
    SysDeptVO toSysDeptVO(SysDeptBO source);

    /**
     * 将 Web 入参转换为独立领域数据，不访问持久化或修改源对象。
     *
     * @param source Web 表单
     * @return 领域数据，null 输入返回 null
     */
    SysDictDataBO toSysDictDataBO(SysDictDataForm source);

    /**
     * 将领域结果转换为 Web 响应，保留字段级序列化契约。
     *
     * @param source 领域结果
     * @return Web 视图，null 输入返回 null
     */
    SysDictDataVO toSysDictDataVO(SysDictDataBO source);

    /**
     * 将 Web 入参转换为独立领域数据，不访问持久化或修改源对象。
     *
     * @param source Web 表单
     * @return 领域数据，null 输入返回 null
     */
    SysDictTypeBO toSysDictTypeBO(SysDictTypeForm source);

    /**
     * 将领域结果转换为 Web 响应，保留字段级序列化契约。
     *
     * @param source 领域结果
     * @return Web 视图，null 输入返回 null
     */
    SysDictTypeVO toSysDictTypeVO(SysDictTypeBO source);

    /**
     * 将 Web 入参转换为独立领域数据，不访问持久化或修改源对象。
     *
     * @param source Web 表单
     * @return 领域数据，null 输入返回 null
     */
    SysMenuBO toSysMenuBO(SysMenuForm source);

    /**
     * 将领域结果转换为 Web 响应，保留字段级序列化契约。
     *
     * @param source 领域结果
     * @return Web 视图，null 输入返回 null
     */
    SysMenuVO toSysMenuVO(SysMenuBO source);

    /**
     * 将 Web 入参转换为独立领域数据，不访问持久化或修改源对象。
     *
     * @param source Web 表单
     * @return 领域数据，null 输入返回 null
     */
    SysRoleBO toSysRoleBO(SysRoleForm source);

    /**
     * 将领域结果转换为 Web 响应，保留字段级序列化契约。
     *
     * @param source 领域结果
     * @return Web 视图，null 输入返回 null
     */
    SysRoleVO toSysRoleVO(SysRoleBO source);

    /**
     * 将 Web 入参转换为独立领域数据，不访问持久化或修改源对象。
     *
     * @param source Web 表单
     * @return 领域数据，null 输入返回 null
     */
    SysUserBO toSysUserBO(SysUserForm source);

    /**
     * 将领域结果转换为 Web 响应，保留字段级序列化契约。
     *
     * @param source 领域结果
     * @return Web 视图，null 输入返回 null
     */
    SysUserVO toSysUserVO(SysUserBO source);

    /**
     * 将 Web 入参转换为独立领域数据，不访问持久化或修改源对象。
     *
     * @param source Web 表单
     * @return 领域数据，null 输入返回 null
     */
    SysJobBO toSysJobBO(SysJobForm source);

    /**
     * 将领域结果转换为 Web 响应，保留字段级序列化契约。
     *
     * @param source 领域结果
     * @return Web 视图，null 输入返回 null
     */
    SysJobVO toSysJobVO(SysJobBO source);

    /**
     * 将 Web 入参转换为独立领域数据，不访问持久化或修改源对象。
     *
     * @param source Web 表单
     * @return 领域数据，null 输入返回 null
     */
    SysJobLogBO toSysJobLogBO(SysJobLogForm source);

    /**
     * 将领域结果转换为 Web 响应，保留字段级序列化契约。
     *
     * @param source 领域结果
     * @return Web 视图，null 输入返回 null
     */
    SysJobLogVO toSysJobLogVO(SysJobLogBO source);

    /**
     * 将 Web 入参转换为独立领域数据，不访问持久化或修改源对象。
     *
     * @param source Web 表单
     * @return 领域数据，null 输入返回 null
     */
    SysConfigBO toSysConfigBO(SysConfigForm source);

    /**
     * 将领域结果转换为 Web 响应，保留字段级序列化契约。
     *
     * @param source 领域结果
     * @return Web 视图，null 输入返回 null
     */
    SysConfigVO toSysConfigVO(SysConfigBO source);

    /**
     * 将 Web 入参转换为独立领域数据，不访问持久化或修改源对象。
     *
     * @param source Web 表单
     * @return 领域数据，null 输入返回 null
     */
    SysLogininforBO toSysLogininforBO(SysLogininforForm source);

    /**
     * 将领域结果转换为 Web 响应，保留字段级序列化契约。
     *
     * @param source 领域结果
     * @return Web 视图，null 输入返回 null
     */
    SysLogininforVO toSysLogininforVO(SysLogininforBO source);

    /**
     * 将 Web 入参转换为独立领域数据，不访问持久化或修改源对象。
     *
     * @param source Web 表单
     * @return 领域数据，null 输入返回 null
     */
    SysNoticeBO toSysNoticeBO(SysNoticeForm source);

    /**
     * 将领域结果转换为 Web 响应，保留字段级序列化契约。
     *
     * @param source 领域结果
     * @return Web 视图，null 输入返回 null
     */
    SysNoticeVO toSysNoticeVO(SysNoticeBO source);

    /**
     * 将 Web 入参转换为独立领域数据，不访问持久化或修改源对象。
     *
     * @param source Web 表单
     * @return 领域数据，null 输入返回 null
     */
    SysOperLogBO toSysOperLogBO(SysOperLogForm source);

    /**
     * 将领域结果转换为 Web 响应，保留字段级序列化契约。
     *
     * @param source 领域结果
     * @return Web 视图，null 输入返回 null
     */
    SysOperLogVO toSysOperLogVO(SysOperLogBO source);

    /**
     * 将 Web 入参转换为独立领域数据，不访问持久化或修改源对象。
     *
     * @param source Web 表单
     * @return 领域数据，null 输入返回 null
     */
    SysUserRoleBO toSysUserRoleBO(SysUserRoleForm source);

    /**
     * 将领域结果转换为 Web 响应，保留字段级序列化契约。
     *
     * @param source 领域结果
     * @return Web 视图，null 输入返回 null
     */
    RouterMetaVO toRouterMetaVO(RouterMetaBO source);

    /**
     * 将领域结果转换为 Web 响应，保留字段级序列化契约。
     *
     * @param source 领域结果
     * @return Web 视图，null 输入返回 null
     */
    RouterVO toRouterVO(RouterBO source);

    /**
     * 将 Web 入参转换为独立领域数据，不访问持久化或修改源对象。
     *
     * @param source Web 表单
     * @return 领域数据，null 输入返回 null
     */
    LoginParam toLoginParam(LoginBodyForm source);

    /**
     * 将 Web 入参转换为独立领域数据，不访问持久化或修改源对象。
     *
     * @param source Web 表单
     * @return 领域数据，null 输入返回 null
     */
    RegisterParam toRegisterParam(RegisterBodyForm source);

}
