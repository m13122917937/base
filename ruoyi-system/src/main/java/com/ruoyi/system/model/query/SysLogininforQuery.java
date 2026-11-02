package com.ruoyi.system.model.query;

import lombok.Data;
import com.ruoyi.common.mybatis.QueryField;
import com.ruoyi.common.mybatis.DynamicCondition.Operator;

/** SysLogininfor 普通单表查询条件，仅包含原列表允许的筛选字段。 */
@Data
public class SysLogininforQuery {
    @QueryField(operator = Operator.LIKE, field = "ipaddr")
    private String ipaddr;
    @QueryField(operator = Operator.EQ, field = "status")
    private String status;
    @QueryField(operator = Operator.LIKE, field = "user_name")
    private String userName;
    @QueryField(ignore = true)
    private Object beginTime;
    @QueryField(ignore = true)
    private Object endTime;
}
