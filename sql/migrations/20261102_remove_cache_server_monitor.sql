-- 清理已有数据库中的服务监控、缓存监控、缓存列表菜单及角色关联；本文件不会自动执行。
-- 按组件和功能权限定位，不使用固定菜单 ID。
START TRANSACTION;
DELETE
FROM sys_role_menu
WHERE menu_id IN (SELECT menu_id
                  FROM sys_menu
                  WHERE component IN ('monitor/server/index', 'monitor/cache/index', 'monitor/cache/list')
                     OR perms LIKE 'monitor:server:%'
                     OR perms LIKE 'monitor:cache:%');
DELETE
FROM sys_menu
WHERE component IN ('monitor/server/index', 'monitor/cache/index', 'monitor/cache/list')
   OR perms LIKE 'monitor:server:%'
   OR perms LIKE 'monitor:cache:%';
COMMIT;
