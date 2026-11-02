-- 清理已有数据库中的旧验证码开关；新建库的初始化脚本已移除该配置。
-- 本文件不会自动执行。执行后可在参数设置中刷新缓存，移除旧的参数缓存。
START TRANSACTION;
DELETE FROM sys_config WHERE config_key = 'sys.account.captchaEnabled';
COMMIT;
