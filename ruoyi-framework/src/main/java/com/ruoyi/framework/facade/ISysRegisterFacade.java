package com.ruoyi.framework.facade;

import com.ruoyi.framework.model.bo.RegisterParam;
import com.ruoyi.system.model.bo.*;
import com.ruoyi.framework.model.bo.*;

/**
 * 稳定领域能力入口，隐藏 Service、Entity 和 Mapper。
 */
public interface ISysRegisterFacade {
    /**
     * register：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param registerBody registerBody 领域参数
     * @return 原领域能力的处理结果
     */
    String register(RegisterParam registerBody);
}
