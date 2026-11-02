package com.ruoyi.system.facade.impl;

import com.ruoyi.common.core.page.PageDomain;
import java.util.List;

import com.ruoyi.system.model.bo.SysDictDataBO;
import com.ruoyi.system.convert.BoundaryConvert;
import com.ruoyi.common.utils.BoundaryPages;
import com.ruoyi.system.model.bo.*;
import org.springframework.stereotype.Component;
import com.ruoyi.system.service.ISysDictDataService;
import com.ruoyi.system.facade.ISysDictDataFacade;

/**
 * 本领域的边界转换和 Service 编排，不声明事务。
 */
@Component
public class SysDictDataFacade implements ISysDictDataFacade {
    private final ISysDictDataService service;

    /**
     * 接收本领域 Service，建立唯一持久化调用路径，不执行查询或写入。
     *
     * @param service 本领域业务服务
     */
    public SysDictDataFacade(ISysDictDataService service) {
        this.service = service;
    }

    /**
     * selectDictDataList：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param dictData dictData 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public List<SysDictDataBO> queryDictDataList(SysDictDataBO dictData) {
        return BoundaryPages.map(service.selectDictDataList(BoundaryConvert.INSTANCE.toSysDictData(dictData)), BoundaryConvert.INSTANCE::toSysDictDataBOList);
    }

    /**
     * 将显式分页参数传给本领域 Service，并在边界转换时保留数据库总数。
     * 查询失败时异常原样向上传递，不新增事务；null 分页参数表示全量查询。
     *
     * @param dictData 原查询条件
     * @param pageDomain 通用分页参数，不读取 Servlet 请求
     * @return 保留 Page 元数据的查询列表
     */
    @Override
    public List<SysDictDataBO> queryDictDataList(SysDictDataBO dictData, PageDomain pageDomain) {
        return BoundaryPages.map(service.selectDictDataList(BoundaryConvert.INSTANCE.toSysDictData(dictData), pageDomain), BoundaryConvert.INSTANCE::toSysDictDataBOList);
    }

    /**
     * selectDictDataById：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param dictCode dictCode 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public SysDictDataBO queryDictDataById(Long dictCode) {
        return BoundaryConvert.INSTANCE.toSysDictDataBO(service.selectDictDataById(dictCode));
    }

    /**
     * deleteDictDataByIds：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param dictCodes dictCodes 领域参数
     */
    @Override
    public void removeDictDataByIds(Long[] dictCodes) {
        service.deleteDictDataByIds(dictCodes);
    }

    /**
     * insertDictData：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param dictData dictData 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public int createDictData(SysDictDataBO dictData) {
        return service.insertDictData(BoundaryConvert.INSTANCE.toSysDictData(dictData));
    }

    /**
     * updateDictData：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param dictData dictData 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public int modifyDictData(SysDictDataBO dictData) {
        return service.updateDictData(BoundaryConvert.INSTANCE.toSysDictData(dictData));
    }
}
