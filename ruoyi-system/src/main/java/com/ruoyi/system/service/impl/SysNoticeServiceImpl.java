package com.ruoyi.system.service.impl;

import java.util.Date;
import com.ruoyi.system.domain.SysNoticeRead;
import com.ruoyi.system.model.query.SysNoticeQuery;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ruoyi.common.mybatis.DynamicCondition;
import com.ruoyi.common.mybatis.PersistenceChecks;
import com.ruoyi.system.convert.PersistenceQueryConvert;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.common.core.page.PageDomain;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.system.domain.SysNotice;
import com.ruoyi.system.mapper.SysNoticeMapper;
import com.ruoyi.system.mapper.SysNoticeReadMapper;
import com.ruoyi.system.service.ISysNoticeService;

/**
 * 公告 服务层实现
 *
 * @author ruoyi
 */
@Service
public class SysNoticeServiceImpl extends ServiceImpl<SysNoticeMapper, SysNotice> implements ISysNoticeService {
    private final SysNoticeReadMapper noticeReadMapper;

    /**
     * 接收公告及已读记录 Mapper，使删除业务在同一个 Service 事务内完成。
     *
     * @param noticeMapper     公告持久化入口
     * @param noticeReadMapper 同领域已读记录持久化入口
     */
    public SysNoticeServiceImpl(SysNoticeMapper noticeMapper, SysNoticeReadMapper noticeReadMapper) {
        this.baseMapper = noticeMapper;
        this.noticeReadMapper = noticeReadMapper;
    }

    /**
     * 通过通用 getById 查询本表，未命中返回 null；不执行关联加载。
     * 查询公告信息
     *
     * @param noticeId 公告ID
     * @return 公告信息
     */
    @Override
    public SysNotice selectNoticeById(Long noticeId) {
        return getById(noticeId);
    }

    /**
     * 将允许的筛选项转换为 Query，经 DynamicCondition 构造条件，再调用通用 list；查询异常原样抛出。
     * 查询公告列表
     *
     * @param notice 公告信息
     * @return 公告集合
     */
    @Override
    public List<SysNotice> selectNoticeList(SysNotice notice) {
        return list(queryConditions(notice));
    }

    /**
     * 将允许的筛选项转换为 Query，经 DynamicCondition 构造条件，再调用通用 list；查询异常原样抛出。
     * 在目标 Mapper 查询前启动分页并在 finally 中清理线程状态，保留原过滤条件及数据权限。
     * 查询失败时异常原样向上传递，不新增事务；null 分页参数表示全量查询。
     *
     * @param notice 原查询条件
     * @param pageDomain 通用分页参数，不读取 Servlet 请求
     * @return 保留 Page 元数据的查询列表
     */
    @Override
    public List<SysNotice> selectNoticeList(SysNotice notice, PageDomain pageDomain) {
        return PageUtils.startPage(pageDomain, () -> list(queryConditions(notice)));
    }

    /**
     * 按原字段策略调用通用 save，回填自增主键；创建/日志时间使用服务端当前时间。
     * 新增公告
     *
     * @param notice 公告信息
     * @return 结果
     */
    @Override
    public int insertNotice(SysNotice notice) {
        return insertRecord(notice);
    }

    /**
     * 按原接口允许的列调用通用 update，保留空值策略及数据库更新时间，主键为空拒绝执行。
     * 修改公告
     *
     * @param notice 公告信息
     * @return 结果
     */
    @Override
    public int updateNotice(SysNotice notice) {
        return updateRecord(notice);
    }

    /**
     * 删除公告对象
     *
     * @param noticeId 公告ID
     * @return 结果
     */
    @Override
    public int deleteNoticeById(Long noticeId) {
        return baseMapper.deleteById(PersistenceChecks.id(noticeId));
    }

    /**
     * 主键集合经非空校验后使用通用批量删除，返回数据库实际影响行数。
     * 先校验公告编号集合非空，再删除同批公告的已读记录和公告；任一步失败时整体回滚。
     *
     * @param noticeIds 需要删除的公告ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteNoticeByIds(Long[] noticeIds) {
        PersistenceChecks.ids(noticeIds);
        if (noticeIds == null || noticeIds.length == 0) {
            throw new IllegalArgumentException("公告编号不能为空");
        }
        noticeReadMapper.delete(new QueryWrapper<SysNoticeRead>().in("notice_id", PersistenceChecks.ids(noticeIds)));
        return baseMapper.deleteByIds(PersistenceChecks.ids(noticeIds));
    }

    /** 显式设置新增时间为服务端当前时间，再按字段插入策略调用通用 save，返回单行写入结果。 */
    private int insertRecord(SysNotice value) {
        value.setCreateTime(new Date());
        value.setNoticeId(null);
        return save(value) ? 1 : 0;
    }

    /** 转换原列表允许的筛选项，再由 DynamicCondition 构造参数化条件，保留日期和排序规则。 */
    private QueryWrapper<SysNotice> queryConditions(SysNotice source) {
        SysNoticeQuery query = source == null ? new SysNoticeQuery() : PersistenceQueryConvert.INSTANCE.toSysNoticeQuery(source);
        QueryWrapper<SysNotice> wrapper = DynamicCondition.toWrapper(query);
        return wrapper.orderByDesc("notice_id");
    }
    /** 只更新公告正文等原接口允许的列；null 正文保留，空串可清空，主键为空拒绝执行。 */
    private int updateRecord(SysNotice value) {
        return update(new UpdateWrapper<SysNotice>().eq("notice_id", PersistenceChecks.id(value.getNoticeId()))
                .set(value.getNoticeTitle() != null && !value.getNoticeTitle().isEmpty(), "notice_title", value.getNoticeTitle())
                .set(value.getNoticeType() != null && !value.getNoticeType().isEmpty(), "notice_type", value.getNoticeType())
                .set(value.getNoticeContent() != null, "notice_content", value.getNoticeContent())
                .set(value.getStatus() != null && !value.getStatus().isEmpty(), "status", value.getStatus())
                .set(value.getUpdateBy() != null && !value.getUpdateBy().isEmpty(), "update_by", value.getUpdateBy())
                .setSql("update_time = CURRENT_TIMESTAMP")) ? 1 : 0;
    }
}
