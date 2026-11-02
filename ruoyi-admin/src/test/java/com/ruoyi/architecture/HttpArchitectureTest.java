package com.ruoyi.architecture;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableDataInfo;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 不启动数据库或 Redis，检查全部 HTTP 契约及 Controller 的真实编译后依赖。
 */
class HttpArchitectureTest {
    /**
     * 读取当前契约快照，逐个比较 107 个方法的类路径、方法路径、HTTP 动词、权限及审计标识。
     * 任何新增、删除或漏迁移的 HTTP 方法都会导致数量和集合比较失败。
     *
     * @throws Exception 资源读取或反射失败
     */
    @Test
    void preservesEveryHttpContract() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        InputStream resource = getClass().getResourceAsStream("/contracts/http-endpoints.json");
        assertNotNull(resource);
        JsonNode contracts;
        try (InputStream input = resource) {
            contracts = mapper.readTree(input);
        }
        Set<String> expected = new HashSet<String>();
        for (JsonNode contract : contracts) {
            Class<?> controller = Class.forName(contract.get("controller").asText());
            Method method = Arrays.stream(controller.getDeclaredMethods())
                    .filter(candidate -> candidate.getName().equals(contract.get("method").asText()))
                    .findFirst().orElseThrow();
            RequestMapping classMapping = AnnotatedElementUtils.findMergedAnnotation(controller, RequestMapping.class);
            RequestMapping mapping = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
            assertNotNull(mapping, method.toString());
            assertEquals(strings(contract.get("classPaths")), new TreeSet<String>(Arrays.asList(classMapping == null ? new String[0] : classMapping.path())), method.toString());
            assertEquals(strings(contract.get("paths")), new TreeSet<String>(Arrays.asList(mapping.path())), method.toString());
            assertEquals(strings(contract.get("verbs")), Arrays.stream(mapping.method()).map(Enum::name).collect(Collectors.toCollection(TreeSet::new)), method.toString());
            PreAuthorize permission = method.getAnnotation(PreAuthorize.class);
            assertEquals(nullableText(contract.get("permission")), permission == null ? null : permission.value(), method.toString());
            Log log = method.getAnnotation(Log.class);
            assertEquals(nullableText(contract.get("logTitle")), log == null ? null : log.title(), method.toString());
            assertEquals(nullableText(contract.get("logType")), log == null ? null : log.businessType().name(), method.toString());
            expected.add(controller.getName() + "#" + method.getName());
        }
        Set<String> actual = new HashSet<String>();
        for (Class<?> controller : controllers()) {
            for (Method method : controller.getDeclaredMethods()) {
                if (AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class) != null) {
                    actual.add(controller.getName() + "#" + method.getName());
                }
            }
        }
        assertEquals(107, expected.size());
        assertEquals(expected, actual);
    }

    /**
     * 扫描实际控制器与注入的 Biz，要求 Controller 位于 web.controller、Biz 不继承控制器或携带 HTTP 协议类型。
     * 同时检查 Controller 不暴露实体；禁止将响应封装或 Servlet 依赖再次下沉到应用编排。
     *
     * @throws Exception 类加载或扫描失败
     */
    @Test
    void controllersOnlyDependOnConcreteBizClasses() throws Exception {
        Set<Class<?>> controllers = controllers();
        assertEquals(17, controllers.size());
        for (Class<?> controller : controllers) {
            assertTrue(controller.getPackageName().startsWith("com.ruoyi.web.controller."), controller.getName());
            Field[] dependencies = Arrays.stream(controller.getDeclaredFields()).filter(field -> !Modifier.isStatic(field.getModifiers())).toArray(Field[]::new);
            assertEquals(1, dependencies.length, controller.getName());
            assertFalse(dependencies[0].getType().isInterface(), controller.getName());
            assertFalse(Modifier.isAbstract(dependencies[0].getType().getModifiers()), controller.getName());
            assertTrue(dependencies[0].getType().getName().startsWith("com.ruoyi.biz."), controller.getName());
            Class<?> biz = dependencies[0].getType();
            assertFalse(BaseController.class.isAssignableFrom(biz), biz.getName());
            assertEquals(Object.class, biz.getSuperclass(), biz.getName());
            for (Method method : biz.getDeclaredMethods()) {
                assertFalse(AjaxResult.class.isAssignableFrom(method.getReturnType()), method.toString());
                assertFalse(TableDataInfo.class.isAssignableFrom(method.getReturnType()), method.toString());
                assertFalse(R.class.isAssignableFrom(method.getReturnType()), method.toString());
                assertFalse(method.toGenericString().contains("jakarta.servlet."), method.toString());
            }
            for (Method method : controller.getDeclaredMethods()) {
                assertFalse(method.toGenericString().contains("domain.entity"), method.toString());
                assertFalse(method.toGenericString().matches(".*com\\.ruoyi\\.(system|quartz)\\.domain\\..*"), method.toString());
            }
        }
    }

    /**
     * 只扫描带 RestController 的可实例化类，不初始化 Spring 容器或领域服务。
     *
     * @return 当前编译产物中的全部 HTTP 控制器
     * @throws ClassNotFoundException 控制器类缺失
     */
    private Set<Class<?>> controllers() throws ClassNotFoundException {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(RestController.class));
        Set<Class<?>> result = new HashSet<Class<?>>();
        for (org.springframework.beans.factory.config.BeanDefinition definition : scanner.findCandidateComponents("com.ruoyi")) {
            result.add(Class.forName(definition.getBeanClassName()));
        }
        return result;
    }

    /**
     * 将快照中的路径或动词数组读为集合，以忽略注解数组的无关排序。
     */
    private Set<String> strings(JsonNode values) {
        Set<String> result = new TreeSet<String>();
        values.forEach(value -> result.add(value.asText()));
        return result;
    }

    /**
     * 将 JSON null 保留为空，避免无权限注解被误判为字符串 null。
     */
    private String nullableText(JsonNode value) {
        return value == null || value.isNull() ? null : value.asText();
    }
}
