package com.ruoyi.system.facade;

import java.util.List;

import com.ruoyi.common.core.domain.TreeSelect;
import com.ruoyi.system.model.bo.SysDeptBO;
import com.ruoyi.system.model.bo.*;

/**
 * 稳定领域能力入口，隐藏 Service、Entity 和 Mapper。
 */
public interface ISysDeptFacade {
    /**
     * selectDeptList：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param dept dept 领域参数
     * @return 原领域能力的处理结果
     */
    List<SysDeptBO> queryDeptList(SysDeptBO dept);

    /**
     * selectDeptTreeList：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param dept dept 领域参数
     * @return 原领域能力的处理结果
     */
    List<TreeSelect> queryDeptTreeList(SysDeptBO dept);

    /**
     * selectDeptListByRoleId：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param roleId roleId 领域参数
     * @return 原领域能力的处理结果
     */
    List<Long> queryDeptListByRoleId(Long roleId);

    /**
     * selectDeptById：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param deptId deptId 领域参数
     * @return 原领域能力的处理结果
     */
    SysDeptBO queryDeptById(Long deptId);

    /**
     * selectNormalChildrenDeptById：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param deptId deptId 领域参数
     * @return 原领域能力的处理结果
     */
    int queryNormalChildrenDeptById(Long deptId);

    /**
     * hasChildByDeptId：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param deptId deptId 领域参数
     * @return 原领域能力的处理结果
     */
    boolean hasChildByDeptId(Long deptId);

    /**
     * checkDeptExistUser：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param deptId deptId 领域参数
     * @return 原领域能力的处理结果
     */
    boolean checkDeptExistUser(Long deptId);

    /**
     * checkDeptNameUnique：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param dept dept 领域参数
     * @return 原领域能力的处理结果
     */
    boolean checkDeptNameUnique(SysDeptBO dept);

    /**
     * checkDeptDataScope：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param deptId deptId 领域参数
     */
    void checkDeptDataScope(Long deptId);

    /**
     * insertDept：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param dept dept 领域参数
     * @return 原领域能力的处理结果
     */
    int createDept(SysDeptBO dept);

    /**
     * updateDept：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param dept dept 领域参数
     * @return 原领域能力的处理结果
     */
    int modifyDept(SysDeptBO dept);

    /**
     * updateDeptSort：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param deptIds   deptIds 领域参数
     * @param orderNums orderNums 领域参数
     */
    void modifyDeptSort(String[] deptIds, String[] orderNums);

    /**
     * deleteDeptById：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param deptId deptId 领域参数
     * @return 原领域能力的处理结果
     */
    int removeDeptById(Long deptId);
}
