package com.ruoyi.architecture;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.framework.config.JacksonJsonRedisSerializer;
import com.ruoyi.framework.config.RedisConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.ByteArrayCodec;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.redisson.spring.starter.RedissonAutoConfigurationV2;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 对显式提供端口的隔离 Redis 执行真实客户端测试，默认不接触本机或业务 Redis。
 */
@EnabledIfSystemProperty(named = "redis.test.port", matches = "\\d+")
class RedissonRedisIntegrationTest {
    /**
     * 使用实际 Redisson 自动连接，在隔离 Redis 中验证旧缓存字节、会话、TTL、集合、Hash 和 Lua 限流。
     * 连接端口由命令行 redis.test.port 显式提供，测试只操作 cf:redisson:test: 前缀，最终清理本次创建的键。
     */
    @Test
    @SuppressWarnings("unchecked")
    void worksWithExistingCacheAndRateLimiterOnRealRedis() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(JacksonAutoConfiguration.class, RedisAutoConfiguration.class, RedissonAutoConfigurationV2.class, CacheAutoConfiguration.class))
                .withUserConfiguration(RedisConfig.class, RedisCache.class)
                .withPropertyValues("spring.data.redis.host=127.0.0.1", "spring.data.redis.port=" + System.getProperty("redis.test.port"), "spring.data.redis.timeout=5s")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).hasSingleBean(RedissonClient.class);
                    assertThat(context).hasSingleBean(RedissonConnectionFactory.class);
                    RedisCache cache = context.getBean(RedisCache.class);
                    RedisTemplate<Object, Object> template = (RedisTemplate<Object, Object>) context.getBean("redisTemplate");
                    RedissonClient client = context.getBean(RedissonClient.class);
                    String prefix = "cf:redisson:test:";
                    try {
                        RedisSerializer<Object> serializer = new JacksonJsonRedisSerializer(com.ruoyi.common.utils.JsonUtils.mapper());
                        LoginUser session = new LoginUser(12L, 7L, new SysUser(12L), Set.of("system:user:list"));
                        session.setToken("fixture-token");
                        byte[] legacyBytes;
                        try (java.io.InputStream fixture = new org.springframework.core.io.ClassPathResource("json-cache/legacy-session.json").getInputStream()) {
                            legacyBytes = fixture.readAllBytes();
                        }
                        client.getBucket(prefix + "legacy", ByteArrayCodec.INSTANCE).set(legacyBytes);
                        LoginUser restored = cache.getCacheObject(prefix + "legacy");
                        assertEquals("fixture-token", restored.getToken());
                        cache.setCacheObject(prefix + "session", session, 30, TimeUnit.SECONDS);
                        assertArrayEquals(serializer.serialize(session), client.<byte[]>getBucket(prefix + "session", ByteArrayCodec.INSTANCE).get());
                        assertTrue(cache.getExpire(prefix + "session") > 0);
                        cache.setCacheObject(prefix + "text", "1234", 30, TimeUnit.SECONDS);
                        assertEquals("1234", cache.getCacheObject(prefix + "text"));
                        assertEquals(2, cache.setCacheList(prefix + "list", List.of("a", "b")));
                        assertEquals(List.of("a", "b"), cache.getCacheList(prefix + "list"));
                        cache.setCacheSet(prefix + "set", Set.of("a", "b"));
                        assertEquals(Set.of("a", "b"), cache.getCacheSet(prefix + "set"));
                        cache.setCacheMap(prefix + "hash", Map.of("field", "value"));
                        assertEquals("value", cache.getCacheMapValue(prefix + "hash", "field"));
                        RedisScript<Long> script = (RedisScript<Long>) context.getBean("limitScript");
                        assertEquals(1L, template.execute(script, List.of(prefix + "limit"), 2, 30));
                        assertEquals(2L, template.execute(script, List.of(prefix + "limit"), 2, 30));
                        assertEquals(3L, template.execute(script, List.of(prefix + "limit"), 2, 30));
                        assertTrue(cache.getExpire(prefix + "limit") > 0);
                    } finally {
                        cache.deleteObject(cache.keys(prefix + "*"));
                    }
                });
    }
}
