package com.ruoyi.web.controller.system;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.biz.system.SysRegisterBiz;
import com.ruoyi.web.convert.WebBoundaryConvert;
import com.ruoyi.web.convert.WebResponses;
import com.ruoyi.web.form.system.*;
import com.ruoyi.web.form.quartz.*;
import com.ruoyi.web.form.framework.*;

/**
 * HTTP 协议入口；业务调用只通过 Biz，保留既有权限和审计注解。
 */
@RestController
public class SysRegisterController extends BaseController {
    private final SysRegisterBiz biz;

    /**
     * 通过构造器接收应用入口，不在控制器内访问领域服务或数据库。
     *
     * @param biz 应用业务入口
     */
    public SysRegisterController(SysRegisterBiz biz) {
        this.biz = biz;
    }

    /**
     * 接收请求并保留原参数校验，调用应用层业务入口，再转换并返回原协议的响应。
     *
     * @param user user 请求参数或领域数据
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @PostMapping("/register")
    public AjaxResult register(@RequestBody RegisterBodyForm user) {
        biz.register(WebBoundaryConvert.INSTANCE.toRegisterParam(user));
        return AjaxResult.success();
    }
}
