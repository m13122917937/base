package com.ruoyi.architecture;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 在编译产物中检查领域出口，不允许借 Facade 把 Service 或持久化对象暴露给 Biz。
 */
class FacadeBoundaryTest {
    /**
     * 扫描所有 Facade 接口，逐个检查包含泛型的完整方法签名，禁止实体、Service、Mapper 和无类型投影 Map。
     *
     * @throws Exception 类路径扫描或反射失败
     */
    @Test
    void facadesHidePersistenceAndServiceTypes() throws Exception {
        Resource[] resources = new PathMatchingResourcePatternResolver().getResources("classpath*:com/ruoyi/**/facade/I*.class");
        CachingMetadataReaderFactory metadata = new CachingMetadataReaderFactory();
        Set<String> found = new HashSet<String>();
        for (Resource resource : resources) {
            String className = metadata.getMetadataReader(resource).getClassMetadata().getClassName();
            Class<?> type = Class.forName(className);
            if (!type.isInterface()) {
                continue;
            }
            found.add(className);
            for (Method method : type.getDeclaredMethods()) {
                String signature = method.toGenericString();
                assertFalse(signature.contains(".service."), signature);
                assertFalse(signature.contains(".mapper."), signature);
                assertFalse(signature.contains(".domain.entity."), signature);
                assertFalse(signature.matches(".*com\\.ruoyi\\.(system|quartz)\\.domain\\..*"), signature);
                assertFalse(signature.contains("java.util.Map<java.lang.String, java.lang.Object>"), signature);
            }
        }
        assertEquals(18, found.size());
    }
}
