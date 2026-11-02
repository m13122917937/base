package com.ruoyi.biz.system;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.system.model.bo.SysDeptBO;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.facade.ISysDeptFacade;
import org.springframework.stereotype.Component;
import com.ruoyi.system.model.bo.*;
import com.ruoyi.framework.model.bo.*;

/**
 * SysDeptController 对应的应用编排，保留原业务执行顺序与响应语义。
 */
@Component
public class SysDeptBiz {

    private final ISysDeptFacade deptFacade;

    /**
     * 接收应用编排所需的领域 Facade 和基础设施，不在构造阶段访问外部系统。
     */
    public SysDeptBiz(ISysDeptFacade deptFacade) {
        this.deptFacade = deptFacade;
    }

    /**
     * 获取部门列表：按原顺序执行 selectDeptList、业务动作等步骤，并保留原判断、返回及异常语义。
     *
     * @param dept dept 请求参数或领域数据
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public List<SysDeptBO> list(SysDeptBO dept) {
        List<SysDeptBO> depts = deptFacade.queryDeptList(dept);
        return depts;
    }

    /**
     * 查询部门列表（排除节点）：按原顺序执行 selectDeptList、removeIf、getDeptId、intValue、contains、split、getAncestors、业务动作等步骤，并保留原判断、返回及异常语义。
     *
     * @param deptId deptId 请求参数或领域数据
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public List<SysDeptBO> excludeChild(Long deptId) {
        List<SysDeptBO> depts = deptFacade.queryDeptList(new SysDeptBO());
        depts.removeIf(d -> d.getDeptId().intValue() == deptId || ArrayUtils.contains(StringUtils.split(d.getAncestors(), ","), deptId + ""));
        return depts;
    }

    /**
     * 根据部门编号获取详细信息：按原顺序执行 checkDeptDataScope、selectDeptById 等步骤，并保留原判断、返回及异常语义。
     *
     * @param deptId deptId 请求参数或领域数据
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public SysDeptBO getInfo(Long deptId) {
        deptFacade.checkDeptDataScope(deptId);
        return deptFacade.queryDeptById(deptId);
    }

    /**
     * 新增部门：按原顺序执行 checkDeptNameUnique、getDeptName、setCreateBy、getUsername、insertDept 等步骤，并保留原判断、返回及异常语义。
     *
     * @param dept dept 请求参数或领域数据
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public int add(SysDeptBO dept) {
        if (!deptFacade.checkDeptNameUnique(dept)) {
            throw new ServiceException("新增部门'" + dept.getDeptName() + "'失败，部门名称已存在");
        }
        dept.setCreateBy(SecurityUtils.getUsername());
        return deptFacade.createDept(dept);
    }

    /**
     * 修改部门：按原顺序执行 getDeptId、checkDeptDataScope、checkDeptNameUnique、getDeptName、getParentId、equals、getStatus 等步骤，并保留原判断、返回及异常语义。
     *
     * @param dept dept 请求参数或领域数据
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public int edit(SysDeptBO dept) {
        Long deptId = dept.getDeptId();
        deptFacade.checkDeptDataScope(deptId);
        if (!deptFacade.checkDeptNameUnique(dept)) {
            throw new ServiceException("修改部门'" + dept.getDeptName() + "'失败，部门名称已存在");
        } else if (dept.getParentId().equals(deptId)) {
            throw new ServiceException("修改部门'" + dept.getDeptName() + "'失败，上级部门不能是自己");
        } else if (StringUtils.equals(UserConstants.DEPT_DISABLE, dept.getStatus()) && deptFacade.queryNormalChildrenDeptById(deptId) > 0) {
            throw new ServiceException("该部门包含未停用的子部门！");
        }
        dept.setUpdateBy(SecurityUtils.getUsername());
        return deptFacade.modifyDept(dept);
    }

    /**
     * 保存部门排序：按原顺序执行 get、split、updateDeptSort、业务动作等步骤，并保留原判断、返回及异常语义。
     *
     * @param params params 请求参数或领域数据
     */
    public void updateSort(Map<String, String> params) {
        String[] deptIds = params.get("deptIds").split(",");
        String[] orderNums = params.get("orderNums").split(",");
        deptFacade.modifyDeptSort(deptIds, orderNums);
        return;
    }

    /**
     * 删除部门：按原顺序执行 hasChildByDeptId、checkDeptExistUser、checkDeptDataScope、deleteDeptById 等步骤，并保留原判断、返回及异常语义。
     *
     * @param deptId deptId 请求参数或领域数据
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public int remove(Long deptId) {
        if (deptFacade.hasChildByDeptId(deptId)) {
            throw new ServiceException("存在下级部门,不允许删除", HttpStatus.WARN);
        }
        if (deptFacade.checkDeptExistUser(deptId)) {
            throw new ServiceException("部门存在用户,不允许删除", HttpStatus.WARN);
        }
        deptFacade.checkDeptDataScope(deptId);
        return deptFacade.removeDeptById(deptId);
    }
}
