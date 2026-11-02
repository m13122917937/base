package com.ruoyi.common.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ruoyi.common.utils.spring.SpringUtils;
import org.springframework.beans.BeansException;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/** 统一 Jackson JSON 编解码；优先复用 Spring 配置，独立工具调用使用不启用多态类型的默认配置。 */
public final class JsonUtils {
    private static final ObjectMapper DEFAULT_MAPPER = Jackson2ObjectMapperBuilder.json().build();

    /** 工具不持有请求状态，不允许实例化。 */
    private JsonUtils() {
    }

    /** 获取 Spring 的共享 ObjectMapper，遵循 spring.jackson 配置；独立调用使用 Jackson 默认值，不覆盖时区。 */
    public static ObjectMapper mapper() {
        try {
            return SpringUtils.getBean(ObjectMapper.class);
        } catch (BeansException | IllegalStateException | NullPointerException ex) {
            return DEFAULT_MAPPER;
        }
    }

    /** 序列化为标准 JSON；失败抛出固定消息，不把原始数据拼入异常消息。 */
    public static String toJsonString(Object value) {
        try {
            return mapper().writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("JSON序列化失败", ex);
        }
    }

    /** 转换为独立 JSON 树后递归移除敏感键，覆盖 Bean、Map 和嵌套数组，不修改原对象。 */
    public static String toJsonString(Object value, String... excludedProperties) {
        ObjectMapper objectMapper = mapper();
        JsonNode tree = objectMapper.valueToTree(value);
        Set<String> excluded = excludedProperties == null ? Set.of()
                : new HashSet<>(Arrays.asList(excludedProperties));
        removeProperties(tree, excluded);
        try {
            return objectMapper.writeValueAsString(tree);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("JSON序列化失败", ex);
        }
    }

    /** 递归删除对象属性及数组元素内的同名属性，保留其余数据和原有顺序。 */
    private static void removeProperties(JsonNode node, Set<String> excluded) {
        if (node instanceof ObjectNode object) {
            object.remove(excluded);
        }
        if (node != null && node.isContainerNode()) {
            node.forEach(child -> removeProperties(child, excluded));
        }
    }

    /** 解析 JSON 树供外部响应读取；格式错误抛出固定消息，由业务调用方决定降级策略。 */
    public static JsonNode readTree(String value) {
        try {
            return mapper().readTree(value);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("JSON数据格式不正确", ex);
        }
    }

    /** 将缓存的 JSON 集合/Map 绑定到明确的目标类型，不解析输入中的任意 Java 类名。 */
    public static <T> T convertValue(Object value, TypeReference<T> type) {
        return mapper().convertValue(value, type);
    }
}
