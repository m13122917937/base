package com.ruoyi.architecture;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import javax.sql.DataSource;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.JsonUtils;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.framework.config.ApplicationConfig;
import com.ruoyi.framework.config.RedisConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/** 使用实际 YML 和程序配置验证时区绑定，覆盖 HTTP、静态工具、缓存及外部覆盖，不修改 JVM 时区。 */
class JacksonTimezoneConfigurationTest {
    /** YML 的 Asia/Shanghai 在程序配置存在时仍生效，三条 JSON 链路输出同一个本地时间。 */
    @Test
    void usesYamlTimezoneAcrossHttpUtilityAndCache() {
        assertTimezone("Asia/Shanghai", "1970-01-01 08:00:00");
    }

    /** 外部属性可把 YML 的时区覆盖为 UTC，代码定制器或缓存复制过程不会将其改回系统默认值。 */
    @Test
    void allowsExternalTimezoneOverride() {
        assertTimezone("UTC", "1970-01-01 00:00:00", "spring.jackson.time-zone=UTC");
    }

    /** 创建沿用实际 application.yml 的 Jackson 实例，供固定旧缓存样本使用，避免依赖测试机器时区。 */
    static ObjectMapper configuredMapper() {
        AtomicReference<ObjectMapper> mapper = new AtomicReference<>();
        jacksonContext().run(context -> {
            assertThat(context).hasNotFailed();
            mapper.set(context.getBean(ObjectMapper.class));
        });
        return mapper.get();
    }

    /** 加载项目 YML 后只装配 Jackson，不连接应用配置中的数据库或 Redis。 */
    private static ApplicationContextRunner jacksonContext() {
        return new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(JacksonAutoConfiguration.class))
                .withInitializer(context -> {
                    try {
                        new YamlPropertySourceLoader().load("application", new ClassPathResource("application.yml"))
                                .forEach(source -> context.getEnvironment().getPropertySources().addLast(source));
                    } catch (java.io.IOException ex) {
                        throw new IllegalStateException("无法加载Jackson配置", ex);
                    }
                });
    }

    /**
     * 在真实 ApplicationConfig 和隔离会话工厂下验证共享配置，缓存使用生产 RedisConfig 工厂及模拟连接。
     * 完成后恢复 SpringUtils 的静态引用，避免关闭的测试上下文影响其他用例；不发起 Redis 网络请求。
     */
    @SuppressWarnings("unchecked")
    private void assertTimezone(String timezone, String expectedTime, String... properties) {
        Object previousFactory = ReflectionTestUtils.getField(SpringUtils.class, "beanFactory");
        Object previousContext = ReflectionTestUtils.getField(SpringUtils.class, "applicationContext");
        try {
            jacksonContext()
                    .withConfiguration(AutoConfigurations.of(MybatisPlusAutoConfiguration.class))
                    .withUserConfiguration(ApplicationConfig.class)
                    .withBean(SpringUtils.class)
                    .withBean(DataSource.class, () -> new DriverManagerDataSource(
                            "jdbc:h2:mem:timezone_" + UUID.randomUUID(), "sa", ""))
                    .withPropertyValues(properties)
                    .run(context -> {
                        assertThat(context).hasNotFailed();
                        ObjectMapper mapper = context.getBean(ObjectMapper.class);
                        assertThat(mapper.getSerializationConfig().getTimeZone().getID()).isEqualTo(timezone);
                        assertThat(JsonUtils.mapper()).isSameAs(mapper);
                        SysUser user = new SysUser(12L);
                        user.setCreateTime(new Date(0));
                        assertThat(mapper.readTree(JsonUtils.toJsonString(user)).path("createTime").asText())
                                .isEqualTo(expectedTime);
                        MockHttpOutputMessage response = new MockHttpOutputMessage();
                        new MappingJackson2HttpMessageConverter(mapper).write(user, MediaType.APPLICATION_JSON, response);
                        assertThat(mapper.readTree(response.getBodyAsBytes()).path("createTime").asText())
                                .isEqualTo(expectedTime);
                        RedisSerializer<Object> serializer = (RedisSerializer<Object>) new RedisConfig()
                                .redisTemplate(mock(RedisConnectionFactory.class), mapper).getValueSerializer();
                        byte[] cached = serializer.serialize(user);
                        assertThat(mapper.readTree(cached).path("createTime").asText()).isEqualTo(expectedTime);
                        assertThat(((SysUser) serializer.deserialize(cached)).getCreateTime()).isEqualTo(new Date(0));
                    });
        } finally {
            ReflectionTestUtils.setField(SpringUtils.class, "beanFactory", previousFactory);
            ReflectionTestUtils.setField(SpringUtils.class, "applicationContext", previousContext);
        }
    }
}
