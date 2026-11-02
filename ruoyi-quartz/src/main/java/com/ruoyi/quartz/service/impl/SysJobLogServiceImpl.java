package com.ruoyi.quartz.service.impl;

import java.util.Date;
import com.ruoyi.quartz.model.query.SysJobLogQuery;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.mybatis.DynamicCondition;
import com.ruoyi.common.mybatis.PersistenceChecks;
import com.ruoyi.quartz.convert.PersistenceQueryConvert;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.common.core.page.PageDomain;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.quartz.domain.SysJobLog;
import com.ruoyi.quartz.mapper.SysJobLogMapper;
import com.ruoyi.quartz.service.ISysJobLogService;

/**
 * 定时任务调度日志信息 服务层
 *
 * @author ruoyi
 */
@Service
public class SysJobLogServiceImpl extends ServiceImpl<SysJobLogMapper, SysJobLog> implements ISysJobLogService {
    @Autowired
    private SysJobLogMapper jobLogMapper;

    /**
     * 将允许的筛选项转换为 Query，经 DynamicCondition 构造条件，再调用通用 list；查询异常原样抛出。
     * 获取quartz调度器日志的计划任务
     *
     * @param jobLog 调度日志信息
     * @return 调度任务日志集合
     */
    @Override
    public List<SysJobLog> selectJobLogList(SysJobLog jobLog) {
        return list(queryConditions(jobLog));
    }

    /**
     * 将允许的筛选项转换为 Query，经 DynamicCondition 构造条件，再调用通用 list；查询异常原样抛出。
     * 在目标 Mapper 查询前启动分页并在 finally 中清理线程状态，保留原过滤条件及数据权限。
     * 查询失败时异常原样向上传递，不新增事务；null 分页参数表示全量查询。
     *
     * @param jobLog 原查询条件
     * @param pageDomain 通用分页参数，不读取 Servlet 请求
     * @return 保留 Page 元数据的查询列表
     */
    @Override
    public List<SysJobLog> selectJobLogList(SysJobLog jobLog, PageDomain pageDomain) {
        return PageUtils.startPage(pageDomain, () -> list(queryConditions(jobLog)));
    }

    /**
     * 通过通用 getById 查询本表，未命中返回 null；不执行关联加载。
     * 通过调度任务日志ID查询调度信息
     *
     * @param jobLogId 调度任务日志ID
     * @return 调度任务日志对象信息
     */
    @Override
    public SysJobLog selectJobLogById(Long jobLogId) {
        return getById(jobLogId);
    }

    /**
     * 按原字段策略调用通用 save，回填自增主键；创建/日志时间使用服务端当前时间。
     * 新增任务日志
     *
     * @param jobLog 调度日志信息
     */
    @Override
    public void addJobLog(SysJobLog jobLog) {
        insertRecord(jobLog);
    }

    /**
     * 主键集合经非空校验后使用通用批量删除，返回数据库实际影响行数。
     * 批量删除调度日志信息
     *
     * @param logIds 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteJobLogByIds(Long[] logIds) {
        PersistenceChecks.ids(logIds);
        return baseMapper.deleteByIds(PersistenceChecks.ids(logIds));
    }

    /**
     * 删除任务日志
     *
     * @param jobId 调度日志ID
     */
    @Override
    public int deleteJobLogById(Long jobId) {
        return baseMapper.deleteById(PersistenceChecks.id(jobId));
    }

    /**
     * 清空任务日志
     */
    @Override
    public void cleanJobLog() {
        jobLogMapper.cleanJobLog();
    }

    /** 显式设置新增时间为服务端当前时间，再按字段插入策略调用通用 save，返回单行写入结果。 */
    private int insertRecord(SysJobLog value) {
        value.setCreateTime(new Date());
        if (value.getJobLogId() != null && value.getJobLogId() == 0) {
            value.setJobLogId(null);
        }
        return save(value) ? 1 : 0;
    }

    /** 转换原列表允许的筛选项，再由 DynamicCondition 构造参数化条件，保留日期和排序规则。 */
    private QueryWrapper<SysJobLog> queryConditions(SysJobLog source) {
        SysJobLogQuery query = source == null ? new SysJobLogQuery() : PersistenceQueryConvert.INSTANCE.toSysJobLogQuery(source);
        QueryWrapper<SysJobLog> wrapper = DynamicCondition.toWrapper(query);
        wrapper.apply(query.getBeginTime() != null && !"".equals(query.getBeginTime()), "DATE_FORMAT(create_time, '%Y%m%d') >= DATE_FORMAT({0}, '%Y%m%d')", query.getBeginTime());
        wrapper.apply(query.getEndTime() != null && !"".equals(query.getEndTime()), "DATE_FORMAT(create_time, '%Y%m%d') <= DATE_FORMAT({0}, '%Y%m%d')", query.getEndTime());
        return wrapper.orderByDesc("create_time");
    }
}
