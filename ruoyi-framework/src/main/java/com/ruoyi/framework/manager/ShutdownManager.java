package com.ruoyi.framework.manager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import jakarta.annotation.PreDestroy;

/**
 * 确保应用退出时能关闭后台线程
 *
 * @author ruoyi
 */
@Component
@Slf4j(topic = "sys-user")
public class ShutdownManager {
    @PreDestroy
    public void destroy() {
        shutdownAsyncManager();
    }

    /**
     * 记录关闭提示后停止异步任务线程池；关闭失败时通过 sys-user 日志记录完整异常，不向销毁流程抛出。
     */
    private void shutdownAsyncManager() {
        try {
            log.info("====关闭后台任务任务线程池====");
            AsyncManager.me().shutdown();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
