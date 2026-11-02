package com.ruoyi.web.vo.system;

import java.util.Date;

/**
 * 公告已读用户响应，字段与原已读列表完全一致。
 *
 * @param userId      阅读者编号
 * @param userName    登录名
 * @param nickName    显示名
 * @param deptName    部门名称
 * @param phonenumber 联系手机号
 * @param readTime    首次已读时间
 */
public record NoticeReaderVO(Long userId, String userName, String nickName, String deptName, String phonenumber,
                             Date readTime) {
}
