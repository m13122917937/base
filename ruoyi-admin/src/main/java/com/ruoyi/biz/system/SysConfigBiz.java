package com.ruoyi.biz.system;

import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;

import java.util.List;

import com.ruoyi.system.model.bo.SysConfigBO;
import com.ruoyi.system.facade.ISysConfigFacade;
import org.springframework.stereotype.Component;
import com.ruoyi.system.model.bo.*;
import com.ruoyi.framework.model.bo.*;

/**
 * SysConfigController 对应的应用编排，保留原业务执行顺序与响应语义。
 */
@Component
public class SysConfigBiz {

    private final ISysConfigFacade configFacade;

    /**
     * 接收应用编排所需的领域 Facade 和基础设施，不在构造阶段访问外部系统。
     */
    public SysConfigBiz(ISysConfigFacade configFacade) {
        this.configFacade = configFacade;
    }

    /**
     * 传递显式分页参数，不创建线程分页状态；保留原业务校验顺序。
     * 获取参数配置列表：按原顺序执行 selectConfigList、返回列表等步骤，并保留原判断、返回及异常语义。
     *
     * @param config config 请求参数或领域数据
     * @param pageDomain 通用分页参数，显式传给 Facade，由 Service 启动分页
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public List<SysConfigBO> list(SysConfigBO config, PageDomain pageDomain) {
        List<SysConfigBO> list = configFacade.queryConfigList(config, pageDomain);
        return list;
    }

    /**
     * export：按原顺序执行 selectConfigList、返回导出数据 等步骤，并保留原判断、返回及异常语义。
     *
     * @param config config 请求参数或领域数据
     * @return 当前过滤条件和数据权限范围内的导出数据，由 Web 层编码为 Excel
     */
    public List<SysConfigBO> export(SysConfigBO config) {
        return configFacade.queryConfigList(config);
    }

    /**
     * 根据参数编号获取详细信息：按原顺序执行 selectConfigById 等步骤，并保留原判断、返回及异常语义。
     *
     * @param configId configId 请求参数或领域数据
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public SysConfigBO getInfo(Long configId) {
        return configFacade.queryConfigById(configId);
    }

    /**
     * 根据参数键名查询参数值：按原顺序执行 selectConfigByKey 等步骤，并保留原判断、返回及异常语义。
     *
     * @param configKey configKey 请求参数或领域数据
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public String getConfigKey(String configKey) {
        return configFacade.queryConfigByKey(configKey);
    }

    /**
     * 新增参数配置：按原顺序执行 checkConfigKeyUnique、getConfigName、setCreateBy、getUsername、insertConfig 等步骤，并保留原判断、返回及异常语义。
     *
     * @param config config 请求参数或领域数据
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public int add(SysConfigBO config) {
        if (!configFacade.checkConfigKeyUnique(config)) {
            throw new ServiceException("新增参数'" + config.getConfigName() + "'失败，参数键名已存在");
        }
        config.setCreateBy(SecurityUtils.getUsername());
        return configFacade.createConfig(config);
    }

    /**
     * 修改参数配置：按原顺序执行 checkConfigKeyUnique、getConfigName、setUpdateBy、getUsername、updateConfig 等步骤，并保留原判断、返回及异常语义。
     *
     * @param config config 请求参数或领域数据
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public int edit(SysConfigBO config) {
        if (!configFacade.checkConfigKeyUnique(config)) {
            throw new ServiceException("修改参数'" + config.getConfigName() + "'失败，参数键名已存在");
        }
        config.setUpdateBy(SecurityUtils.getUsername());
        return configFacade.modifyConfig(config);
    }

    /**
     * 删除参数配置：按原顺序执行 deleteConfigByIds、业务动作等步骤，并保留原判断、返回及异常语义。
     *
     * @param configIds configIds 请求参数或领域数据
     */
    public void remove(Long[] configIds) {
        configFacade.removeConfigByIds(configIds);
    }

    /**
     * 刷新参数缓存：按原顺序执行 resetConfigCache、业务动作等步骤，并保留原判断、返回及异常语义。
     */
    public void refreshCache() {
        configFacade.resetConfigCache();
    }
}
