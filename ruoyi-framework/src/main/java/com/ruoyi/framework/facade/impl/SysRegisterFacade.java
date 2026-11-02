package com.ruoyi.framework.facade.impl;

import org.springframework.stereotype.Component;
import com.ruoyi.framework.model.bo.RegisterParam;
import com.ruoyi.framework.convert.FrameworkBoundaryConvert;
import com.ruoyi.system.model.bo.*;
import com.ruoyi.framework.model.bo.*;
import com.ruoyi.framework.web.service.SysRegisterService;
import com.ruoyi.framework.facade.ISysRegisterFacade;

/**
 * 本领域的边界转换和 Service 编排，不声明事务。
 */
@Component
public class SysRegisterFacade implements ISysRegisterFacade {
    private final SysRegisterService service;

    /**
     * 接收本领域 Service，建立唯一持久化调用路径，不执行查询或写入。
     *
     * @param service 本领域业务服务
     */
    public SysRegisterFacade(SysRegisterService service) {
        this.service = service;
    }

    /**
     * register：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param registerBody registerBody 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public String register(RegisterParam registerBody) {
        return service.register(FrameworkBoundaryConvert.INSTANCE.toRegisterBody(registerBody));
    }
}
