package com.ruoyi.biz.system;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.framework.model.bo.RegisterParam;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.facade.ISysRegisterFacade;
import com.ruoyi.system.facade.ISysConfigFacade;
import org.springframework.stereotype.Component;
import com.ruoyi.system.model.bo.*;
import com.ruoyi.framework.model.bo.*;

/**
 * SysRegisterController 对应的应用编排，保留原业务执行顺序与响应语义。
 */
@Component
public class SysRegisterBiz {

    private final ISysRegisterFacade registerFacade;

    private final ISysConfigFacade configFacade;

    /**
     * 接收应用编排所需的领域 Facade 和基础设施，不在构造阶段访问外部系统。
     */
    public SysRegisterBiz(ISysRegisterFacade registerFacade, ISysConfigFacade configFacade) {
        this.registerFacade = registerFacade;
        this.configFacade = configFacade;
    }

    /**
     * register：按原顺序执行 register、isEmpty、业务动作等步骤，并保留原判断、返回及异常语义。
     *
     * @param user user 请求参数或领域数据
     */
    public void register(RegisterParam user) {
        if (!("true".equals(configFacade.queryConfigByKey("sys.account.registerUser")))) {
            throw new ServiceException("当前系统没有开启注册功能！");
        }
        String msg = registerFacade.register(user);
        if (StringUtils.isNotEmpty(msg)) {
            throw new ServiceException(msg);
        }
    }
}
