package com.ruoyi.architecture;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.framework.config.JacksonJsonRedisSerializer;
import com.ruoyi.framework.config.RedisConfig;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.redisson.spring.starter.RedissonAutoConfigurationV2;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

/**
 * 检查自动配置真实选择 Redisson 连接工厂，并使用 Jackson 会话缓存协议。
 */
class RedissonConfigurationTest {
    /**
     * 使用模拟客户端启动实际 Spring 自动配置，确认唯一连接工厂来自 Redisson，缓存模板和 Lua 脚本均正常装配。
     * 客户端已有 Bean，自动配置不会创建网络连接，因此不依赖本机 Redis 服务。
     */
    @Test
    void usesRedissonConnectionFactoryAndExistingCacheTemplate() {
        RedissonClient client = mock(RedissonClient.class);
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(JacksonAutoConfiguration.class, RedisAutoConfiguration.class, RedissonAutoConfigurationV2.class, CacheAutoConfiguration.class))
                .withUserConfiguration(RedisConfig.class)
                .withBean(RedissonClient.class, () -> client)
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).hasSingleBean(RedisConnectionFactory.class);
                    assertInstanceOf(RedissonConnectionFactory.class, context.getBean(RedisConnectionFactory.class));
                    RedisTemplate<?, ?> template = context.getBean("redisTemplate", RedisTemplate.class);
                    assertSame(context.getBean(RedisConnectionFactory.class), template.getConnectionFactory());
                    assertInstanceOf(StringRedisSerializer.class, template.getKeySerializer());
                    assertInstanceOf(StringRedisSerializer.class, template.getHashKeySerializer());
                    assertInstanceOf(JacksonJsonRedisSerializer.class, template.getValueSerializer());
                    assertSame(template.getValueSerializer(), template.getHashValueSerializer());
                    assertThat(context).hasBean("limitScript");
                });
        assertThrows(ClassNotFoundException.class, () -> Class.forName("io.lettuce.core.RedisClient"));
    }

    /**
     * 将 LoginUser 用 Jackson 缓存序列化器往返转换，验证令牌、权限、类型和密码排除策略保持兼容。
     * 同时检查普通字符串和限流计数继续采用原字节表示。
     */
    @Test
    void preservesExistingSessionAndScalarWireFormat() {
        RedisSerializer<Object> serializer = new JacksonJsonRedisSerializer(com.ruoyi.common.utils.JsonUtils.mapper());
        SysUser user = new SysUser(12L);
        user.setUserName("fixture-user");
        user.setPassword("fixture-hash");
        LoginUser original = new LoginUser(12L, 7L, user, Set.of("system:user:list"));
        original.setToken("fixture-token");
        LoginUser restored = assertInstanceOf(LoginUser.class, serializer.deserialize(serializer.serialize(original)));
        assertEquals(original.getToken(), restored.getToken());
        assertEquals(original.getPermissions(), restored.getPermissions());
        assertNull(restored.getPassword(), "Jackson 不在缓存中写出密码");
        assertEquals(original.getUserId(), restored.getUserId());
        assertEquals("1234", serializer.deserialize(serializer.serialize("1234")));
        assertEquals("1", new String(serializer.serialize(1), java.nio.charset.StandardCharsets.UTF_8));
    }
}
