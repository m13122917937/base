package com.ruoyi.biz.system;

import com.ruoyi.common.exception.ServiceException;

import java.util.Map;

import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.system.model.bo.SysUserBO;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.facade.ISysUserFacade;
import org.springframework.stereotype.Component;
import com.ruoyi.system.model.bo.*;
import com.ruoyi.framework.model.bo.*;

/**
 * SysIndexController 对应的应用编排，保留原业务执行顺序与响应语义。
 */
@Component
public class SysIndexBiz {

    /**
     * 系统基础配置
     */
    private final RuoYiConfig ruoyiConfig;

    private final ISysUserFacade userFacade;

    /**
     * 接收应用编排所需的领域 Facade 和基础设施，不在构造阶段访问外部系统。
     */
    public SysIndexBiz(RuoYiConfig ruoyiConfig, ISysUserFacade userFacade) {
        this.ruoyiConfig = ruoyiConfig;
        this.userFacade = userFacade;
    }

    /**
     * 访问首页，提示语：按原顺序执行 format、getName、getVersion 等步骤，并保留原判断、返回及异常语义。
     *
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public String index() {
        return StringUtils.format("欢迎使用{}后台管理框架，当前版本：v{}，请通过前端地址访问。", ruoyiConfig.getName(), ruoyiConfig.getVersion());
    }

    /**
     * 解锁屏幕：按原顺序执行 get、isEmpty、getUsername、selectUserByUserName、matchesPassword、getPassword、业务动作等步骤，并保留原判断、返回及异常语义。
     *
     * @param body body 请求参数或领域数据
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public String unlockScreen(Map<String, String> body) {
        String password = body.get("password");
        if (StringUtils.isEmpty(password)) {
            throw new ServiceException("密码不能为空");
        }
        String username = SecurityUtils.getUsername();
        SysUserBO user = userFacade.queryUserByUserName(username);
        if (user == null) {
            throw new ServiceException("服务器超时，请重新登录");
        }
        if (!SecurityUtils.matchesPassword(password, user.getPassword())) {
            throw new ServiceException("密码错误，请重新输入");
        }

        return "解锁成功";
    }
}
