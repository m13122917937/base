package com.ruoyi.system.facade;

import java.util.List;

import com.ruoyi.common.core.domain.TreeSelect;
import com.ruoyi.system.model.bo.SysMenuBO;
import com.ruoyi.system.model.bo.RouterBO;
import com.ruoyi.system.model.bo.*;

/**
 * 稳定领域能力入口，隐藏 Service、Entity 和 Mapper。
 */
public interface ISysMenuFacade {
    /**
     * selectMenuList：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param userId userId 领域参数
     * @return 原领域能力的处理结果
     */
    List<SysMenuBO> queryMenuList(Long userId);

    /**
     * selectMenuList：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param menu   menu 领域参数
     * @param userId userId 领域参数
     * @return 原领域能力的处理结果
     */
    List<SysMenuBO> queryMenuList(SysMenuBO menu, Long userId);

    /**
     * selectMenuTreeByUserId：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param userId userId 领域参数
     * @return 原领域能力的处理结果
     */
    List<SysMenuBO> queryMenuTreeByUserId(Long userId);

    /**
     * selectMenuListByRoleId：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param roleId roleId 领域参数
     * @return 原领域能力的处理结果
     */
    List<Long> queryMenuListByRoleId(Long roleId);

    /**
     * buildMenus：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param menus menus 领域参数
     * @return 原领域能力的处理结果
     */
    List<RouterBO> buildMenus(List<SysMenuBO> menus);

    /**
     * buildMenuTreeSelect：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param menus menus 领域参数
     * @return 原领域能力的处理结果
     */
    List<TreeSelect> buildMenuTreeSelect(List<SysMenuBO> menus);

    /**
     * selectMenuById：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param menuId menuId 领域参数
     * @return 原领域能力的处理结果
     */
    SysMenuBO queryMenuById(Long menuId);

    /**
     * hasChildByMenuId：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param menuId menuId 领域参数
     * @return 原领域能力的处理结果
     */
    boolean hasChildByMenuId(Long menuId);

    /**
     * checkMenuExistRole：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param menuId menuId 领域参数
     * @return 原领域能力的处理结果
     */
    boolean checkMenuExistRole(Long menuId);

    /**
     * insertMenu：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param menu menu 领域参数
     * @return 原领域能力的处理结果
     */
    int createMenu(SysMenuBO menu);

    /**
     * updateMenu：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param menu menu 领域参数
     * @return 原领域能力的处理结果
     */
    int modifyMenu(SysMenuBO menu);

    /**
     * updateMenuSort：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param menuIds   menuIds 领域参数
     * @param orderNums orderNums 领域参数
     */
    void modifyMenuSort(String[] menuIds, String[] orderNums);

    /**
     * deleteMenuById：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param menuId menuId 领域参数
     * @return 原领域能力的处理结果
     */
    int removeMenuById(Long menuId);

    /**
     * checkMenuNameUnique：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param menu menu 领域参数
     * @return 原领域能力的处理结果
     */
    boolean checkMenuNameUnique(SysMenuBO menu);

    /**
     * checkRouteConfigUnique：转换领域入参后调用本领域 Service，转换返回值并保留分页、校验及异常语义。
     *
     * @param menu menu 领域参数
     * @return 原领域能力的处理结果
     */
    boolean checkRouteConfigUnique(SysMenuBO menu);
}
