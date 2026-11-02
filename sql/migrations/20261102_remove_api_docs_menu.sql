-- 可选：清理已有数据库中的旧 API 文档菜单；新建库的初始化脚本已移除该入口。
-- 仅针对已删除的页面组件，保留其他系统工具菜单。
START TRANSACTION;
DELETE
FROM sys_role_menu
WHERE menu_id IN (SELECT menu_id FROM sys_menu WHERE component = 'tool/swagger/index');
DELETE
FROM sys_menu
WHERE component = 'tool/swagger/index';
COMMIT;
