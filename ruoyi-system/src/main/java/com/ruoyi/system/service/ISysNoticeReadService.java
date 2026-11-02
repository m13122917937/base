package com.ruoyi.system.service;

import com.ruoyi.system.domain.SysNoticeRead;
import com.baomidou.mybatisplus.spring.service.IService;
import com.ruoyi.common.core.page.PageDomain;
import java.util.List;
import java.util.Map;

import com.ruoyi.system.domain.SysNotice;

/**
 * 公告已读记录 服务层
 *
 * @author ruoyi
 */
public interface ISysNoticeReadService extends IService<SysNoticeRead> {
    /**
     * 标记已读（幂等，重复调用不报错）
     *
     * @param noticeId 公告ID
     * @param userId   用户ID
     */
    public void markRead(Long noticeId, Long userId);

    /**
     * 查询某用户未读公告数量
     *
     * @param userId 用户ID
     * @return 未读数量
     */
    public int selectUnreadCount(Long userId);

    /**
     * 查询公告列表并标记当前用户已读状态（用于首页展示）
     *
     * @param userId 用户ID
     * @param limit  最多返回条数
     * @return 带 isRead 标记的公告列表
     */
    public List<SysNotice> selectNoticeListWithReadStatus(Long userId, int limit);

    /**
     * 批量标记已读
     *
     * @param userId    用户ID
     * @param noticeIds 公告ID数组
     */
    public void markReadBatch(Long userId, Long[] noticeIds);

    /**
     * 查询已阅读某公告的用户列表
     *
     * @param noticeId    公告ID
     * @param searchValue 搜索值
     * @return 已读用户列表
     */
    public List<Map<String, Object>> selectReadUsersByNoticeId(Long noticeId, String searchValue);

    /**
     * 在目标 Mapper 查询前启动分页并在 finally 中清理线程状态，保留原过滤条件及数据权限。
     * 查询失败时异常原样向上传递，不新增事务；null 分页参数表示全量查询。
     *
     * @param noticeId 原查询条件
     * @param searchValue 原查询条件
     * @param pageDomain 通用分页参数，不读取 Servlet 请求
     * @return 保留 Page 元数据的查询列表
     */
    public List<Map<String, Object>> selectReadUsersByNoticeId(Long noticeId, String searchValue, PageDomain pageDomain);

    /**
     * 删除公告时清理对应已读记录
     *
     * @param noticeIds 公告ID数组
     */
    public void deleteByNoticeIds(Long[] noticeIds);
}
