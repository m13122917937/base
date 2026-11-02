package com.ruoyi.quartz.model.query;

import lombok.Data;
import com.ruoyi.common.mybatis.QueryField;
import com.ruoyi.common.mybatis.DynamicCondition.Operator;

/** SysJob 普通单表查询条件，仅包含原列表允许的筛选字段。 */
@Data
public class SysJobQuery {
    @QueryField(operator = Operator.LIKE, field = "job_name")
    private String jobName;
    @QueryField(operator = Operator.EQ, field = "job_group")
    private String jobGroup;
    @QueryField(operator = Operator.EQ, field = "status")
    private String status;
    @QueryField(operator = Operator.LIKE, field = "invoke_target")
    private String invokeTarget;
}
