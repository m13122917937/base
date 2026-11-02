package com.ruoyi.system.model.query;

import lombok.Data;
import com.ruoyi.common.mybatis.QueryField;
import com.ruoyi.common.mybatis.DynamicCondition.Operator;

/** SysNotice 普通单表查询条件，仅包含原列表允许的筛选字段。 */
@Data
public class SysNoticeQuery {
    @QueryField(operator = Operator.LIKE, field = "notice_title")
    private String noticeTitle;
    @QueryField(operator = Operator.EQ, field = "notice_type")
    private String noticeType;
    @QueryField(operator = Operator.LIKE, field = "create_by")
    private String createBy;
}
