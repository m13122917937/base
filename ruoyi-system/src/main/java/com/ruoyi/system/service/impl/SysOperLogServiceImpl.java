package com.ruoyi.system.service.impl;

import java.util.Date;
import com.ruoyi.system.model.query.SysOperLogQuery;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.mybatis.DynamicCondition;
import com.ruoyi.common.mybatis.PersistenceChecks;
import com.ruoyi.system.convert.PersistenceQueryConvert;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.common.core.page.PageDomain;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.SysOperLog;
import com.ruoyi.system.mapper.SysOperLogMapper;
import com.ruoyi.system.service.ISysOperLogService;

/**
 * 操作日志 服务层处理
 *
 * @author ruoyi
 */
@Service
public class SysOperLogServiceImpl extends ServiceImpl<SysOperLogMapper, SysOperLog> implements ISysOperLogService {
    @Autowired
    private SysOperLogMapper operLogMapper;

    /**
     * 显式设置操作时间为服务端当前时间，再按原字段策略调用通用 save 并回填自增主键。
     * 新增操作日志
     *
     * @param operLog 操作日志对象
     */
    @Override
    public void insertOperlog(SysOperLog operLog) {
        operLog.setOperId(null);
        operLog.setOperTime(new Date());
        save(operLog);
    }

    /**
     * 将允许的筛选项转换为 Query，经 DynamicCondition 构造条件，再调用通用 list；查询异常原样抛出。
     * 查询系统操作日志集合
     *
     * @param operLog 操作日志对象
     * @return 操作日志集合
     */
    @Override
    public List<SysOperLog> selectOperLogList(SysOperLog operLog) {
        return list(queryConditions(operLog));
    }

    /**
     * 将允许的筛选项转换为 Query，经 DynamicCondition 构造条件，再调用通用 list；查询异常原样抛出。
     * 在目标 Mapper 查询前启动分页并在 finally 中清理线程状态，保留原过滤条件及数据权限。
     * 查询失败时异常原样向上传递，不新增事务；null 分页参数表示全量查询。
     *
     * @param operLog 原查询条件
     * @param pageDomain 通用分页参数，不读取 Servlet 请求
     * @return 保留 Page 元数据的查询列表
     */
    @Override
    public List<SysOperLog> selectOperLogList(SysOperLog operLog, PageDomain pageDomain) {
        return PageUtils.startPage(pageDomain, () -> list(queryConditions(operLog)));
    }

    /**
     * 主键集合经非空校验后使用通用批量删除，返回数据库实际影响行数。
     * 批量删除系统操作日志
     *
     * @param operIds 需要删除的操作日志ID
     * @return 结果
     */
    @Override
    public int deleteOperLogByIds(Long[] operIds) {
        PersistenceChecks.ids(operIds);
        return baseMapper.deleteByIds(PersistenceChecks.ids(operIds));
    }

    /**
     * 通过通用 getById 查询本表，未命中返回 null；不执行关联加载。
     * 查询操作日志详细
     *
     * @param operId 操作ID
     * @return 操作日志对象
     */
    @Override
    public SysOperLog selectOperLogById(Long operId) {
        return getById(operId);
    }

    /**
     * 清空操作日志
     */
    @Override
    public void cleanOperLog() {
        operLogMapper.cleanOperLog();
    }

    /** 转换原列表允许的筛选项，再由 DynamicCondition 构造参数化条件，保留日期和排序规则。 */
    private QueryWrapper<SysOperLog> queryConditions(SysOperLog source) {
        SysOperLogQuery query = source == null ? new SysOperLogQuery() : PersistenceQueryConvert.INSTANCE.toSysOperLogQuery(source);
        QueryWrapper<SysOperLog> wrapper = DynamicCondition.toWrapper(query);
        wrapper.apply(query.getBeginTime() != null && !"".equals(query.getBeginTime()), "oper_time >= {0}", query.getBeginTime());
        wrapper.apply(query.getEndTime() != null && !"".equals(query.getEndTime()), "oper_time <= {0}", query.getEndTime());
        return wrapper.orderByDesc("oper_id");
    }
}
