package com.ruoyi.web.vo.system;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * 路由配置信息
 *
 * @author ruoyi
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RouterVO {
    /**
     * 路由名字
     */
    private String name;

    /**
     * 路由地址
     */
    private String path;

    /**
     * 是否隐藏路由，当设置 true 的时候该路由不会再侧边栏出现
     */
    private boolean hidden;

    /**
     * 重定向地址，当设置 noRedirect 的时候该路由在面包屑导航中不可被点击
     */
    private String redirect;

    /**
     * 组件地址
     */
    private String component;

    /**
     * 路由参数：如 {"id": 1, "name": "ry"}
     */
    private String query;

    /**
     * 当你一个路由下面的 children 声明的路由大于1个时，自动会变成嵌套的模式--如组件页面
     */
    private Boolean alwaysShow;

    /**
     * 其他元素
     */
    private RouterMetaVO meta;

    /**
     * 子路由
     */
    private List<RouterVO> children;

    /**
     * 读取并返回当前对象的 Name 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public String getName() {
        return name;
    }

    /**
     * 将参数 name 写入当前对象的 Name 属性，不执行持久化。
     *
     * @param name name 请求参数或领域数据
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 读取并返回当前对象的 Path 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
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
     * 读取并返回当前对象的 Hidden 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public boolean getHidden() {
        return hidden;
    }

    /**
     * 将参数 hidden 写入当前对象的 Hidden 属性，不执行持久化。
     *
     * @param hidden hidden 请求参数或领域数据
     */
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    /**
     * 读取并返回当前对象的 Redirect 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public String getRedirect() {
        return redirect;
    }

    /**
     * 将参数 redirect 写入当前对象的 Redirect 属性，不执行持久化。
     *
     * @param redirect redirect 请求参数或领域数据
     */
    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    /**
     * 读取并返回当前对象的 Component 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
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
     * 读取并返回当前对象的 AlwaysShow 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public Boolean getAlwaysShow() {
        return alwaysShow;
    }

    /**
     * 将参数 alwaysShow 写入当前对象的 AlwaysShow 属性，不执行持久化。
     *
     * @param alwaysShow alwaysShow 请求参数或领域数据
     */
    public void setAlwaysShow(Boolean alwaysShow) {
        this.alwaysShow = alwaysShow;
    }

    /**
     * 读取并返回当前对象的 Meta 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public RouterMetaVO getMeta() {
        return meta;
    }

    /**
     * 将参数 meta 写入当前对象的 Meta 属性，不执行持久化。
     *
     * @param meta meta 请求参数或领域数据
     */
    public void setMeta(RouterMetaVO meta) {
        this.meta = meta;
    }

    /**
     * 读取并返回当前对象的 Children 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public List<RouterVO> getChildren() {
        return children;
    }

    /**
     * 将参数 children 写入当前对象的 Children 属性，不执行持久化。
     *
     * @param children children 请求参数或领域数据
     */
    public void setChildren(List<RouterVO> children) {
        this.children = children;
    }
}
