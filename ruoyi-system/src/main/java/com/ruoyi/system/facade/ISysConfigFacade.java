package com.ruoyi.system.facade;

import com.ruoyi.common.core.page.PageDomain;
import java.util.List;

import com.ruoyi.system.model.bo.SysConfigBO;
import com.ruoyi.system.model.bo.*;

/**
 * 稳定领域能力入口，隐藏 Service、Entity 和 Mapper。
 */
public interface ISysConfigFacade {
    /**
     * selectConfigById：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param configId configId 领域参数
     * @return 原领域能力的处理结果
     */
    SysConfigBO queryConfigById(Long configId);

    /**
     * selectConfigByKey：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param configKey configKey 领域参数
     * @return 原领域能力的处理结果
     */
    String queryConfigByKey(String configKey);

    /**
     * selectConfigList：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param config config 领域参数
     * @return 原领域能力的处理结果
     */
    List<SysConfigBO> queryConfigList(SysConfigBO config);

    /**
     * 将显式分页参数传给本领域 Service，并在边界转换时保留数据库总数。
     * 查询失败时异常原样向上传递，不新增事务；null 分页参数表示全量查询。
     *
     * @param config 原查询条件
     * @param pageDomain 通用分页参数，不读取 Servlet 请求
     * @return 保留 Page 元数据的查询列表
     */
    List<SysConfigBO> queryConfigList(SysConfigBO config, PageDomain pageDomain);

    /**
     * insertConfig：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param config config 领域参数
     * @return 原领域能力的处理结果
     */
    int createConfig(SysConfigBO config);

    /**
     * updateConfig：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param config config 领域参数
     * @return 原领域能力的处理结果
     */
    int modifyConfig(SysConfigBO config);

    /**
     * deleteConfigByIds：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param configIds configIds 领域参数
     */
    void removeConfigByIds(Long[] configIds);

    /**
     * resetConfigCache：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     */
    void resetConfigCache();

    /**
     * checkConfigKeyUnique：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param config config 领域参数
     * @return 原领域能力的处理结果
     */
    boolean checkConfigKeyUnique(SysConfigBO config);
}
