package com.ruoyi.system.facade.impl;

import java.util.List;

import com.ruoyi.common.core.domain.TreeSelect;
import com.ruoyi.system.model.bo.SysDeptBO;
import com.ruoyi.system.convert.BoundaryConvert;
import com.ruoyi.common.utils.BoundaryPages;
import com.ruoyi.system.model.bo.*;
import org.springframework.stereotype.Component;
import com.ruoyi.system.service.ISysDeptService;
import com.ruoyi.system.facade.ISysDeptFacade;

/**
 * 本领域的边界转换和 Service 编排，不声明事务。
 */
@Component
public class SysDeptFacade implements ISysDeptFacade {
    private final ISysDeptService service;

    /**
     * 接收本领域 Service，建立唯一持久化调用路径，不执行查询或写入。
     *
     * @param service 本领域业务服务
     */
    public SysDeptFacade(ISysDeptService service) {
        this.service = service;
    }

    /**
     * selectDeptList：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param dept dept 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public List<SysDeptBO> queryDeptList(SysDeptBO dept) {
        return BoundaryPages.map(service.selectDeptList(BoundaryConvert.INSTANCE.toSysDept(dept)), BoundaryConvert.INSTANCE::toSysDeptBOList);
    }

    /**
     * selectDeptTreeList：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param dept dept 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public List<TreeSelect> queryDeptTreeList(SysDeptBO dept) {
        return service.selectDeptTreeList(BoundaryConvert.INSTANCE.toSysDept(dept));
    }

    /**
     * selectDeptListByRoleId：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param roleId roleId 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public List<Long> queryDeptListByRoleId(Long roleId) {
        return service.selectDeptListByRoleId(roleId);
    }

    /**
     * selectDeptById：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param deptId deptId 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public SysDeptBO queryDeptById(Long deptId) {
        return BoundaryConvert.INSTANCE.toSysDeptBO(service.selectDeptById(deptId));
    }

    /**
     * selectNormalChildrenDeptById：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param deptId deptId 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public int queryNormalChildrenDeptById(Long deptId) {
        return service.selectNormalChildrenDeptById(deptId);
    }

    /**
     * hasChildByDeptId：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param deptId deptId 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public boolean hasChildByDeptId(Long deptId) {
        return service.hasChildByDeptId(deptId);
    }

    /**
     * checkDeptExistUser：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param deptId deptId 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public boolean checkDeptExistUser(Long deptId) {
        return service.checkDeptExistUser(deptId);
    }

    /**
     * checkDeptNameUnique：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param dept dept 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public boolean checkDeptNameUnique(SysDeptBO dept) {
        return service.checkDeptNameUnique(BoundaryConvert.INSTANCE.toSysDept(dept));
    }

    /**
     * checkDeptDataScope：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param deptId deptId 领域参数
     */
    @Override
    public void checkDeptDataScope(Long deptId) {
        service.checkDeptDataScope(deptId);
    }

    /**
     * insertDept：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param dept dept 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public int createDept(SysDeptBO dept) {
        return service.insertDept(BoundaryConvert.INSTANCE.toSysDept(dept));
    }

    /**
     * updateDept：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param dept dept 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public int modifyDept(SysDeptBO dept) {
        return service.updateDept(BoundaryConvert.INSTANCE.toSysDept(dept));
    }

    /**
     * updateDeptSort：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param deptIds   deptIds 领域参数
     * @param orderNums orderNums 领域参数
     */
    @Override
    public void modifyDeptSort(String[] deptIds, String[] orderNums) {
        service.updateDeptSort(deptIds, orderNums);
    }

    /**
     * deleteDeptById：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param deptId deptId 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public int removeDeptById(Long deptId) {
        return service.deleteDeptById(deptId);
    }
}
