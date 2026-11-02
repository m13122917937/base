package com.ruoyi.framework.config;

import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.core.domain.entity.SysDictType;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

/**
 * 使用 Jackson 写标准 JSON，并只恢复固定登记的缓存类型；不启用 default typing 或按输入加载任意类。
 * 兼容旧缓存的类型标记、Set[...] 和整数 L 后缀；缓存键、TTL、字符串和 Integer 限流参数保持原协议。
 */
public final class JacksonJsonRedisSerializer implements RedisSerializer<Object> {
    private static final String TYPE = "@type";
    private static final String VALUE = "@value";
    private static final String SET_TYPE = "java.util.LinkedHashSet";
    private static final Set<String> MAP_TYPES = Set.of("java.util.HashMap", "java.util.LinkedHashMap");
    private static final Map<String, Class<?>> TYPES = registeredTypes();
    private final ObjectMapper mapper;

    /** 复制应用配置供缓存独立使用，关闭全局多态和未知字段失败，以读取旧对象中的派生属性。 */
    public JacksonJsonRedisSerializer(ObjectMapper objectMapper) {
        this.mapper = objectMapper.copy().deactivateDefaultTyping()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);
    }

    /** 创建明确的类型登记表；增加缓存模型时须显式登记，不根据包名前缀放行任意类。 */
    private static Map<String, Class<?>> registeredTypes() {
        Map<String, Class<?>> types = new LinkedHashMap<>();
        for (Class<?> type : List.of(LoginUser.class, SysUser.class, SysDept.class, SysRole.class,
                SysDictData.class, SysDictType.class, Long.class, Short.class, Byte.class, Float.class,
                BigDecimal.class, BigInteger.class, Date.class)) {
            types.put(type.getName(), type);
        }
        return Map.copyOf(types);
    }

    /** 将已登记对象写为带受控类型信息的 JSON；null 写空字节，不支持的缓存类型直接报错。 */
    @Override
    public byte[] serialize(Object value) throws SerializationException {
        if (value == null) {
            return new byte[0];
        }
        try {
            return mapper.writeValueAsBytes(toTree(value));
        } catch (IOException | IllegalArgumentException ex) {
            throw new SerializationException("无法序列化JSON缓存", ex);
        }
    }

    /** 递归记录集合/Map 的元素类型；自然标量直接输出，保留 Lua 所需的裸整数。 */
    private JsonNode toTree(Object value) {
        if (value == null) {
            return mapper.nullNode();
        }
        if (value instanceof Map<?, ?> map) {
            ObjectNode node = mapper.createObjectNode();
            map.forEach((key, entry) -> node.set(String.valueOf(key), toTree(entry)));
            return node;
        }
        if (value instanceof Collection<?> collection) {
            ArrayNode array = mapper.createArrayNode();
            collection.forEach(entry -> array.add(toTree(entry)));
            return value instanceof Set<?> ? typedValue(SET_TYPE, array) : array;
        }
        if (value.getClass().isArray()) {
            ArrayNode array = mapper.createArrayNode();
            for (int index = 0; index < Array.getLength(value); index++) {
                array.add(toTree(Array.get(value, index)));
            }
            return array;
        }
        Class<?> type = value.getClass();
        if (type == String.class || type == Integer.class || type == Boolean.class || type == Double.class) {
            return mapper.valueToTree(value);
        }
        if (!TYPES.containsKey(type.getName())) {
            throw new IllegalArgumentException("未登记的JSON缓存类型");
        }
        JsonNode data = mapper.valueToTree(value);
        if (data instanceof ObjectNode object) {
            object.put(TYPE, type.getName());
            return object;
        }
        return typedValue(type.getName(), data);
    }

    /** 为 Set 和非自然标量建立标准 JSON 包装，保留其 Java 值类型。 */
    private ObjectNode typedValue(String type, JsonNode value) {
        return mapper.createObjectNode().put(TYPE, type).set(VALUE, value);
    }

    /** 先将旧字节语法规范化，再用 Jackson 读取；未知类型或无效数据报错，不静默降级为会话对象。 */
    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            String json = normalizeLegacySyntax(new String(bytes, StandardCharsets.UTF_8));
            JsonNode tree = mapper.readTree(json);
            if (tree == null) {
                throw new IllegalArgumentException("JSON缓存内容为空");
            }
            return fromTree(tree);
        } catch (IOException | IllegalArgumentException ex) {
            throw new SerializationException("无法反序列化JSON缓存", ex);
        }
    }

    /** 恢复已登记对象及集合；无类型标记的对象返回 Map，数组返回保留元素类型的 List。 */
    private Object fromTree(JsonNode node) throws IOException {
        if (node.isObject()) {
            if (node.has(TYPE)) {
                String type = node.path(TYPE).asText();
                if (SET_TYPE.equals(type)) {
                    if (!node.path(VALUE).isArray()) {
                        throw new IllegalArgumentException("Set缓存内容不是数组");
                    }
                    return new LinkedHashSet<>((List<?>) fromTree(node.get(VALUE)));
                }
                if (!MAP_TYPES.contains(type)) {
                    Class<?> target = TYPES.get(type);
                    if (target == null) {
                        throw new IllegalArgumentException("未登记的JSON缓存类型");
                    }
                    return mapper.treeToValue(cleanMetadata(node.has(VALUE) ? node.get(VALUE) : node), target);
                }
            }
            Map<String, Object> values = new LinkedHashMap<>();
            java.util.Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                if (!TYPE.equals(entry.getKey())) {
                    values.put(entry.getKey(), fromTree(entry.getValue()));
                }
            }
            return values;
        }
        if (node.isArray()) {
            List<Object> values = new ArrayList<>();
            for (JsonNode item : node) {
                values.add(fromTree(item));
            }
            return values;
        }
        return mapper.treeToValue(node, Object.class);
    }

    /** 校验嵌套类型标记后移除元数据，交由明确 Bean 类型绑定 Date、Set 和关联对象。 */
    private JsonNode cleanMetadata(JsonNode node) {
        if (node instanceof ObjectNode object) {
            if (object.has(TYPE)) {
                String type = object.path(TYPE).asText();
                if (!TYPES.containsKey(type) && !MAP_TYPES.contains(type) && !SET_TYPE.equals(type)) {
                    throw new IllegalArgumentException("未登记的JSON缓存类型");
                }
                if (object.has(VALUE)) {
                    return cleanMetadata(object.get(VALUE));
                }
                object.remove(TYPE);
            }
            java.util.Iterator<Map.Entry<String, JsonNode>> fields = object.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                entry.setValue(cleanMetadata(entry.getValue()));
            }
        } else if (node instanceof ArrayNode array) {
            for (int index = 0; index < array.size(); index++) {
                array.set(index, cleanMetadata(array.get(index)));
            }
        }
        return node;
    }

    /**
     * 只适配旧缓存的 Set 前缀和整数 L 后缀，不执行类型加载；字符串内的相同文本原样保留。
     * 适配结果仍交由 Jackson 严格验证，括号或数字错误不会被吞掉。
     */
    private String normalizeLegacySyntax(String text) {
        StringBuilder json = new StringBuilder(text.length());
        Deque<Boolean> arrays = new ArrayDeque<>();
        boolean quoted = false;
        boolean escaped = false;
        for (int index = 0; index < text.length(); index++) {
            char current = text.charAt(index);
            if (quoted) {
                json.append(current);
                if (escaped) {
                    escaped = false;
                } else if (current == '\\') {
                    escaped = true;
                } else if (current == '"') {
                    quoted = false;
                }
            } else if (current == '"') {
                quoted = true;
                json.append(current);
            } else if (text.startsWith("Set[", index)) {
                json.append("{\"@type\":\"").append(SET_TYPE).append("\",\"@value\":[");
                arrays.push(true);
                index += 3;
            } else if (current == '[') {
                arrays.push(false);
                json.append(current);
            } else if (current == ']') {
                json.append(current);
                if (!arrays.isEmpty() && arrays.pop()) {
                    json.append('}');
                }
            } else if (current == '-' || Character.isDigit(current)) {
                int end = index + 1;
                while (end < text.length() && Character.isDigit(text.charAt(end))) {
                    end++;
                }
                if (end < text.length() && text.charAt(end) == 'L') {
                    json.append("{\"@type\":\"java.lang.Long\",\"@value\":")
                            .append(text, index, end).append('}');
                    index = end;
                } else {
                    json.append(current);
                }
            } else {
                json.append(current);
            }
        }
        return json.toString();
    }
}
