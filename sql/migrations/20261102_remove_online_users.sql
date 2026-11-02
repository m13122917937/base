-- 清理已有数据库中的在线用户菜单、按钮及角色关联；本文件不会自动执行。
-- 按页面组件、权限和直属子项定位，不使用固定菜单 ID。
START TRANSACTION;
DELETE rm
FROM sys_role_menu rm
JOIN sys_menu m ON m.menu_id = rm.menu_id
WHERE m.component = 'monitor/online/index'
   OR m.perms LIKE 'monitor:online:%'
   OR m.parent_id IN (
       SELECT menu_id FROM (
           SELECT DISTINCT menu_id FROM sys_menu WHERE component = 'monitor/online/index'
       ) online_menus
   );
DELETE FROM sys_menu
WHERE component = 'monitor/online/index'
   OR perms LIKE 'monitor:online:%'
   OR parent_id IN (
       SELECT menu_id FROM (
           SELECT DISTINCT menu_id FROM sys_menu WHERE component = 'monitor/online/index'
       ) online_menus
   );
COMMIT;
