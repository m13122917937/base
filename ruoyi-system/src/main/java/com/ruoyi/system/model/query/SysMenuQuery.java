package com.ruoyi.system.model.query;

import lombok.Data;
import com.ruoyi.common.mybatis.QueryField;
import com.ruoyi.common.mybatis.DynamicCondition.Operator;

/** SysMenu 普通单表查询条件，仅包含原列表允许的筛选字段。 */
@Data
public class SysMenuQuery {
    @QueryField(operator = Operator.LIKE, field = "menu_name")
    private String menuName;
    @QueryField(operator = Operator.EQ, field = "visible")
    private String visible;
    @QueryField(operator = Operator.EQ, field = "status")
    private String status;
}
