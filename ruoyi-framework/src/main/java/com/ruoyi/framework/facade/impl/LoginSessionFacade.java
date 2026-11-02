package com.ruoyi.framework.facade.impl;

import org.springframework.stereotype.Component;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.framework.facade.ILoginSessionFacade;
import com.ruoyi.system.convert.BoundaryConvert;
import com.ruoyi.system.model.bo.LoginSessionBO;

/**
 * 适配已有身份基础设施；Redis 继续保存原 LoginUser，不迁移线上缓存。
 */
@Component
public class LoginSessionFacade implements ILoginSessionFacade {
    private final RedisCache redisCache;

    /**
     * 接收现有 Redis 客户端，在构造阶段不读取或修改缓存。
     *
     * @param redisCache 已配置的身份缓存客户端
     */
    public LoginSessionFacade(RedisCache redisCache) {
        this.redisCache = redisCache;
    }

    /**
     * 读取当前安全上下文中的 LoginUser，以 MapStruct 复制用户、权限及令牌元数据为 BO。
     *
     * @return 独立会话快照；未认证时沿用现有认证异常
     */
    @Override
    public LoginSessionBO currentSession() {
        return BoundaryConvert.INSTANCE.toLoginSessionBO(SecurityUtils.getLoginUser());
    }

    /**
     * 读取指定缓存键中的原 LoginUser，再转换为 BO，保持原缓存类型和 TTL 不变。
     *
     * @param key 完整在线会话缓存键
     * @return 会话快照；不存在时为 null
     */
    @Override
    public LoginSessionBO readSession(String key) {
        LoginUser session = redisCache.getCacheObject(key);
        return BoundaryConvert.INSTANCE.toLoginSessionBO(session);
    }
}
