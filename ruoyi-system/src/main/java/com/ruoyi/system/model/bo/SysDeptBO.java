package com.ruoyi.system.model.bo;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 部门表 sys_dept
 *
 * @author ruoyi
 */
public class SysDeptBO extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 父部门ID
     */
    private Long parentId;

    /**
     * 祖级列表
     */
    private String ancestors;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 显示顺序
     */
    private Integer orderNum;

    /**
     * 负责人
     */
    private String leader;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 部门状态:0正常,1停用
     */
    private String status;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;

    /**
     * 父部门名称
     */
    private String parentName;

    /**
     * 子部门
     */
    private List<SysDeptBO> children = new ArrayList<SysDeptBO>();

    /**
     * 读取并返回当前对象的 DeptId 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public Long getDeptId() {
        return deptId;
    }

    /**
     * 将参数 deptId 写入当前对象的 DeptId 属性，不执行持久化。
     *
     * @param deptId deptId 请求参数或领域数据
     */
    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    /**
     * 读取并返回当前对象的 ParentId 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public Long getParentId() {
        return parentId;
    }

    /**
     * 将参数 parentId 写入当前对象的 ParentId 属性，不执行持久化。
     *
     * @param parentId parentId 请求参数或领域数据
     */
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    /**
     * 读取并返回当前对象的 Ancestors 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public String getAncestors() {
        return ancestors;
    }

    /**
     * 将参数 ancestors 写入当前对象的 Ancestors 属性，不执行持久化。
     *
     * @param ancestors ancestors 请求参数或领域数据
     */
    public void setAncestors(String ancestors) {
        this.ancestors = ancestors;
    }

    /**
     * 读取并返回当前对象的 DeptName 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @NotBlank(message = "部门名称不能为空")
    @Size(min = 0, max = 30, message = "部门名称长度不能超过30个字符")
    public String getDeptName() {
        return deptName;
    }

    /**
     * 将参数 deptName 写入当前对象的 DeptName 属性，不执行持久化。
     *
     * @param deptName deptName 请求参数或领域数据
     */
    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    /**
     * 读取并返回当前对象的 OrderNum 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @NotNull(message = "显示顺序不能为空")
    public Integer getOrderNum() {
        return orderNum;
    }

    /**
     * 将参数 orderNum 写入当前对象的 OrderNum 属性，不执行持久化。
     *
     * @param orderNum orderNum 请求参数或领域数据
     */
    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    /**
     * 读取并返回当前对象的 Leader 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public String getLeader() {
        return leader;
    }

    /**
     * 将参数 leader 写入当前对象的 Leader 属性，不执行持久化。
     *
     * @param leader leader 请求参数或领域数据
     */
    public void setLeader(String leader) {
        this.leader = leader;
    }

    /**
     * 读取并返回当前对象的 Phone 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @Size(min = 0, max = 11, message = "联系电话长度不能超过11个字符")
    public String getPhone() {
        return phone;
    }

    /**
     * 将参数 phone 写入当前对象的 Phone 属性，不执行持久化。
     *
     * @param phone phone 请求参数或领域数据
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * 读取并返回当前对象的 Email 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    @Email(message = "邮箱格式不正确")
    @Size(min = 0, max = 50, message = "邮箱长度不能超过50个字符")
    public String getEmail() {
        return email;
    }

    /**
     * 将参数 email 写入当前对象的 Email 属性，不执行持久化。
     *
     * @param email email 请求参数或领域数据
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 读取并返回当前对象的 Status 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public String getStatus() {
        return status;
    }

    /**
     * 将参数 status 写入当前对象的 Status 属性，不执行持久化。
     *
     * @param status status 请求参数或领域数据
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 读取并返回当前对象的 DelFlag 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public String getDelFlag() {
        return delFlag;
    }

    /**
     * 将参数 delFlag 写入当前对象的 DelFlag 属性，不执行持久化。
     *
     * @param delFlag delFlag 请求参数或领域数据
     */
    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    /**
     * 读取并返回当前对象的 ParentName 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public String getParentName() {
        return parentName;
    }

    /**
     * 将参数 parentName 写入当前对象的 ParentName 属性，不执行持久化。
     *
     * @param parentName parentName 请求参数或领域数据
     */
    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    /**
     * 读取并返回当前对象的 Children 属性，供上层查询或边界转换使用。
     *
     * @return 原接口约定的处理结果；未找到或校验失败沿用原语义
     */
    public List<SysDeptBO> getChildren() {
        return children;
    }

    /**
     * 将参数 children 写入当前对象的 Children 属性，不执行持久化。
     *
     * @param children children 请求参数或领域数据
     */
    public void setChildren(List<SysDeptBO> children) {
        this.children = children;
    }

}
