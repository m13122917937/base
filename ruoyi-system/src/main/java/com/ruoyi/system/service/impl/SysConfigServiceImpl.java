package com.ruoyi.system.service.impl;

import java.util.Date;
import com.ruoyi.system.model.query.SysConfigQuery;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.mybatis.DynamicCondition;
import com.ruoyi.common.mybatis.PersistenceChecks;
import com.ruoyi.system.convert.PersistenceQueryConvert;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.common.core.page.PageDomain;
import java.util.Collection;
import java.util.List;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.constant.CacheConstants;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.core.text.Convert;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.mapper.SysConfigMapper;
import com.ruoyi.system.service.ISysConfigService;

/**
 * 参数配置 服务层实现
 *
 * @author ruoyi
 */
@Service
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements ISysConfigService {
    @Autowired
    private RedisCache redisCache;

    /**
     * 项目启动时，初始化参数到缓存
     */
    @PostConstruct
    public void init() {
        loadingConfigCache();
    }

    /**
     * 使用绑定参数的通用 getOne 查询匹配记录，保持既有唯一性判断。
     * 查询参数配置信息
     *
     * @param configId 参数配置ID
     * @return 参数配置信息
     */
    @Override
    public SysConfig selectConfigById(Long configId) {
        return getById(configId);
    }

    /**
     * 使用绑定参数的通用 getOne 查询匹配记录，保持既有唯一性判断。
     * 根据键名查询参数配置信息
     *
     * @param configKey 参数key
     * @return 参数键值
     */
    @Override
    public String selectConfigByKey(String configKey) {
        String configValue = Convert.toStr(redisCache.getCacheObject(getCacheKey(configKey)));
        if (StringUtils.isNotEmpty(configValue)) {
            return configValue;
        }
        SysConfig config = new SysConfig();
        config.setConfigKey(configKey);
        SysConfig retConfig = getOne(new QueryWrapper<SysConfig>().eq(config.getConfigId() != null, "config_id", config.getConfigId()).eq(config.getConfigKey() != null && !config.getConfigKey().isEmpty(), "config_key", config.getConfigKey()));
        if (StringUtils.isNotNull(retConfig)) {
            redisCache.setCacheObject(getCacheKey(configKey), retConfig.getConfigValue());
            return retConfig.getConfigValue();
        }
        return StringUtils.EMPTY;
    }

    /**
     * 将允许的筛选项转换为 Query，经 DynamicCondition 构造条件，再调用通用 list；查询异常原样抛出。
     * 查询参数配置列表
     *
     * @param config 参数配置信息
     * @return 参数配置集合
     */
    @Override
    public List<SysConfig> selectConfigList(SysConfig config) {
        return list(queryConditions(config));
    }

    /**
     * 将允许的筛选项转换为 Query，经 DynamicCondition 构造条件，再调用通用 list；查询异常原样抛出。
     * 在目标 Mapper 查询前启动分页并在 finally 中清理线程状态，保留原过滤条件及数据权限。
     * 查询失败时异常原样向上传递，不新增事务；null 分页参数表示全量查询。
     *
     * @param config 原查询条件
     * @param pageDomain 通用分页参数，不读取 Servlet 请求
     * @return 保留 Page 元数据的查询列表
     */
    @Override
    public List<SysConfig> selectConfigList(SysConfig config, PageDomain pageDomain) {
        return PageUtils.startPage(pageDomain, () -> list(queryConditions(config)));
    }

    /**
     * 按原字段策略调用通用 save，回填自增主键；创建/日志时间使用服务端当前时间。
     * 新增参数配置
     *
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public int insertConfig(SysConfig config) {
        int row = insertRecord(config);
        if (row > 0) {
            redisCache.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
        return row;
    }

    /**
     * 按原接口允许的列调用通用 update，保留空值策略及数据库更新时间，主键为空拒绝执行。
     * 修改参数配置
     *
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public int updateConfig(SysConfig config) {
        SysConfig temp = getById(config.getConfigId());
        if (!StringUtils.equals(temp.getConfigKey(), config.getConfigKey())) {
            redisCache.deleteObject(getCacheKey(temp.getConfigKey()));
        }

        int row = updateRecord(config);
        if (row > 0) {
            redisCache.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
        return row;
    }

    /**
     * 批量删除参数信息
     *
     * @param configIds 需要删除的参数ID
     */
    @Override
    public void deleteConfigByIds(Long[] configIds) {
        PersistenceChecks.ids(configIds);
        for (Long configId : configIds) {
            SysConfig config = selectConfigById(configId);
            if (StringUtils.equals(UserConstants.YES, config.getConfigType())) {
                throw new ServiceException(String.format("内置参数【%1$s】不能删除 ", config.getConfigKey()));
            }
            baseMapper.deleteById(PersistenceChecks.id(configId));
            redisCache.deleteObject(getCacheKey(config.getConfigKey()));
        }
    }

    /**
     * 将允许的筛选项转换为 Query，经 DynamicCondition 构造条件，再调用通用 list；查询异常原样抛出。
     * 加载参数缓存数据
     */
    @Override
    public void loadingConfigCache() {
        List<SysConfig> configsList = list(queryConditions(new SysConfig()));
        for (SysConfig config : configsList) {
            redisCache.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
    }

    /**
     * 清空参数缓存数据
     */
    @Override
    public void clearConfigCache() {
        Collection<String> keys = redisCache.keys(CacheConstants.SYS_CONFIG_KEY + "*");
        redisCache.deleteObject(keys);
    }

    /**
     * 重置参数缓存数据
     */
    @Override
    public void resetConfigCache() {
        clearConfigCache();
        loadingConfigCache();
    }

    /**
     * 使用绑定参数查询同键名配置，排除当前 ID；存在其他记录时返回不唯一，查询异常向上传递。
     * 校验参数键名是否唯一
     *
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public boolean checkConfigKeyUnique(SysConfig config) {
        return !exists(new QueryWrapper<SysConfig>().eq("config_key", config.getConfigKey())
                .ne(config.getConfigId() != null, "config_id", config.getConfigId()));
    }

    /**
     * 设置cache key
     *
     * @param configKey 参数键
     * @return 缓存键key
     */
    private String getCacheKey(String configKey) {
        return CacheConstants.SYS_CONFIG_KEY + configKey;
    }

    /** 主键非空时调用通用 updateById；实体元数据保留原列的空值策略、只读审计列和数据库更新时间。 */
    private int updateRecord(SysConfig value) {
        PersistenceChecks.id(value.getConfigId());
        return updateById(value) ? 1 : 0;
    }

    /** 显式设置新增时间为服务端当前时间，再按字段插入策略调用通用 save，返回单行写入结果。 */
    private int insertRecord(SysConfig value) {
        value.setCreateTime(new Date());
        value.setConfigId(null);
        return save(value) ? 1 : 0;
    }

    /** 转换原列表允许的筛选项，再由 DynamicCondition 构造参数化条件，保留日期和排序规则。 */
    private QueryWrapper<SysConfig> queryConditions(SysConfig source) {
        SysConfigQuery query = source == null ? new SysConfigQuery() : PersistenceQueryConvert.INSTANCE.toSysConfigQuery(source);
        QueryWrapper<SysConfig> wrapper = DynamicCondition.toWrapper(query);
        wrapper.apply(query.getBeginTime() != null && !"".equals(query.getBeginTime()), "DATE_FORMAT(create_time, '%Y%m%d') >= DATE_FORMAT({0}, '%Y%m%d')", query.getBeginTime());
        wrapper.apply(query.getEndTime() != null && !"".equals(query.getEndTime()), "DATE_FORMAT(create_time, '%Y%m%d') <= DATE_FORMAT({0}, '%Y%m%d')", query.getEndTime());
        return wrapper;
    }
}
