package com.ruoyi.system.facade.impl;

import com.ruoyi.common.core.page.PageDomain;
import java.util.List;

import com.ruoyi.system.model.bo.SysDictDataBO;
import com.ruoyi.system.model.bo.SysDictTypeBO;
import com.ruoyi.system.convert.BoundaryConvert;
import com.ruoyi.common.utils.BoundaryPages;
import com.ruoyi.system.model.bo.*;
import org.springframework.stereotype.Component;
import com.ruoyi.system.service.ISysDictTypeService;
import com.ruoyi.system.facade.ISysDictTypeFacade;

/**
 * 本领域的边界转换和 Service 编排，不声明事务。
 */
@Component
public class SysDictTypeFacade implements ISysDictTypeFacade {
    private final ISysDictTypeService service;

    /**
     * 接收本领域 Service，建立唯一持久化调用路径，不执行查询或写入。
     *
     * @param service 本领域业务服务
     */
    public SysDictTypeFacade(ISysDictTypeService service) {
        this.service = service;
    }

    /**
     * selectDictTypeList：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param dictType dictType 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public List<SysDictTypeBO> queryDictTypeList(SysDictTypeBO dictType) {
        return BoundaryPages.map(service.selectDictTypeList(BoundaryConvert.INSTANCE.toSysDictType(dictType)), BoundaryConvert.INSTANCE::toSysDictTypeBOList);
    }

    /**
     * 将显式分页参数传给本领域 Service，并在边界转换时保留数据库总数。
     * 查询失败时异常原样向上传递，不新增事务；null 分页参数表示全量查询。
     *
     * @param dictType 原查询条件
     * @param pageDomain 通用分页参数，不读取 Servlet 请求
     * @return 保留 Page 元数据的查询列表
     */
    @Override
    public List<SysDictTypeBO> queryDictTypeList(SysDictTypeBO dictType, PageDomain pageDomain) {
        return BoundaryPages.map(service.selectDictTypeList(BoundaryConvert.INSTANCE.toSysDictType(dictType), pageDomain), BoundaryConvert.INSTANCE::toSysDictTypeBOList);
    }

    /**
     * selectDictTypeAll：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @return 原领域能力的处理结果
     */
    @Override
    public List<SysDictTypeBO> queryDictTypeAll() {
        return BoundaryPages.map(service.selectDictTypeAll(), BoundaryConvert.INSTANCE::toSysDictTypeBOList);
    }

    /**
     * selectDictDataByType：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param dictType dictType 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public List<SysDictDataBO> queryDictDataByType(String dictType) {
        return BoundaryPages.map(service.selectDictDataByType(dictType), BoundaryConvert.INSTANCE::toSysDictDataBOList);
    }

    /**
     * selectDictTypeById：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param dictId dictId 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public SysDictTypeBO queryDictTypeById(Long dictId) {
        return BoundaryConvert.INSTANCE.toSysDictTypeBO(service.selectDictTypeById(dictId));
    }

    /**
     * deleteDictTypeByIds：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param dictIds dictIds 领域参数
     */
    @Override
    public void removeDictTypeByIds(Long[] dictIds) {
        service.deleteDictTypeByIds(dictIds);
    }

    /**
     * resetDictCache：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     */
    @Override
    public void resetDictCache() {
        service.resetDictCache();
    }

    /**
     * insertDictType：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param dictType dictType 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public int createDictType(SysDictTypeBO dictType) {
        return service.insertDictType(BoundaryConvert.INSTANCE.toSysDictType(dictType));
    }

    /**
     * updateDictType：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param dictType dictType 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public int modifyDictType(SysDictTypeBO dictType) {
        return service.updateDictType(BoundaryConvert.INSTANCE.toSysDictType(dictType));
    }

    /**
     * checkDictTypeUnique：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param dictType dictType 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public boolean checkDictTypeUnique(SysDictTypeBO dictType) {
        return service.checkDictTypeUnique(BoundaryConvert.INSTANCE.toSysDictType(dictType));
    }
}
