package com.ruoyi.system.model.query;

import lombok.Data;
import com.ruoyi.common.mybatis.QueryField;
import com.ruoyi.common.mybatis.DynamicCondition.Operator;

/** SysConfig 普通单表查询条件，仅包含原列表允许的筛选字段。 */
@Data
public class SysConfigQuery {
    @QueryField(operator = Operator.LIKE, field = "config_name")
    private String configName;
    @QueryField(operator = Operator.EQ, field = "config_type")
    private String configType;
    @QueryField(operator = Operator.LIKE, field = "config_key")
    private String configKey;
    @QueryField(ignore = true)
    private Object beginTime;
    @QueryField(ignore = true)
    private Object endTime;
}
