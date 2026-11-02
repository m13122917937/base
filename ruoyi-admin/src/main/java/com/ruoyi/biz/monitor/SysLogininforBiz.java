package com.ruoyi.biz.monitor;

import com.ruoyi.common.core.page.PageDomain;
import java.util.List;

import com.ruoyi.system.model.bo.SysLogininforBO;
import com.ruoyi.system.facade.ISysLogininforFacade;
import org.springframework.stereotype.Component;
import com.ruoyi.system.model.bo.*;
import com.ruoyi.framework.model.bo.*;

/**
 * SysLogininforController 对应的应用编排，保留原业务执行顺序与响应语义。
 */
@Component
public class SysLogininforBiz {

    private final ISysLogininforFacade logininforFacade;

    /**
     * 接收应用编排所需的领域 Facade 和基础设施，不在构造阶段访问外部系统。
     */
    public SysLogininforBiz(ISysLogininforFacade logininforFacade) {
        this.logininforFacade = logininforFacade;
    }

    /**
     * 传递显式分页参数，不创建线程分页状态；保留原业务校验顺序。
     * list：按原顺序执行 selectLogininforList、返回列表等步骤，并保留原判断、返回及异常语义。
     *
     * @param logininfor logininfor 请求参数或领域数据
     * @param pageDomain 通用分页参数，显式传给 Facade，由 Service 启动分页
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public List<SysLogininforBO> list(SysLogininforBO logininfor, PageDomain pageDomain) {
        List<SysLogininforBO> list = logininforFacade.queryLogininforList(logininfor, pageDomain);
        return list;
    }

    /**
     * export：按原顺序执行 selectLogininforList、返回导出数据 等步骤，并保留原判断、返回及异常语义。
     *
     * @param logininfor logininfor 请求参数或领域数据
     * @return 当前过滤条件和数据权限范围内的导出数据，由 Web 层编码为 Excel
     */
    public List<SysLogininforBO> export(SysLogininforBO logininfor) {
        return logininforFacade.queryLogininforList(logininfor);
    }

    /**
     * remove：按原顺序执行 deleteLogininforByIds 等步骤，并保留原判断、返回及异常语义。
     *
     * @param infoIds infoIds 请求参数或领域数据
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public int remove(Long[] infoIds) {
        return logininforFacade.removeLogininforByIds(infoIds);
    }

    /**
     * clean：按原顺序执行 cleanLogininfor、业务动作等步骤，并保留原判断、返回及异常语义。
     */
    public void clean() {
        logininforFacade.cleanLogininfor();
    }

}
