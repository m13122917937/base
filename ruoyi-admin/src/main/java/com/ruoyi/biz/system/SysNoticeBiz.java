package com.ruoyi.biz.system;

import com.ruoyi.common.core.page.PageDomain;
import java.util.LinkedHashMap;
import java.util.Map;

import com.ruoyi.common.utils.SecurityUtils;

import java.util.List;

import com.ruoyi.common.core.text.Convert;
import com.ruoyi.system.model.bo.SysNoticeBO;
import com.ruoyi.system.facade.ISysNoticeFacade;
import com.ruoyi.system.facade.ISysNoticeReadFacade;
import org.springframework.stereotype.Component;
import com.ruoyi.system.model.bo.*;
import com.ruoyi.framework.model.bo.*;

/**
 * SysNoticeController 对应的应用编排，保留原业务执行顺序与响应语义。
 */
@Component
public class SysNoticeBiz {

    private final ISysNoticeFacade noticeFacade;

    private final ISysNoticeReadFacade noticeReadFacade;

    /**
     * 接收应用编排所需的领域 Facade 和基础设施，不在构造阶段访问外部系统。
     */
    public SysNoticeBiz(ISysNoticeFacade noticeFacade, ISysNoticeReadFacade noticeReadFacade) {
        this.noticeFacade = noticeFacade;
        this.noticeReadFacade = noticeReadFacade;
    }

    /**
     * 传递显式分页参数，不创建线程分页状态；保留原业务校验顺序。
     * 获取通知公告列表：按原顺序执行 selectNoticeList、查询结果返回等步骤，并保留原判断、返回及异常语义。
     *
     * @param notice notice 请求参数或领域数据
     * @param pageDomain 通用分页参数，显式传给 Facade，由 Service 启动分页
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public List<SysNoticeBO> list(SysNoticeBO notice, PageDomain pageDomain) {
        List<SysNoticeBO> list = noticeFacade.queryNoticeList(notice, pageDomain);
        return list;
    }

    /**
     * 根据通知公告编号获取详细信息：按原顺序执行 selectNoticeById 等步骤，并保留原判断、返回及异常语义。
     *
     * @param noticeId noticeId 请求参数或领域数据
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public SysNoticeBO getInfo(Long noticeId) {
        return noticeFacade.queryNoticeById(noticeId);
    }

    /**
     * 新增通知公告：按原顺序执行 setCreateBy、getUsername、insertNotice 等步骤，并保留原判断、返回及异常语义。
     *
     * @param notice notice 请求参数或领域数据
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public int add(SysNoticeBO notice) {
        notice.setCreateBy(SecurityUtils.getUsername());
        return noticeFacade.createNotice(notice);
    }

    /**
     * 修改通知公告：按原顺序执行 setUpdateBy、getUsername、updateNotice 等步骤，并保留原判断、返回及异常语义。
     *
     * @param notice notice 请求参数或领域数据
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public int edit(SysNoticeBO notice) {
        notice.setUpdateBy(SecurityUtils.getUsername());
        return noticeFacade.modifyNotice(notice);
    }

    /**
     * 首页顶部公告列表（返回全部正常公告，带当前用户已读标记，最多5条）：按原顺序执行 getUserId、selectNoticeListWithReadStatus、stream、filter、getIsRead、count、put 等步骤，并保留原判断、返回及异常语义。
     *
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public Map<String, Object> listTop() {
        Long userId = SecurityUtils.getUserId();
        List<SysNoticeBO> list = noticeReadFacade.queryNoticeListWithReadStatus(userId, 5);
        long unreadCount = list.stream().filter(n -> !n.getIsRead()).count();
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("data", list);
        result.put("unreadCount", unreadCount);
        return result;
    }

    /**
     * 标记公告已读：按原顺序执行 getUserId、markRead、业务动作等步骤，并保留原判断、返回及异常语义。
     *
     * @param noticeId noticeId 请求参数或领域数据
     */
    public void markRead(Long noticeId) {
        Long userId = SecurityUtils.getUserId();
        noticeReadFacade.markRead(noticeId, userId);
        return;
    }

    /**
     * 批量标记已读：按原顺序执行 getUserId、toLongArray、markReadBatch、业务动作等步骤，并保留原判断、返回及异常语义。
     *
     * @param ids ids 请求参数或领域数据
     */
    public void markReadAll(String ids) {
        Long userId = SecurityUtils.getUserId();
        Long[] noticeIds = Convert.toLongArray(ids);
        noticeReadFacade.markReadBatch(userId, noticeIds);
        return;
    }

    /**
     * 传递显式分页参数，不创建线程分页状态；保留原业务校验顺序。
     * 已读用户列表数据：按原顺序执行 selectReadUsersByNoticeId、查询结果返回等步骤，并保留原判断、返回及异常语义。
     *
     * @param noticeId    noticeId 请求参数或领域数据
     * @param searchValue searchValue 请求参数或领域数据
     * @param pageDomain 通用分页参数，显式传给 Facade，由 Service 启动分页
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public List<?> readUsersList(Long noticeId, String searchValue, PageDomain pageDomain) {
        List<?> list = noticeReadFacade.queryReadUsersByNoticeId(noticeId, searchValue, pageDomain);
        return list;
    }

    /**
     * 调用公告领域的完整删除能力，将公告和已读记录作为同一个 Service 事务删除，再返回影响行数。
     *
     * @param noticeIds noticeIds 请求参数或领域数据
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public int remove(Long[] noticeIds) {
        return noticeFacade.removeNoticeByIds(noticeIds);
    }
}
