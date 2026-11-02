package com.ruoyi.framework.facade;

import com.ruoyi.system.model.bo.LoginSessionBO;

/**
 * 身份基础设施的会话读取边界，不向 Biz 暴露缓存中的认证实体。
 */
public interface ILoginSessionFacade {
    /**
     * 从当前安全上下文读取会话，再转换为独立的会话 BO；认证失败沿用 SecurityUtils 异常。
     *
     * @return 当前请求的会话快照，不与缓存对象共享可变用户对象
     */
    LoginSessionBO currentSession();

    /**
     * 根据在线会话缓存键读取原认证对象，再转换为 BO；不修改缓存格式或过期时间。
     *
     * @param key 由应用层按现有令牌前缀取得的完整缓存键
     * @return 会话快照，缓存不存在时为 null
     */
    LoginSessionBO readSession(String key);
}
