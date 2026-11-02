package com.ruoyi.architecture;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.annotation.RepeatSubmit;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.enums.OperatorType;
import com.ruoyi.common.utils.JsonUtils;
import com.ruoyi.common.utils.http.HttpUtils;
import com.ruoyi.common.utils.ip.AddressUtils;
import com.ruoyi.framework.aspectj.LogAspect;
import com.ruoyi.framework.config.JacksonJsonRedisSerializer;
import com.ruoyi.framework.interceptor.impl.SameUrlDataInterceptor;
import com.ruoyi.framework.security.handle.AuthenticationEntryPointImpl;
import com.ruoyi.system.domain.SysOperLog;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/** 用旧缓存静态样本和真实 Jackson 编解码验收移除旧 JSON 库后的兼容性，不连接业务 Redis/HTTP。 */
class JacksonMigrationTest {
    private final ObjectMapper mapper = JacksonTimezoneConfigurationTest.configuredMapper();
    private final JacksonJsonRedisSerializer serializer = new JacksonJsonRedisSerializer(mapper);

    /** 实际旧字节恢复登录用户、权限、部门和角色；新字节为标准 JSON，密码不输出。 */
    @Test
    void readsLegacySessionAndWritesStandardJson() throws Exception {
        LoginUser session = (LoginUser) serializer.deserialize(fixture("session"));
        assertThat(session.getToken()).isEqualTo("fixture-token");
        assertThat(session.getUserId()).isEqualTo(12L);
        assertThat(session.getPermissions()).containsExactly("system:user:list");
        assertThat(session.getUser().getDept().getDeptName()).isEqualTo("fixture-dept");
        assertThat(session.getUser().getRoles()).extracting(role -> role.getRoleKey()).containsExactly("fixture-role");
        assertThat(session.getUser().getCreateTime().getTime()).isEqualTo(1700000000000L);
        byte[] bytes = serializer.serialize(session);
        JsonNode json = mapper.readTree(bytes);
        assertThat(json.path("@type").asText()).isEqualTo(LoginUser.class.getName());
        assertThat(json.has("password")).isFalse();
        assertThat(json.path("user").has("password")).isFalse();
        LoginUser roundTrip = (LoginUser) serializer.deserialize(bytes);
        assertThat(roundTrip.getPermissions()).isEqualTo(session.getPermissions());
        assertThat(roundTrip.getUser().getCreateTime()).isEqualTo(session.getUser().getCreateTime());
    }

    /** 旧字典数组、Map、Set 和小 Long 均能读取；字符串里的旧语法片段保持原值。 */
    @Test
    void readsLegacyCollectionsAndTypedNumbers() throws Exception {
        List<?> dictionaries = (List<?>) serializer.deserialize(fixture("dictionary"));
        SysDictData dictionary = (SysDictData) dictionaries.get(0);
        assertThat(dictionary.getDictCode()).isEqualTo(3L);
        assertThat(dictionary.getDictLabel()).isEqualTo("fixture-label");
        Map<?, ?> map = (Map<?, ?>) serializer.deserialize(fixture("map"));
        assertThat(((Map<?, ?>) map.get("repeat")).get("repeatTime")).isEqualTo(1700000000000L);
        assertThat(serializer.deserialize(fixture("long"))).isEqualTo(1L);
        assertThat(serializer.deserialize(fixture("set"))).isEqualTo(Set.of("a", "b"));
        String text = "Set[\"a\"] 1L \\\"quoted";
        assertThat(serializer.deserialize(serializer.serialize(text))).isEqualTo(text);
        Map<String, Object> value = Map.of("list", List.of(dictionary), "long", 1L, "set", Set.of("a"));
        Map<?, ?> restored = (Map<?, ?>) serializer.deserialize(serializer.serialize(value));
        assertThat(restored.get("long")).isEqualTo(1L);
        assertThat(restored.get("set")).isEqualTo(Set.of("a"));
        assertThat(((List<?>) restored.get("list")).get(0)).isInstanceOf(SysDictData.class);
        assertThat(new String(serializer.serialize(1), StandardCharsets.UTF_8)).isEqualTo("1");
        assertThat(serializer.deserialize(new byte[0])).isNull();
        assertThat(serializer.serialize(null)).isEmpty();
    }

    /** 根对象及嵌套对象都拒绝未登记类型；格式错误、尾部数据不会被静默接受。 */
    @Test
    void rejectsUnknownCacheTypesAndMalformedJson() {
        for (String invalid : List.of("{\"@type\":\"java.lang.Runtime\"}",
                "{\"@type\":\"com.ruoyi.UnknownType\"}",
                "{\"@type\":\"com.ruoyi.common.core.domain.model.LoginUser\",\"user\":{\"@type\":\"java.lang.Runtime\"}}",
                "Set[", "{broken}", "[] {}")) {
            assertThatThrownBy(() -> serializer.deserialize(invalid.getBytes(StandardCharsets.UTF_8)))
                    .isInstanceOf(SerializationException.class);
        }
        assertThatThrownBy(() -> serializer.serialize(new Object())).isInstanceOf(SerializationException.class);
    }

    /** 认证失败仍返回原 code/msg 协议；项目运行时不包含旧 JSON 实现。 */
    @Test
    void preservesAuthenticationResponseAndRemovesOldDependency() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/fixture/private");
        MockHttpServletResponse response = new MockHttpServletResponse();
        new AuthenticationEntryPointImpl().commence(request, response, new BadCredentialsException("fixture"));
        JsonNode json = mapper.readTree(response.getContentAsString());
        assertThat(json.path("code").asInt()).isEqualTo(401);
        assertThat(json.path("msg").asText()).contains("认证失败", "/fixture/private");
        assertThatThrownBy(() -> Class.forName("com.alibaba.fastjson2.JSON")).isInstanceOf(ClassNotFoundException.class);
        assertThatThrownBy(() -> Class.forName("com.alibaba.fastjson.JSON")).isInstanceOf(ClassNotFoundException.class);
    }

    /** 真实日志切面使用 Jackson 递归排除请求/响应中的密码及注解指定字段，原对象保持完整。 */
    @Test
    void redactsNestedRequestAndResponseProperties() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/fixture/log");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        try {
            Map<String, Object> payload = Map.of("password", "fixture-password",
                    "nested", List.of(Map.of("oldPassword", "fixture-old", "secret", "fixture-secret", "visible", "keep")));
            Log annotation = mock(Log.class);
            when(annotation.businessType()).thenReturn(BusinessType.UPDATE);
            when(annotation.operatorType()).thenReturn(OperatorType.MANAGE);
            when(annotation.title()).thenReturn("fixture");
            when(annotation.isSaveRequestData()).thenReturn(true);
            when(annotation.isSaveResponseData()).thenReturn(true);
            when(annotation.excludeParamNames()).thenReturn(new String[]{"secret"});
            JoinPoint point = mock(JoinPoint.class);
            when(point.getArgs()).thenReturn(new Object[]{payload});
            SysOperLog record = new SysOperLog();
            record.setRequestMethod("POST");
            new LogAspect().getControllerMethodDescription(point, annotation, record, payload);
            assertThat(record.getOperParam()).contains("keep").doesNotContain("fixture-password", "fixture-old", "fixture-secret");
            assertThat(record.getJsonResult()).contains("keep").doesNotContain("fixture-password", "fixture-old", "fixture-secret");
            assertThat(payload).containsKey("password");
        } finally {
            RequestContextHolder.resetRequestAttributes();
        }
    }

    /** 缓存值真实经过 Jackson 往返后，同参数被拒绝、参数变化放行，时间值按 Number 读取。 */
    @Test
    void preservesRepeatSubmitBehaviorAcrossCacheRoundTrip() {
        RedisCache cache = mock(RedisCache.class);
        AtomicReference<Object> saved = new AtomicReference<>();
        when(cache.getCacheObject(anyString())).thenAnswer(invocation -> saved.get());
        doAnswer(invocation -> {
            saved.set(serializer.deserialize(serializer.serialize(invocation.getArgument(1))));
            return null;
        }).when(cache).setCacheObject(anyString(), any(), anyInt(), any(TimeUnit.class));
        SameUrlDataInterceptor interceptor = new SameUrlDataInterceptor();
        ReflectionTestUtils.setField(interceptor, "redisCache", cache);
        ReflectionTestUtils.setField(interceptor, "header", "Authorization");
        RepeatSubmit annotation = mock(RepeatSubmit.class);
        when(annotation.interval()).thenReturn(5000);
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/fixture/repeat");
        request.addHeader("Authorization", "fixture-token");
        request.setParameter("value", "first");
        assertThat(interceptor.isRepeatSubmit(request, annotation)).isFalse();
        assertThat(interceptor.isRepeatSubmit(request, annotation)).isTrue();
        request.setParameter("value", "second");
        assertThat(interceptor.isRepeatSubmit(request, annotation)).isFalse();
    }

    /** 外部 IP 响应只用 Jackson 解析，格式错误保留未知地址降级，不发起真实 HTTP。 */
    @Test
    void parsesAddressResponseWithJackson() {
        boolean previous = RuoYiConfig.isAddressEnabled();
        new RuoYiConfig().setAddressEnabled(true);
        try (org.mockito.MockedStatic<HttpUtils> http = mockStatic(HttpUtils.class)) {
            http.when(() -> HttpUtils.sendGet(anyString(), anyString(), anyString()))
                    .thenReturn("{\"pro\":\"浙江省\",\"city\":\"杭州市\"}", "{broken}", "[]", "null", "{}");
            assertThat(AddressUtils.getRealAddressByIP("8.8.8.8")).isEqualTo("浙江省 杭州市");
            assertThat(AddressUtils.getRealAddressByIP("8.8.8.8")).isEqualTo(AddressUtils.UNKNOWN);
            assertThat(AddressUtils.getRealAddressByIP("8.8.8.8")).isEqualTo(AddressUtils.UNKNOWN);
            assertThat(AddressUtils.getRealAddressByIP("8.8.8.8")).isEqualTo(AddressUtils.UNKNOWN);
            assertThat(AddressUtils.getRealAddressByIP("8.8.8.8")).isEqualTo(AddressUtils.UNKNOWN);
            http.verify(() -> HttpUtils.sendGet(anyString(), anyString(), anyString()), times(5));
        } finally {
            new RuoYiConfig().setAddressEnabled(previous);
        }
    }

    /** 读取迁移前由旧序列化器生成的固定样本，验证兼容不依赖运行时旧库。 */
    private byte[] fixture(String name) throws Exception {
        try (java.io.InputStream stream = new ClassPathResource("json-cache/legacy-" + name + ".json").getInputStream()) {
            return stream.readAllBytes();
        }
    }
}
