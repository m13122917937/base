package com.ruoyi.system.service.impl;

import java.util.Date;
import com.ruoyi.system.model.query.SysDictTypeQuery;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ruoyi.common.mybatis.DynamicCondition;
import com.ruoyi.common.mybatis.PersistenceChecks;
import com.ruoyi.system.convert.PersistenceQueryConvert;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.common.core.page.PageDomain;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.core.domain.entity.SysDictType;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DictUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.mapper.SysDictDataMapper;
import com.ruoyi.system.mapper.SysDictTypeMapper;
import com.ruoyi.system.service.ISysDictTypeService;

/**
 * 字典 业务层处理
 *
 * @author ruoyi
 */
@Service
public class SysDictTypeServiceImpl extends ServiceImpl<SysDictTypeMapper, SysDictType> implements ISysDictTypeService {
    @Autowired
    private SysDictDataMapper dictDataMapper;

    /**
     * 项目启动时，初始化字典到缓存
     */
    @PostConstruct
    public void init() {
        loadingDictCache();
    }

    /**
     * 将允许的筛选项转换为 Query，经 DynamicCondition 构造条件，再调用通用 list；查询异常原样抛出。
     * 根据条件分页查询字典类型
     *
     * @param dictType 字典类型信息
     * @return 字典类型集合信息
     */
    @Override
    public List<SysDictType> selectDictTypeList(SysDictType dictType) {
        return list(queryConditions(dictType));
    }

    /**
     * 将允许的筛选项转换为 Query，经 DynamicCondition 构造条件，再调用通用 list；查询异常原样抛出。
     * 在目标 Mapper 查询前启动分页并在 finally 中清理线程状态，保留原过滤条件及数据权限。
     * 查询失败时异常原样向上传递，不新增事务；null 分页参数表示全量查询。
     *
     * @param dictType 原查询条件
     * @param pageDomain 通用分页参数，不读取 Servlet 请求
     * @return 保留 Page 元数据的查询列表
     */
    @Override
    public List<SysDictType> selectDictTypeList(SysDictType dictType, PageDomain pageDomain) {
        return PageUtils.startPage(pageDomain, () -> list(queryConditions(dictType)));
    }

    /**
     * 根据所有字典类型
     *
     * @return 字典类型集合信息
     */
    @Override
    public List<SysDictType> selectDictTypeAll() {
        return list(new QueryWrapper<SysDictType>());
    }

    /**
     * 根据字典类型查询字典数据
     *
     * @param dictType 字典类型
     * @return 字典数据集合信息
     */
    @Override
    public List<SysDictData> selectDictDataByType(String dictType) {
        List<SysDictData> dictDatas = DictUtils.getDictCache(dictType);
        if (StringUtils.isNotEmpty(dictDatas)) {
            return dictDatas;
        }
        dictDatas = dictDataMapper.selectList(new QueryWrapper<SysDictData>().eq("status", "0").eq("dict_type", dictType).orderByAsc("dict_sort"));
        if (StringUtils.isNotEmpty(dictDatas)) {
            DictUtils.setDictCache(dictType, dictDatas);
            return dictDatas;
        }
        return null;
    }

    /**
     * 通过通用 getById 查询本表，未命中返回 null；不执行关联加载。
     * 根据字典类型ID查询信息
     *
     * @param dictId 字典类型ID
     * @return 字典类型
     */
    @Override
    public SysDictType selectDictTypeById(Long dictId) {
        return getById(dictId);
    }

    /**
     * 使用绑定参数的通用 getOne 查询匹配记录，保持既有唯一性判断。
     * 根据字典类型查询信息
     *
     * @param dictType 字典类型
     * @return 字典类型
     */
    @Override
    public SysDictType selectDictTypeByType(String dictType) {
        return getOne(new QueryWrapper<SysDictType>().eq("dict_type", dictType));
    }

    /**
     * 批量删除字典类型信息
     *
     * @param dictIds 需要删除的字典ID
     */
    @Override
    public void deleteDictTypeByIds(Long[] dictIds) {
        PersistenceChecks.ids(dictIds);
        for (Long dictId : dictIds) {
            SysDictType dictType = selectDictTypeById(dictId);
            if (Math.toIntExact(dictDataMapper.selectCount(new QueryWrapper<SysDictData>().eq("dict_type", dictType.getDictType()))) > 0) {
                throw new ServiceException(String.format("%1$s已分配,不能删除", dictType.getDictName()));
            }
            baseMapper.deleteById(PersistenceChecks.id(dictId));
            DictUtils.removeDictCache(dictType.getDictType());
        }
    }

    /**
     * 加载字典缓存数据
     */
    @Override
    public void loadingDictCache() {
        SysDictData dictData = new SysDictData();
        dictData.setStatus("0");
        Map<String, List<SysDictData>> dictDataMap = dictDataMapper.selectList(DynamicCondition.<SysDictData>toWrapper(PersistenceQueryConvert.INSTANCE.toSysDictDataQuery(dictData)).orderByAsc("dict_sort")).stream().collect(Collectors.groupingBy(SysDictData::getDictType));
        for (Map.Entry<String, List<SysDictData>> entry : dictDataMap.entrySet()) {
            DictUtils.setDictCache(entry.getKey(), entry.getValue().stream().sorted(Comparator.comparing(SysDictData::getDictSort)).collect(Collectors.toList()));
        }
    }

    /**
     * 清空字典缓存数据
     */
    @Override
    public void clearDictCache() {
        DictUtils.clearDictCache();
    }

    /**
     * 重置字典缓存数据
     */
    @Override
    public void resetDictCache() {
        clearDictCache();
        loadingDictCache();
    }

    /**
     * 按原字段策略调用通用 save，回填自增主键；创建/日志时间使用服务端当前时间。
     * 新增保存字典类型信息
     *
     * @param dict 字典类型信息
     * @return 结果
     */
    @Override
    public int insertDictType(SysDictType dict) {
        int row = insertRecord(dict);
        if (row > 0) {
            DictUtils.setDictCache(dict.getDictType(), null);
        }
        return row;
    }

    /**
     * 按原接口允许的列调用通用 update，保留空值策略及数据库更新时间，主键为空拒绝执行。
     * 修改保存字典类型信息
     *
     * @param dict 字典类型信息
     * @return 结果
     */
    @Override
    @Transactional
    public int updateDictType(SysDictType dict) {
        SysDictType oldDict = getById(dict.getDictId());
        dictDataMapper.update(null, new UpdateWrapper<SysDictData>().set("dict_type", dict.getDictType()).eq("dict_type", PersistenceChecks.id(oldDict.getDictType())));
        int row = updateRecord(dict);
        if (row > 0) {
            List<SysDictData> dictDatas = dictDataMapper.selectList(new QueryWrapper<SysDictData>().eq("status", "0").eq("dict_type", dict.getDictType()).orderByAsc("dict_sort"));
            DictUtils.setDictCache(dict.getDictType(), dictDatas);
        }
        return row;
    }

    /**
     * 使用绑定参数查询同类型字典，排除当前 ID；存在其他记录时返回不唯一，查询异常向上传递。
     * 校验字典类型称是否唯一
     *
     * @param dict 字典类型
     * @return 结果
     */
    @Override
    public boolean checkDictTypeUnique(SysDictType dict) {
        return !exists(new QueryWrapper<SysDictType>().eq("dict_type", dict.getDictType())
                .ne(dict.getDictId() != null, "dict_id", dict.getDictId()));
    }

    /** 主键非空时调用通用 updateById；实体元数据保留原列的空值策略、只读审计列和数据库更新时间。 */
    private int updateRecord(SysDictType value) {
        PersistenceChecks.id(value.getDictId());
        return updateById(value) ? 1 : 0;
    }

    /** 显式设置新增时间为服务端当前时间，再按字段插入策略调用通用 save，返回单行写入结果。 */
    private int insertRecord(SysDictType value) {
        value.setCreateTime(new Date());
        value.setDictId(null);
        return save(value) ? 1 : 0;
    }

    /** 转换原列表允许的筛选项，再由 DynamicCondition 构造参数化条件，保留日期和排序规则。 */
    private QueryWrapper<SysDictType> queryConditions(SysDictType source) {
        SysDictTypeQuery query = source == null ? new SysDictTypeQuery() : PersistenceQueryConvert.INSTANCE.toSysDictTypeQuery(source);
        QueryWrapper<SysDictType> wrapper = DynamicCondition.toWrapper(query);
        wrapper.apply(query.getBeginTime() != null && !"".equals(query.getBeginTime()), "DATE_FORMAT(create_time, '%Y%m%d') >= DATE_FORMAT({0}, '%Y%m%d')", query.getBeginTime());
        wrapper.apply(query.getEndTime() != null && !"".equals(query.getEndTime()), "DATE_FORMAT(create_time, '%Y%m%d') <= DATE_FORMAT({0}, '%Y%m%d')", query.getEndTime());
        return wrapper;
    }
}
