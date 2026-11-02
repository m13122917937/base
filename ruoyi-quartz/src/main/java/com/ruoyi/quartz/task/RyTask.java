package com.ruoyi.quartz.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 定时任务调度测试
 *
 * @author ruoyi
 */
@Component("ryTask")
@Slf4j
public class RyTask {
    /**
     * 记录调度器传入的多种类型示例参数；仅输出执行日志，不访问数据库或外部服务。
     */
    public void ryMultipleParams(String s, Boolean b, Long l, Double d, Integer i) {
        log.info("执行多参方法： 字符串类型{}，布尔类型{}，长整型{}，浮点型{}，整形{}", s, b, l, d, i);
    }

    /**
     * 记录调度器传入的示例字符串参数；仅输出执行日志，不修改业务状态。
     */
    public void ryParams(String params) {
        log.info("执行有参方法：{}", params);
    }

    /**
     * 记录无参示例任务的执行日志，不修改业务状态。
     */
    public void ryNoParams() {
        log.info("执行无参方法");
    }
}
