package com.ruoyi.biz.system;

import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;

import java.util.List;

import com.ruoyi.system.model.bo.SysDictTypeBO;
import com.ruoyi.system.facade.ISysDictTypeFacade;
import org.springframework.stereotype.Component;
import com.ruoyi.system.model.bo.*;
import com.ruoyi.framework.model.bo.*;

/**
 * SysDictTypeController 对应的应用编排，保留原业务执行顺序与响应语义。
 */
@Component
public class SysDictTypeBiz {

    private final ISysDictTypeFacade dictTypeFacade;

    /**
     * 接收应用编排所需的领域 Facade 和基础设施，不在构造阶段访问外部系统。
     */
    public SysDictTypeBiz(ISysDictTypeFacade dictTypeFacade) {
        this.dictTypeFacade = dictTypeFacade;
    }

    /**
     * 传递显式分页参数，不创建线程分页状态；保留原业务校验顺序。
     * list：按原顺序执行 selectDictTypeList、返回列表等步骤，并保留原判断、返回及异常语义。
     *
     * @param dictType dictType 请求参数或领域数据
     * @param pageDomain 通用分页参数，显式传给 Facade，由 Service 启动分页
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public List<SysDictTypeBO> list(SysDictTypeBO dictType, PageDomain pageDomain) {
        List<SysDictTypeBO> list = dictTypeFacade.queryDictTypeList(dictType, pageDomain);
        return list;
    }

    /**
     * export：按原顺序执行 selectDictTypeList、返回导出数据 等步骤，并保留原判断、返回及异常语义。
     *
     * @param dictType dictType 请求参数或领域数据
     * @return 当前过滤条件和数据权限范围内的导出数据，由 Web 层编码为 Excel
     */
    public List<SysDictTypeBO> export(SysDictTypeBO dictType) {
        return dictTypeFacade.queryDictTypeList(dictType);
    }

    /**
     * 查询字典类型详细：按原顺序执行 selectDictTypeById 等步骤，并保留原判断、返回及异常语义。
     *
     * @param dictId dictId 请求参数或领域数据
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public SysDictTypeBO getInfo(Long dictId) {
        return dictTypeFacade.queryDictTypeById(dictId);
    }

    /**
     * 新增字典类型：按原顺序执行 checkDictTypeUnique、getDictName、setCreateBy、getUsername、insertDictType 等步骤，并保留原判断、返回及异常语义。
     *
     * @param dict dict 请求参数或领域数据
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public int add(SysDictTypeBO dict) {
        if (!dictTypeFacade.checkDictTypeUnique(dict)) {
            throw new ServiceException("新增字典'" + dict.getDictName() + "'失败，字典类型已存在");
        }
        dict.setCreateBy(SecurityUtils.getUsername());
        return dictTypeFacade.createDictType(dict);
    }

    /**
     * 修改字典类型：按原顺序执行 checkDictTypeUnique、getDictName、setUpdateBy、getUsername、updateDictType 等步骤，并保留原判断、返回及异常语义。
     *
     * @param dict dict 请求参数或领域数据
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public int edit(SysDictTypeBO dict) {
        if (!dictTypeFacade.checkDictTypeUnique(dict)) {
            throw new ServiceException("修改字典'" + dict.getDictName() + "'失败，字典类型已存在");
        }
        dict.setUpdateBy(SecurityUtils.getUsername());
        return dictTypeFacade.modifyDictType(dict);
    }

    /**
     * 删除字典类型：按原顺序执行 deleteDictTypeByIds、业务动作等步骤，并保留原判断、返回及异常语义。
     *
     * @param dictIds dictIds 请求参数或领域数据
     */
    public void remove(Long[] dictIds) {
        dictTypeFacade.removeDictTypeByIds(dictIds);
    }

    /**
     * 刷新字典缓存：按原顺序执行 resetDictCache、业务动作等步骤，并保留原判断、返回及异常语义。
     */
    public void refreshCache() {
        dictTypeFacade.resetDictCache();
    }

    /**
     * 获取字典选择框列表：按原顺序执行 selectDictTypeAll、业务动作等步骤，并保留原判断、返回及异常语义。
     *
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public List<SysDictTypeBO> optionselect() {
        List<SysDictTypeBO> dictTypes = dictTypeFacade.queryDictTypeAll();
        return dictTypes;
    }
}
