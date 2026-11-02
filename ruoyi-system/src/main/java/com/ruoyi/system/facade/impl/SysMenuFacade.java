package com.ruoyi.system.facade.impl;

import java.util.List;

import com.ruoyi.common.core.domain.TreeSelect;
import com.ruoyi.system.model.bo.SysMenuBO;
import com.ruoyi.system.model.bo.RouterBO;
import com.ruoyi.system.convert.BoundaryConvert;
import com.ruoyi.common.utils.BoundaryPages;
import com.ruoyi.system.model.bo.*;
import org.springframework.stereotype.Component;
import com.ruoyi.system.service.ISysMenuService;
import com.ruoyi.system.facade.ISysMenuFacade;

/**
 * 本领域的边界转换和 Service 编排，不声明事务。
 */
@Component
public class SysMenuFacade implements ISysMenuFacade {
    private final ISysMenuService service;

    /**
     * 接收本领域 Service，建立唯一持久化调用路径，不执行查询或写入。
     *
     * @param service 本领域业务服务
     */
    public SysMenuFacade(ISysMenuService service) {
        this.service = service;
    }

    /**
     * selectMenuList：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param userId userId 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public List<SysMenuBO> queryMenuList(Long userId) {
        return BoundaryPages.map(service.selectMenuList(userId), BoundaryConvert.INSTANCE::toSysMenuBOList);
    }

    /**
     * selectMenuList：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param menu   menu 领域参数
     * @param userId userId 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public List<SysMenuBO> queryMenuList(SysMenuBO menu, Long userId) {
        return BoundaryPages.map(service.selectMenuList(BoundaryConvert.INSTANCE.toSysMenu(menu), userId), BoundaryConvert.INSTANCE::toSysMenuBOList);
    }

    /**
     * selectMenuTreeByUserId：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param userId userId 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public List<SysMenuBO> queryMenuTreeByUserId(Long userId) {
        return BoundaryPages.map(service.selectMenuTreeByUserId(userId), BoundaryConvert.INSTANCE::toSysMenuBOList);
    }

    /**
     * selectMenuListByRoleId：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param roleId roleId 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public List<Long> queryMenuListByRoleId(Long roleId) {
        return service.selectMenuListByRoleId(roleId);
    }

    /**
     * buildMenus：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param menus menus 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public List<RouterBO> buildMenus(List<SysMenuBO> menus) {
        return BoundaryPages.map(service.buildMenus(BoundaryConvert.INSTANCE.toSysMenuList(menus)), BoundaryConvert.INSTANCE::toRouterBOList);
    }

    /**
     * buildMenuTreeSelect：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param menus menus 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public List<TreeSelect> buildMenuTreeSelect(List<SysMenuBO> menus) {
        return service.buildMenuTreeSelect(BoundaryConvert.INSTANCE.toSysMenuList(menus));
    }

    /**
     * selectMenuById：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param menuId menuId 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public SysMenuBO queryMenuById(Long menuId) {
        return BoundaryConvert.INSTANCE.toSysMenuBO(service.selectMenuById(menuId));
    }

    /**
     * hasChildByMenuId：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param menuId menuId 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public boolean hasChildByMenuId(Long menuId) {
        return service.hasChildByMenuId(menuId);
    }

    /**
     * checkMenuExistRole：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param menuId menuId 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public boolean checkMenuExistRole(Long menuId) {
        return service.checkMenuExistRole(menuId);
    }

    /**
     * insertMenu：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param menu menu 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public int createMenu(SysMenuBO menu) {
        return service.insertMenu(BoundaryConvert.INSTANCE.toSysMenu(menu));
    }

    /**
     * updateMenu：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param menu menu 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public int modifyMenu(SysMenuBO menu) {
        return service.updateMenu(BoundaryConvert.INSTANCE.toSysMenu(menu));
    }

    /**
     * updateMenuSort：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param menuIds   menuIds 领域参数
     * @param orderNums orderNums 领域参数
     */
    @Override
    public void modifyMenuSort(String[] menuIds, String[] orderNums) {
        service.updateMenuSort(menuIds, orderNums);
    }

    /**
     * deleteMenuById：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param menuId menuId 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public int removeMenuById(Long menuId) {
        return service.deleteMenuById(menuId);
    }

    /**
     * checkMenuNameUnique：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param menu menu 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public boolean checkMenuNameUnique(SysMenuBO menu) {
        return service.checkMenuNameUnique(BoundaryConvert.INSTANCE.toSysMenu(menu));
    }

    /**
     * checkRouteConfigUnique：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param menu menu 领域参数
     * @return 原领域能力的处理结果
     */
    @Override
    public boolean checkRouteConfigUnique(SysMenuBO menu) {
        return service.checkRouteConfigUnique(BoundaryConvert.INSTANCE.toSysMenu(menu));
    }
}
