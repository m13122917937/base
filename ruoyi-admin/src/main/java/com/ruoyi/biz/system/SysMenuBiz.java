package com.ruoyi.biz.system;

import com.ruoyi.common.core.domain.TreeSelect;

import java.util.LinkedHashMap;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;

import java.util.List;
import java.util.Map;

import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.system.model.bo.SysMenuBO;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.facade.ISysMenuFacade;
import org.springframework.stereotype.Component;
import com.ruoyi.system.model.bo.*;
import com.ruoyi.framework.model.bo.*;

/**
 * SysMenuController 对应的应用编排，保留原业务执行顺序与响应语义。
 */
@Component
public class SysMenuBiz {

    private final ISysMenuFacade menuFacade;

    /**
     * 接收应用编排所需的领域 Facade 和基础设施，不在构造阶段访问外部系统。
     */
    public SysMenuBiz(ISysMenuFacade menuFacade) {
        this.menuFacade = menuFacade;
    }

    /**
     * 获取菜单列表：按原顺序执行 selectMenuList、getUserId、业务动作等步骤，并保留原判断、返回及异常语义。
     *
     * @param menu menu 请求参数或领域数据
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public List<SysMenuBO> list(SysMenuBO menu) {
        List<SysMenuBO> menus = menuFacade.queryMenuList(menu, SecurityUtils.getUserId());
        return menus;
    }

    /**
     * 根据菜单编号获取详细信息：按原顺序执行 selectMenuById 等步骤，并保留原判断、返回及异常语义。
     *
     * @param menuId menuId 请求参数或领域数据
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public SysMenuBO getInfo(Long menuId) {
        return menuFacade.queryMenuById(menuId);
    }

    /**
     * 获取菜单下拉树列表：按原顺序执行 selectMenuList、getUserId、buildMenuTreeSelect 等步骤，并保留原判断、返回及异常语义。
     *
     * @param menu menu 请求参数或领域数据
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public List<TreeSelect> treeselect(SysMenuBO menu) {
        List<SysMenuBO> menus = menuFacade.queryMenuList(menu, SecurityUtils.getUserId());
        return menuFacade.buildMenuTreeSelect(menus);
    }

    /**
     * 加载对应角色菜单列表树：按原顺序执行 selectMenuList、getUserId、put、selectMenuListByRoleId、buildMenuTreeSelect 等步骤，并保留原判断、返回及异常语义。
     *
     * @param roleId roleId 请求参数或领域数据
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public Map<String, Object> roleMenuTreeselect(Long roleId) {
        List<SysMenuBO> menus = menuFacade.queryMenuList(SecurityUtils.getUserId());
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("checkedKeys", menuFacade.queryMenuListByRoleId(roleId));
        result.put("menus", menuFacade.buildMenuTreeSelect(menus));
        return result;
    }

    /**
     * 新增菜单：按原顺序执行 checkMenuNameUnique、getMenuName、equals、getIsFrame、ishttp、getPath、checkRouteConfigUnique 等步骤，并保留原判断、返回及异常语义。
     *
     * @param menu menu 请求参数或领域数据
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public int add(SysMenuBO menu) {
        if (!menuFacade.checkMenuNameUnique(menu)) {
            throw new ServiceException("新增菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        } else if (UserConstants.YES_FRAME.equals(menu.getIsFrame()) && !StringUtils.ishttp(menu.getPath())) {
            throw new ServiceException("新增菜单'" + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
        } else if (!menuFacade.checkRouteConfigUnique(menu)) {
            throw new ServiceException("新增菜单'" + menu.getMenuName() + "'失败，路由名称或地址已存在");
        }
        menu.setCreateBy(SecurityUtils.getUsername());
        return menuFacade.createMenu(menu);
    }

    /**
     * 修改菜单：按原顺序执行 checkMenuNameUnique、getMenuName、equals、getIsFrame、ishttp、getPath、getMenuId 等步骤，并保留原判断、返回及异常语义。
     *
     * @param menu menu 请求参数或领域数据
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public int edit(SysMenuBO menu) {
        if (!menuFacade.checkMenuNameUnique(menu)) {
            throw new ServiceException("修改菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        } else if (UserConstants.YES_FRAME.equals(menu.getIsFrame()) && !StringUtils.ishttp(menu.getPath())) {
            throw new ServiceException("修改菜单'" + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
        } else if (menu.getMenuId().equals(menu.getParentId())) {
            throw new ServiceException("修改菜单'" + menu.getMenuName() + "'失败，上级菜单不能选择自己");
        } else if (!menuFacade.checkRouteConfigUnique(menu)) {
            throw new ServiceException("修改菜单'" + menu.getMenuName() + "'失败，路由名称或地址已存在");
        }
        menu.setUpdateBy(SecurityUtils.getUsername());
        return menuFacade.modifyMenu(menu);
    }

    /**
     * 保存菜单排序：按原顺序执行 get、split、updateMenuSort、业务动作等步骤，并保留原判断、返回及异常语义。
     *
     * @param params params 请求参数或领域数据
     */
    public void updateSort(Map<String, String> params) {
        String[] menuIds = params.get("menuIds").split(",");
        String[] orderNums = params.get("orderNums").split(",");
        menuFacade.modifyMenuSort(menuIds, orderNums);
        return;
    }

    /**
     * 删除菜单：按原顺序执行 hasChildByMenuId、checkMenuExistRole、deleteMenuById 等步骤，并保留原判断、返回及异常语义。
     *
     * @param menuId menuId 请求参数或领域数据
     * @return 业务查询数据或执行结果；校验失败抛出业务异常，由 Web 层处理
     */
    public int remove(Long menuId) {
        if (menuFacade.hasChildByMenuId(menuId)) {
            throw new ServiceException("存在子菜单,不允许删除", HttpStatus.WARN);
        }
        if (menuFacade.checkMenuExistRole(menuId)) {
            throw new ServiceException("菜单已分配,不允许删除", HttpStatus.WARN);
        }
        return menuFacade.removeMenuById(menuId);
    }
}
