package com.ruoyi.system.facade.impl;

import com.ruoyi.common.core.page.PageDomain;
import java.util.List;

import com.ruoyi.system.model.bo.SysConfigBO;
import com.ruoyi.system.convert.BoundaryConvert;
import com.ruoyi.common.utils.BoundaryPages;
import com.ruoyi.system.model.bo.*;
import org.springframework.stereotype.Component;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.system.facade.ISysConfigFacade;

/**
 * 本领域的边界转换和 Service 编排，不声明事务。
 */
@Component
public class SysConfigFacade implements ISysConfigFacade {
    private final ISysConfigService service;

    /**
     * 接收本领域 Service，建立唯一持久化调用路径，不执行查询或写入。
     *
     * @param service 本领域业务服务
     */
    public SysConfigFacade(ISysConfigService service) {
        this.service = service;
    }

    /**
     * selectConfigById：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param configId configId 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public SysConfigBO queryConfigById(Long configId) {
        return BoundaryConvert.INSTANCE.toSysConfigBO(service.selectConfigById(configId));
    }

    /**
     * selectConfigByKey：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param configKey configKey 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public String queryConfigByKey(String configKey) {
        return service.selectConfigByKey(configKey);
    }

    /**
     * selectConfigList：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param config config 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public List<SysConfigBO> queryConfigList(SysConfigBO config) {
        return BoundaryPages.map(service.selectConfigList(BoundaryConvert.INSTANCE.toSysConfig(config)), BoundaryConvert.INSTANCE::toSysConfigBOList);
    }

    /**
     * 将显式分页参数传给本领域 Service，并在边界转换时保留数据库总数。
     * 查询失败时异常原样向上传递，不新增事务；null 分页参数表示全量查询。
     *
     * @param config 原查询条件
     * @param pageDomain 通用分页参数，不读取 Servlet 请求
     * @return 保留 Page 元数据的查询列表
     */
    @Override
    public List<SysConfigBO> queryConfigList(SysConfigBO config, PageDomain pageDomain) {
        return BoundaryPages.map(service.selectConfigList(BoundaryConvert.INSTANCE.toSysConfig(config), pageDomain), BoundaryConvert.INSTANCE::toSysConfigBOList);
    }

    /**
     * insertConfig：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param config config 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public int createConfig(SysConfigBO config) {
        return service.insertConfig(BoundaryConvert.INSTANCE.toSysConfig(config));
    }

    /**
     * updateConfig：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param config config 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public int modifyConfig(SysConfigBO config) {
        return service.updateConfig(BoundaryConvert.INSTANCE.toSysConfig(config));
    }

    /**
     * deleteConfigByIds：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param configIds configIds 领域参数
     */
    @Override
    public void removeConfigByIds(Long[] configIds) {
        service.deleteConfigByIds(configIds);
    }

    /**
     * resetConfigCache：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     */
    @Override
    public void resetConfigCache() {
        service.resetConfigCache();
    }

    /**
     * checkConfigKeyUnique：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param config config 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public boolean checkConfigKeyUnique(SysConfigBO config) {
        return service.checkConfigKeyUnique(BoundaryConvert.INSTANCE.toSysConfig(config));
    }
}
