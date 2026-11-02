-- 可选：清理已有数据库中的 Druid 监控菜单及角色关联；本文件不会自动执行。
-- 按页面组件定位，保留其他系统监控菜单和 Druid 数据源。
START TRANSACTION;
DELETE
FROM sys_role_menu
WHERE menu_id IN (SELECT menu_id FROM sys_menu WHERE component = 'monitor/druid/index');
DELETE
FROM sys_menu
WHERE component = 'monitor/druid/index';
COMMIT;
