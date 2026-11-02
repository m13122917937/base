package com.ruoyi.system.model.bo;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 菜单权限表 sys_menu
 *
 * @author ruoyi
 */
public class SysMenuBO extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 菜单ID
     */
    private Long menuId;

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 父菜单名称
     */
    private String parentName;

    /**
     * 父菜单ID
     */
    private Long parentId;

    /**
     * 显示顺序
     */
    private Integer orderNum;

    /**
     * 路由地址
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 路由参数
     */
    private String query;

    /**
     * 路由名称，默认和路由地址相同的驼峰格式（注意：因为vue3版本的router会删除名称相同路由，为避免名字的冲突，特殊情况可以自定义）
     */
    private String routeName;

    /**
     * 是否为外链（0是 1否）
     */
    private String isFrame;

    /**
     * 是否缓存（0缓存 1不缓存）
     */
    private String isCache;

    /**
     * 类型（M目录 C菜单 F按钮）
     */
    private String menuType;

    /**
     * 显示状态（0显示 1隐藏）
     */
    private String visible;

    /**
     * 菜单状态（0正常 1停用）
     */
    private String status;

    /**
     * 权限字符串
     */
    private String perms;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 子菜单
     */
    private List<SysMenuBO> children = new ArrayList<SysMenuBO>();

    /**
     * 读取并返回当前对象的 MenuId 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public Long getMenuId() {
        return menuId;
    }

    /**
     * 将参数 menuId 写入当前对象的 MenuId 属性，不执行持久化。
     *
     * @param menuId menuId 请求参数或领域数据
     */
    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    /**
     * 读取并返回当前对象的 MenuName 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @NotBlank(message = "菜单名称不能为空")
    @Size(min = 0, max = 50, message = "菜单名称长度不能超过50个字符")
    public String getMenuName() {
        return menuName;
    }

    /**
     * 将参数 menuName 写入当前对象的 MenuName 属性，不执行持久化。
     *
     * @param menuName menuName 请求参数或领域数据
     */
    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    /**
     * 读取并返回当前对象的 ParentName 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public String getParentName() {
        return parentName;
    }

    /**
     * 将参数 parentName 写入当前对象的 ParentName 属性，不执行持久化。
     *
     * @param parentName parentName 请求参数或领域数据
     */
    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    /**
     * 读取并返回当前对象的 ParentId 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public Long getParentId() {
        return parentId;
    }

    /**
     * 将参数 parentId 写入当前对象的 ParentId 属性，不执行持久化。
     *
     * @param parentId parentId 请求参数或领域数据
     */
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    /**
     * 读取并返回当前对象的 OrderNum 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @NotNull(message = "显示顺序不能为空")
    public Integer getOrderNum() {
        return orderNum;
    }

    /**
     * 将参数 orderNum 写入当前对象的 OrderNum 属性，不执行持久化。
     *
     * @param orderNum orderNum 请求参数或领域数据
     */
    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    /**
     * 读取并返回当前对象的 Path 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @Size(min = 0, max = 200, message = "路由地址不能超过200个字符")
    public String getPath() {
        return path;
    }

    /**
     * 将参数 path 写入当前对象的 Path 属性，不执行持久化。
     *
     * @param path path 请求参数或领域数据
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 读取并返回当前对象的 Component 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @Size(min = 0, max = 200, message = "组件路径不能超过255个字符")
    public String getComponent() {
        return component;
    }

    /**
     * 将参数 component 写入当前对象的 Component 属性，不执行持久化。
     *
     * @param component component 请求参数或领域数据
     */
    public void setComponent(String component) {
        this.component = component;
    }

    /**
     * 读取并返回当前对象的 Query 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public String getQuery() {
        return query;
    }

    /**
     * 将参数 query 写入当前对象的 Query 属性，不执行持久化。
     *
     * @param query query 请求参数或领域数据
     */
    public void setQuery(String query) {
        this.query = query;
    }

    /**
     * 读取并返回当前对象的 RouteName 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public String getRouteName() {
        return routeName;
    }

    /**
     * 将参数 routeName 写入当前对象的 RouteName 属性，不执行持久化。
     *
     * @param routeName routeName 请求参数或领域数据
     */
    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    /**
     * 读取并返回当前对象的 IsFrame 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public String getIsFrame() {
        return isFrame;
    }

    /**
     * 将参数 isFrame 写入当前对象的 IsFrame 属性，不执行持久化。
     *
     * @param isFrame isFrame 请求参数或领域数据
     */
    public void setIsFrame(String isFrame) {
        this.isFrame = isFrame;
    }

    /**
     * 读取并返回当前对象的 IsCache 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public String getIsCache() {
        return isCache;
    }

    /**
     * 将参数 isCache 写入当前对象的 IsCache 属性，不执行持久化。
     *
     * @param isCache isCache 请求参数或领域数据
     */
    public void setIsCache(String isCache) {
        this.isCache = isCache;
    }

    /**
     * 读取并返回当前对象的 MenuType 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @NotBlank(message = "菜单类型不能为空")
    public String getMenuType() {
        return menuType;
    }

    /**
     * 将参数 menuType 写入当前对象的 MenuType 属性，不执行持久化。
     *
     * @param menuType menuType 请求参数或领域数据
     */
    public void setMenuType(String menuType) {
        this.menuType = menuType;
    }

    /**
     * 读取并返回当前对象的 Visible 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public String getVisible() {
        return visible;
    }

    /**
     * 将参数 visible 写入当前对象的 Visible 属性，不执行持久化。
     *
     * @param visible visible 请求参数或领域数据
     */
    public void setVisible(String visible) {
        this.visible = visible;
    }

    /**
     * 读取并返回当前对象的 Status 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public String getStatus() {
        return status;
    }

    /**
     * 将参数 status 写入当前对象的 Status 属性，不执行持久化。
     *
     * @param status status 请求参数或领域数据
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 读取并返回当前对象的 Perms 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @Size(min = 0, max = 100, message = "权限标识长度不能超过100个字符")
    public String getPerms() {
        return perms;
    }

    /**
     * 将参数 perms 写入当前对象的 Perms 属性，不执行持久化。
     *
     * @param perms perms 请求参数或领域数据
     */
    public void setPerms(String perms) {
        this.perms = perms;
    }

    /**
     * 读取并返回当前对象的 Icon 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public String getIcon() {
        return icon;
    }

    /**
     * 将参数 icon 写入当前对象的 Icon 属性，不执行持久化。
     *
     * @param icon icon 请求参数或领域数据
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * 读取并返回当前对象的 Children 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public List<SysMenuBO> getChildren() {
        return children;
    }

    /**
     * 将参数 children 写入当前对象的 Children 属性，不执行持久化。
     *
     * @param children children 请求参数或领域数据
     */
    public void setChildren(List<SysMenuBO> children) {
        this.children = children;
    }

}
