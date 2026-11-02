package com.ruoyi.framework.facade.impl;

import org.springframework.stereotype.Component;
import com.ruoyi.system.model.bo.*;
import com.ruoyi.framework.model.bo.*;
import com.ruoyi.framework.web.service.SysLoginService;
import com.ruoyi.framework.facade.ISysLoginFacade;

/**
 * 本领域的边界转换和 Service 编排，不声明事务。
 */
@Component
public class SysLoginFacade implements ISysLoginFacade {
    private final SysLoginService service;

    /**
     * 接收本领域 Service，建立唯一持久化调用路径，不执行查询或写入。
     *
     * @param service 本领域业务服务
     */
    public SysLoginFacade(SysLoginService service) {
        this.service = service;
    }

    /**
     * 将账号密码交由登录服务认证，返回令牌；认证失败保留业务异常，不接收验证码。
     *
     * @param username username 领域参数
     * @param password password 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public String login(String username, String password) {
        return service.login(username, password);
    }
}
