package com.ruoyi.biz.system;

import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.utils.SecurityUtils;

import java.util.ArrayList;
import java.util.List;

import com.ruoyi.system.model.bo.SysDictDataBO;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.facade.ISysDictDataFacade;
import com.ruoyi.system.facade.ISysDictTypeFacade;
import org.springframework.stereotype.Component;
import com.ruoyi.system.model.bo.*;
import com.ruoyi.framework.model.bo.*;

/**
 * SysDictDataController 对应的应用编排，保留原业务执行顺序与响应语义。
 */
@Component
public class SysDictDataBiz {

    private final ISysDictDataFacade dictDataFacade;

    private final ISysDictTypeFacade dictTypeFacade;

    /**
     * 接收应用编排所需的领域 Facade 和基础设施，不在构造阶段访问外部系统。
     */
    public SysDictDataBiz(ISysDictDataFacade dictDataFacade, ISysDictTypeFacade dictTypeFacade) {
        this.dictDataFacade = dictDataFacade;
        this.dictTypeFacade = dictTypeFacade;
    }

    /**
     * 传递显式分页参数，不创建线程分页状态；保留原业务校验顺序。
     * list：按原顺序执行 selectDictDataList、返回列表等步骤，并保留原判断、返回及异常语义。
     *
     * @param dictData dictData 请求参数或领域数据
     * @param pageDomain 通用分页参数，显式传给 Facade，由 Service 启动分页
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public List<SysDictDataBO> list(SysDictDataBO dictData, PageDomain pageDomain) {
        List<SysDictDataBO> list = dictDataFacade.queryDictDataList(dictData, pageDomain);
        return list;
    }

    /**
     * export：按原顺序执行 selectDictDataList、返回导出数据 等步骤，并保留原判断、返回及异常语义。
     *
     * @param dictData dictData 请求参数或领域数据
     * @return 当前过滤条件和数据权限范围内的导出数据，由 Web 层编码为 Excel
     */
    public List<SysDictDataBO> export(SysDictDataBO dictData) {
        return dictDataFacade.queryDictDataList(dictData);
    }

    /**
     * 查询字典数据详细：按原顺序执行 selectDictDataById 等步骤，并保留原判断、返回及异常语义。
     *
     * @param dictCode dictCode 请求参数或领域数据
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public SysDictDataBO getInfo(Long dictCode) {
        return dictDataFacade.queryDictDataById(dictCode);
    }

    /**
     * 根据字典类型查询字典数据信息：按原顺序执行 selectDictDataByType、isNull、业务动作等步骤，并保留原判断、返回及异常语义。
     *
     * @param dictType dictType 请求参数或领域数据
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public List<SysDictDataBO> dictType(String dictType) {
        List<SysDictDataBO> data = dictTypeFacade.queryDictDataByType(dictType);
        if (StringUtils.isNull(data)) {
            data = new ArrayList<SysDictDataBO>();
        }
        return data;
    }

    /**
     * 新增字典类型：按原顺序执行 setCreateBy、getUsername、insertDictData 等步骤，并保留原判断、返回及异常语义。
     *
     * @param dict dict 请求参数或领域数据
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public int add(SysDictDataBO dict) {
        dict.setCreateBy(SecurityUtils.getUsername());
        return dictDataFacade.createDictData(dict);
    }

    /**
     * 修改保存字典类型：按原顺序执行 setUpdateBy、getUsername、updateDictData 等步骤，并保留原判断、返回及异常语义。
     *
     * @param dict dict 请求参数或领域数据
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public int edit(SysDictDataBO dict) {
        dict.setUpdateBy(SecurityUtils.getUsername());
        return dictDataFacade.modifyDictData(dict);
    }

    /**
     * 删除字典类型：按原顺序执行 deleteDictDataByIds、业务动作等步骤，并保留原判断、返回及异常语义。
     *
     * @param dictCodes dictCodes 请求参数或领域数据
     */
    public void remove(Long[] dictCodes) {
        dictDataFacade.removeDictDataByIds(dictCodes);
    }
}
