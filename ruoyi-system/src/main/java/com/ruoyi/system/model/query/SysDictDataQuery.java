package com.ruoyi.system.model.query;

import lombok.Data;
import com.ruoyi.common.mybatis.QueryField;
import com.ruoyi.common.mybatis.DynamicCondition.Operator;

/** SysDictData 普通单表查询条件，仅包含原列表允许的筛选字段。 */
@Data
public class SysDictDataQuery {
    @QueryField(operator = Operator.EQ, field = "dict_type")
    private String dictType;
    @QueryField(operator = Operator.LIKE, field = "dict_label")
    private String dictLabel;
    @QueryField(operator = Operator.EQ, field = "status")
    private String status;
}
