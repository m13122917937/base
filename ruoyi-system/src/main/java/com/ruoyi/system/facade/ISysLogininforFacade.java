package com.ruoyi.system.facade;

import com.ruoyi.common.core.page.PageDomain;
import java.util.List;

import com.ruoyi.system.model.bo.SysLogininforBO;
import com.ruoyi.system.model.bo.*;

/**
 * 稳定领域能力入口，隐藏 Service、Entity 和 Mapper。
 */
public interface ISysLogininforFacade {
    /**
     * selectLogininforList：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param logininfor logininfor 领域参数
     * @return 原领域能力的处理结果
     */
    List<SysLogininforBO> queryLogininforList(SysLogininforBO logininfor);

    /**
     * 将显式分页参数传给本领域 Service，并在边界转换时保留数据库总数。
     * 查询失败时异常原样向上传递，不新增事务；null 分页参数表示全量查询。
     *
     * @param logininfor 原查询条件
     * @param pageDomain 通用分页参数，不读取 Servlet 请求
     * @return 保留 Page 元数据的查询列表
     */
    List<SysLogininforBO> queryLogininforList(SysLogininforBO logininfor, PageDomain pageDomain);

    /**
     * deleteLogininforByIds：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param infoIds infoIds 领域参数
     * @return 原领域能力的处理结果
     */
    int removeLogininforByIds(Long[] infoIds);

    /**
     * cleanLogininfor：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     */
    void cleanLogininfor();
}
