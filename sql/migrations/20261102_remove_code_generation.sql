-- 可选：清理已有数据库的代码生成、表单构建菜单及生成器元数据。
-- 仅删除工具配置，不删除由生成器创建的业务表；本文件不会自动执行。
START TRANSACTION;
DELETE rm
FROM sys_role_menu rm
JOIN sys_menu m ON m.menu_id = rm.menu_id
WHERE m.component IN ('tool/gen/index', 'tool/build/index')
   OR m.perms LIKE 'tool:gen:%'
   OR m.parent_id IN (
       SELECT menu_id FROM (
           SELECT DISTINCT menu_id FROM sys_menu WHERE component IN ('tool/gen/index', 'tool/build/index')
       ) retired_tools
   );
DELETE FROM sys_menu
WHERE component IN ('tool/gen/index', 'tool/build/index')
   OR perms LIKE 'tool:gen:%'
   OR parent_id IN (
       SELECT menu_id FROM (
           SELECT DISTINCT menu_id FROM sys_menu WHERE component IN ('tool/gen/index', 'tool/build/index')
       ) retired_tools
   );
-- 系统工具目录已无其他子项时才清理，保留自定义工具。
DELETE rm
FROM sys_role_menu rm
JOIN sys_menu m ON m.menu_id = rm.menu_id
LEFT JOIN sys_menu child ON child.parent_id = m.menu_id
WHERE m.path = 'tool' AND m.menu_type = 'M' AND m.parent_id = 0 AND child.menu_id IS NULL;
DELETE FROM sys_menu
WHERE path = 'tool' AND menu_type = 'M' AND parent_id = 0
  AND menu_id NOT IN (SELECT parent_id FROM (SELECT DISTINCT parent_id FROM sys_menu) remaining_menus);
COMMIT;

-- MySQL DDL 独立提交；移除的两张表仅保存生成器配置。
DROP TABLE IF EXISTS gen_table_column;
DROP TABLE IF EXISTS gen_table;
