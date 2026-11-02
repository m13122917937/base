package com.ruoyi.system.model.bo;

import com.ruoyi.common.utils.StringUtils;

/**
 * 路由显示信息
 *
 * @author ruoyi
 */
public class RouterMetaBO {
    /**
     * 设置该路由在侧边栏和面包屑中展示的名字
     */
    private String title;

    /**
     * 设置该路由的图标，对应路径src/assets/icons/svg
     */
    private String icon;

    /**
     * 设置为true，则不会被 <keep-alive>缓存
     */
    private boolean noCache;

    /**
     * 内链地址（http(s)://开头）
     */
    private String link;

    /**
     * 接收 默认初始化参数，初始化当前对象的依赖或属性；不执行持久化。
     */
    public RouterMetaBO() {
    }

    /**
     * 接收 title, icon，初始化当前对象的依赖或属性；不执行持久化。
     *
     * @param title title 请求参数或领域数据
     * @param icon  icon 请求参数或领域数据
     */
    public RouterMetaBO(String title, String icon) {
        this.title = title;
        this.icon = icon;
    }

    /**
     * 接收 title, icon, noCache，初始化当前对象的依赖或属性；不执行持久化。
     *
     * @param title   title 请求参数或领域数据
     * @param icon    icon 请求参数或领域数据
     * @param noCache noCache 请求参数或领域数据
     */
    public RouterMetaBO(String title, String icon, boolean noCache) {
        this.title = title;
        this.icon = icon;
        this.noCache = noCache;
    }

    /**
     * 接收 title, icon, link，初始化当前对象的依赖或属性；不执行持久化。
     *
     * @param title title 请求参数或领域数据
     * @param icon  icon 请求参数或领域数据
     * @param link  link 请求参数或领域数据
     */
    public RouterMetaBO(String title, String icon, String link) {
        this.title = title;
        this.icon = icon;
        this.link = link;
    }

    /**
     * 接收 title, icon, noCache, link，初始化当前对象的依赖或属性；不执行持久化。
     *
     * @param title   title 请求参数或领域数据
     * @param icon    icon 请求参数或领域数据
     * @param noCache noCache 请求参数或领域数据
     * @param link    link 请求参数或领域数据
     */
    public RouterMetaBO(String title, String icon, boolean noCache, String link) {
        this.title = title;
        this.icon = icon;
        this.noCache = noCache;
        if (StringUtils.ishttp(link)) {
            this.link = link;
        }
    }

    /**
     * isNoCache：按当前字段和入参执行判断或状态更新，保留原返回语义。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public boolean isNoCache() {
        return noCache;
    }

    /**
     * 将参数 noCache 写入当前对象的 NoCache 属性，不执行持久化。
     *
     * @param noCache noCache 请求参数或领域数据
     */
    public void setNoCache(boolean noCache) {
        this.noCache = noCache;
    }

    /**
     * 读取并返回当前对象的 Title 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public String getTitle() {
        return title;
    }

    /**
     * 将参数 title 写入当前对象的 Title 属性，不执行持久化。
     *
     * @param title title 请求参数或领域数据
     */
    public void setTitle(String title) {
        this.title = title;
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
     * 读取并返回当前对象的 Link 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public String getLink() {
        return link;
    }

    /**
     * 将参数 link 写入当前对象的 Link 属性，不执行持久化。
     *
     * @param link link 请求参数或领域数据
     */
    public void setLink(String link) {
        this.link = link;
    }
}
