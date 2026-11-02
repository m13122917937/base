package com.ruoyi.architecture;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.framework.aspectj.LogAspect;
import com.ruoyi.system.domain.SysOperLog;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * 验证移除操作类型后，实际日志切面继续写入既有数据库编码。
 */
class OperationLogCompatibilityTest {
    /**
     * 遍历所有保留的操作类型，通过真实切面构造日志，确保清空数据仍为 9、其他类型保持 0 到 7。
     * 关闭请求和响应采集，不启动容器、不连接数据库，也不触发异步保存。
     *
     * @throws Exception 日志描述处理失败
     */
    @Test
    void preservesPersistedOperationCodes() throws Exception {
        int[] codes = {0, 1, 2, 3, 4, 5, 6, 7, 9};
        BusinessType[] types = BusinessType.values();
        assertEquals(codes.length, types.length);
        for (int index = 0; index < types.length; index++) {
            Log annotation = mock(Log.class);
            when(annotation.businessType()).thenReturn(types[index]);
            when(annotation.operatorType()).thenReturn(com.ruoyi.common.enums.OperatorType.MANAGE);
            SysOperLog record = new SysOperLog();
            new LogAspect().getControllerMethodDescription(null, annotation, record, null);
            assertEquals(codes[index], record.getBusinessType());
        }
    }
}
