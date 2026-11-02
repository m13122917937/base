package com.ruoyi.framework.facade;

import com.ruoyi.system.model.bo.*;
import com.ruoyi.framework.model.bo.*;

/**
 * 稳定领域能力入口，隐藏 Service、Entity 和 Mapper。
 */
public interface ISysLoginFacade {
    /**
     * 将账号密码交由登录服务认证，返回令牌；认证失败保留业务异常，不接收验证码。
     *
     * @param username username 领域参数
     * @param password password 领域参数
     * @return 原领域能力的处理结果
     */
    String login(String username, String password);
}
