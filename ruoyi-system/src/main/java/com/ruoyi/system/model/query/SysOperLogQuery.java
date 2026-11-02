package com.ruoyi.system.model.query;

import lombok.Data;
import com.ruoyi.common.mybatis.QueryField;
import com.ruoyi.common.mybatis.DynamicCondition.Operator;

/** SysOperLog 普通单表查询条件，仅包含原列表允许的筛选字段。 */
@Data
public class SysOperLogQuery {
    @QueryField(operator = Operator.LIKE, field = "oper_ip")
    private String operIp;
    @QueryField(operator = Operator.LIKE, field = "title")
    private String title;
    @QueryField(operator = Operator.EQ, field = "business_type")
    private Integer businessType;
    @QueryField(operator = Operator.IN, field = "business_type")
    private Integer[] businessTypes;
    @QueryField(operator = Operator.EQ, field = "status")
    private Integer status;
    @QueryField(operator = Operator.LIKE, field = "oper_name")
    private String operName;
    @QueryField(ignore = true)
    private Object beginTime;
    @QueryField(ignore = true)
    private Object endTime;
}
