package com.ruoyi.web.controller.system;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.biz.system.SysLoginBiz;
import com.ruoyi.web.convert.WebBoundaryConvert;
import com.ruoyi.web.convert.WebResponses;
import com.ruoyi.web.form.system.*;
import com.ruoyi.web.form.quartz.*;
import com.ruoyi.web.form.framework.*;

/**
 * HTTP 协议入口；业务调用只通过 Biz，保留既有权限和审计注解。
 */
@RestController
public class SysLoginController {
    private final SysLoginBiz biz;

    /**
     * 通过构造器接收应用入口，不在控制器内访问领域服务或数据库。
     *
     * @param biz 应用业务入口
     */
    public SysLoginController(SysLoginBiz biz) {
        this.biz = biz;
    }

    /**
     * 接收请求并保留原参数校验，调用应用层业务入口，再转换并返回原协议的响应。
     *
     * @param loginBody loginBody 请求参数或领域数据
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @PostMapping("/login")
    public AjaxResult login(@RequestBody LoginBodyForm loginBody) {
        return WebResponses.withFields(biz.login(WebBoundaryConvert.INSTANCE.toLoginParam(loginBody)));
    }

    /**
     * 接收请求并保留原参数校验，调用应用层业务入口，再转换并返回原协议的响应。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @GetMapping("getInfo")
    public AjaxResult getInfo() {
        return WebResponses.withFields(biz.getInfo());
    }

    /**
     * 接收请求并保留原参数校验，调用应用层业务入口，再转换并返回原协议的响应。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @GetMapping("getRouters")
    public AjaxResult getRouters() {
        return WebResponses.toWeb(AjaxResult.success(biz.getRouters()));
    }
}
