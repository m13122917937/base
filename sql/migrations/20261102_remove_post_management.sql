-- 移除已有数据库中的岗位菜单、按钮及角色菜单关联；本文件不会自动执行。
-- 按组件、权限及直属子项定位，兼容自定义菜单 ID，可重复执行。
START TRANSACTION;
DELETE rm
FROM sys_role_menu rm
JOIN sys_menu m ON m.menu_id = rm.menu_id
WHERE m.component = 'system/post/index'
   OR m.perms LIKE 'system:post:%'
   OR m.parent_id IN (
       SELECT menu_id FROM (
           SELECT DISTINCT menu_id FROM sys_menu WHERE component = 'system/post/index'
       ) post_menus
   );
DELETE FROM sys_menu
WHERE component = 'system/post/index'
   OR perms LIKE 'system:post:%'
   OR parent_id IN (
       SELECT menu_id FROM (
           SELECT DISTINCT menu_id FROM sys_menu WHERE component = 'system/post/index'
       ) post_menus
   );
COMMIT;

-- MySQL DDL 会隐式提交；仅在应用已更新为不依赖岗位表的版本后执行。
DROP TABLE IF EXISTS sys_user_post;
DROP TABLE IF EXISTS sys_post;
