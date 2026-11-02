package com.ruoyi.system.service.impl;

import java.util.Date;
import com.ruoyi.system.model.query.SysDictDataQuery;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.mybatis.DynamicCondition;
import com.ruoyi.common.mybatis.PersistenceChecks;
import com.ruoyi.system.convert.PersistenceQueryConvert;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.common.core.page.PageDomain;
import java.util.List;

import org.springframework.stereotype.Service;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.utils.DictUtils;
import com.ruoyi.system.mapper.SysDictDataMapper;
import com.ruoyi.system.service.ISysDictDataService;

/**
 * 字典 业务层处理
 *
 * @author ruoyi
 */
@Service
public class SysDictDataServiceImpl extends ServiceImpl<SysDictDataMapper, SysDictData> implements ISysDictDataService {
    /**
     * 将允许的筛选项转换为 Query，经 DynamicCondition 构造条件，再调用通用 list；查询异常原样抛出。
     * 根据条件分页查询字典数据
     *
     * @param dictData 字典数据信息
     * @return 字典数据集合信息
     */
    @Override
    public List<SysDictData> selectDictDataList(SysDictData dictData) {
        return list(queryConditions(dictData));
    }

    /**
     * 将允许的筛选项转换为 Query，经 DynamicCondition 构造条件，再调用通用 list；查询异常原样抛出。
     * 在目标 Mapper 查询前启动分页并在 finally 中清理线程状态，保留原过滤条件及数据权限。
     * 查询失败时异常原样向上传递，不新增事务；null 分页参数表示全量查询。
     *
     * @param dictData 原查询条件
     * @param pageDomain 通用分页参数，不读取 Servlet 请求
     * @return 保留 Page 元数据的查询列表
     */
    @Override
    public List<SysDictData> selectDictDataList(SysDictData dictData, PageDomain pageDomain) {
        return PageUtils.startPage(pageDomain, () -> list(queryConditions(dictData)));
    }

    /**
     * 根据字典类型和字典键值查询字典数据信息
     *
     * @param dictType  字典类型
     * @param dictValue 字典键值
     * @return 字典标签
     */
    @Override
    public String selectDictLabel(String dictType, String dictValue) {
        return selectLabel(dictType, dictValue);
    }

    /**
     * 通过通用 getById 查询本表，未命中返回 null；不执行关联加载。
     * 根据字典数据ID查询信息
     *
     * @param dictCode 字典数据ID
     * @return 字典数据
     */
    @Override
    public SysDictData selectDictDataById(Long dictCode) {
        return getById(dictCode);
    }

    /**
     * 批量删除字典数据信息
     *
     * @param dictCodes 需要删除的字典数据ID
     */
    @Override
    public void deleteDictDataByIds(Long[] dictCodes) {
        PersistenceChecks.ids(dictCodes);
        for (Long dictCode : dictCodes) {
            SysDictData data = selectDictDataById(dictCode);
            baseMapper.deleteById(PersistenceChecks.id(dictCode));
            List<SysDictData> dictDatas = list(new QueryWrapper<SysDictData>().eq("status", "0").eq("dict_type", data.getDictType()).orderByAsc("dict_sort"));
            DictUtils.setDictCache(data.getDictType(), dictDatas);
        }
    }

    /**
     * 按原字段策略调用通用 save，回填自增主键；创建/日志时间使用服务端当前时间。
     * 新增保存字典数据信息
     *
     * @param data 字典数据信息
     * @return 结果
     */
    @Override
    public int insertDictData(SysDictData data) {
        int row = insertRecord(data);
        if (row > 0) {
            List<SysDictData> dictDatas = list(new QueryWrapper<SysDictData>().eq("status", "0").eq("dict_type", data.getDictType()).orderByAsc("dict_sort"));
            DictUtils.setDictCache(data.getDictType(), dictDatas);
        }
        return row;
    }

    /**
     * 按原接口允许的列调用通用 update，保留空值策略及数据库更新时间，主键为空拒绝执行。
     * 修改保存字典数据信息
     *
     * @param data 字典数据信息
     * @return 结果
     */
    @Override
    public int updateDictData(SysDictData data) {
        int row = updateRecord(data);
        if (row > 0) {
            List<SysDictData> dictDatas = list(new QueryWrapper<SysDictData>().eq("status", "0").eq("dict_type", data.getDictType()).orderByAsc("dict_sort"));
            DictUtils.setDictCache(data.getDictType(), dictDatas);
        }
        return row;
    }

    /** 主键非空时调用通用 updateById；实体元数据保留原列的空值策略、只读审计列和数据库更新时间。 */
    private int updateRecord(SysDictData value) {
        PersistenceChecks.id(value.getDictCode());
        return updateById(value) ? 1 : 0;
    }

    /** 显式设置新增时间为服务端当前时间，再按字段插入策略调用通用 save，返回单行写入结果。 */
    private int insertRecord(SysDictData value) {
        value.setCreateTime(new Date());
        value.setDictCode(null);
        return save(value) ? 1 : 0;
    }

    /** 转换原列表允许的筛选项，再由 DynamicCondition 构造参数化条件，保留日期和排序规则。 */
    private QueryWrapper<SysDictData> queryConditions(SysDictData source) {
        SysDictDataQuery query = source == null ? new SysDictDataQuery() : PersistenceQueryConvert.INSTANCE.toSysDictDataQuery(source);
        QueryWrapper<SysDictData> wrapper = DynamicCondition.toWrapper(query);
        return wrapper.orderByAsc("dict_sort");
    }

    /** 使用通用 getOne 查询唯一字典标签，缺失返回 null，重复记录按原 selectOne 语义报错。 */
    private String selectLabel(String type, String value) {
        SysDictData record = getOne(new QueryWrapper<SysDictData>().eq("dict_type", type).eq("dict_value", value));
        return record == null ? null : record.getDictLabel();
    }
}
