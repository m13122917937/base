package com.ruoyi.system.model.bo;

import java.util.Date;

/**
 * 公告已读用户的类型化领域查询结果；不向应用层泄露 Mapper 的动态投影 Map。
 *
 * @param userId      阅读者编号
 * @param userName    登录名
 * @param nickName    显示名
 * @param deptName    部门名称
 * @param phonenumber 联系手机号
 * @param readTime    首次已读时间，保持原查询语义
 */
public record NoticeReaderBO(Long userId, String userName, String nickName, String deptName, String phonenumber,
                             Date readTime) {
}
