package com.ruoyi.architecture;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import javax.sql.DataSource;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.spring.service.IService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.autoconfigure.PageHelperAutoConfiguration;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.domain.entity.SysMenu;
import com.ruoyi.common.core.domain.entity.SysDictType;
import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.framework.config.ApplicationConfig;
import com.ruoyi.quartz.domain.SysJobLog;
import com.ruoyi.quartz.mapper.SysJobLogMapper;
import com.ruoyi.quartz.service.impl.SysJobLogServiceImpl;
import com.ruoyi.system.domain.SysNotice;
import com.ruoyi.system.domain.SysLogininfor;
import com.ruoyi.system.domain.SysOperLog;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.mapper.SysNoticeMapper;
import com.ruoyi.system.mapper.SysLogininforMapper;
import com.ruoyi.system.mapper.SysOperLogMapper;
import com.ruoyi.system.mapper.SysNoticeReadMapper;
import com.ruoyi.system.mapper.SysConfigMapper;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.system.mapper.SysRoleMapper;
import com.ruoyi.system.mapper.SysDeptMapper;
import com.ruoyi.system.mapper.SysMenuMapper;
import com.ruoyi.system.mapper.SysDictTypeMapper;
import com.ruoyi.system.mapper.SysUserRoleMapper;
import com.ruoyi.system.service.impl.SysNoticeServiceImpl;
import com.ruoyi.system.service.impl.SysLogininforServiceImpl;
import com.ruoyi.system.service.impl.SysOperLogServiceImpl;
import com.ruoyi.system.service.impl.SysDeptServiceImpl;
import com.ruoyi.system.service.impl.SysConfigServiceImpl;
import com.ruoyi.system.service.impl.SysUserServiceImpl;
import com.ruoyi.system.service.impl.SysRoleServiceImpl;
import com.ruoyi.system.service.impl.SysMenuServiceImpl;
import com.ruoyi.system.service.impl.SysDictTypeServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.assertj.AssertableApplicationContext;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;

/** 使用当前系统表结构和真实 Plus SQL，验收通用 Service 的读写、映射和动态查询行为。 */
class MybatisPlusPersistenceTest {
    /** 角色唯一性查询执行真实 SQL，覆盖新增冲突、编辑排除自身、多条重复及忽略已删除角色。 */
    @Test
    void checksRoleUniquenessWithoutDuplicateLimits() {
        withDatabase(context -> {
            JdbcTemplate jdbc = new JdbcTemplate(context.getBean(DataSource.class));
            SysRoleServiceImpl service = new SysRoleServiceImpl();
            ReflectionTestUtils.setField(service, "baseMapper", context.getBean(SysRoleMapper.class));
            SysRole role = new SysRole();
            role.setRoleName("fixture-role");
            role.setRoleKey("fixture-key");
            assertThat(service.checkRoleNameUnique(role)).isTrue();
            assertThat(service.checkRoleKeyUnique(role)).isTrue();
            jdbc.update("insert into sys_role (role_id, role_name, role_key, role_sort, status) values (20, 'fixture-role', 'fixture-key', 1, '0')");
            assertThat(service.checkRoleNameUnique(role)).isFalse();
            assertThat(service.checkRoleKeyUnique(role)).isFalse();
            role.setRoleId(20L);
            assertThat(service.checkRoleNameUnique(role)).isTrue();
            assertThat(service.checkRoleKeyUnique(role)).isTrue();
            jdbc.update("insert into sys_role (role_id, role_name, role_key, role_sort, status) values (21, 'fixture-role', 'fixture-key', 2, '0')");
            assertThat(service.checkRoleNameUnique(role)).isFalse();
            assertThat(service.checkRoleKeyUnique(role)).isFalse();
            role.setRoleId(null);
            assertThat(service.checkRoleNameUnique(role)).isFalse();
            assertThat(service.checkRoleKeyUnique(role)).isFalse();
            jdbc.update("update sys_role set del_flag = '2'");
            assertThat(service.checkRoleNameUnique(role)).isTrue();
            assertThat(service.checkRoleKeyUnique(role)).isTrue();
            assertThat(PageHelper.getLocalPage()).isNull();
        });
    }

    /** 用户账号、手机号及邮箱校验执行真实 SQL，允许编辑自身并忽略逻辑删除，重复数据返回不唯一。 */
    @Test
    void checksUserUniquenessWithoutDuplicateLimits() {
        withDatabase(context -> {
            JdbcTemplate jdbc = new JdbcTemplate(context.getBean(DataSource.class));
            SysUserServiceImpl service = new SysUserServiceImpl();
            ReflectionTestUtils.setField(service, "baseMapper", context.getBean(SysUserMapper.class));
            SysUser user = new SysUser();
            user.setUserName("fixture-user");
            user.setPhonenumber("13800000000");
            user.setEmail("fixture@example.test");
            assertThat(service.checkUserNameUnique(user)).isTrue();
            assertThat(service.checkPhoneUnique(user)).isTrue();
            assertThat(service.checkEmailUnique(user)).isTrue();
            jdbc.update("insert into sys_user (user_id, user_name, nick_name, phonenumber, email) values (20, 'fixture-user', 'fixture', '13800000000', 'fixture@example.test')");
            assertThat(service.checkUserNameUnique(user)).isFalse();
            assertThat(service.checkPhoneUnique(user)).isFalse();
            assertThat(service.checkEmailUnique(user)).isFalse();
            user.setUserId(20L);
            assertThat(service.checkUserNameUnique(user)).isTrue();
            assertThat(service.checkPhoneUnique(user)).isTrue();
            assertThat(service.checkEmailUnique(user)).isTrue();
            jdbc.update("insert into sys_user (user_id, user_name, nick_name, phonenumber, email) values (21, 'fixture-user', 'fixture', '13800000000', 'fixture@example.test')");
            assertThat(service.checkUserNameUnique(user)).isFalse();
            assertThat(service.checkPhoneUnique(user)).isFalse();
            assertThat(service.checkEmailUnique(user)).isFalse();
            jdbc.update("update sys_user set del_flag = '2'");
            assertThat(service.checkUserNameUnique(user)).isTrue();
            assertThat(service.checkPhoneUnique(user)).isTrue();
            assertThat(service.checkEmailUnique(user)).isTrue();
        });
    }

    /** 菜单和部门同名校验保留父级范围；编辑排除自身，部门同时保留逻辑删除过滤。 */
    @Test
    void scopesMenuAndDepartmentUniquenessToParent() {
        withDatabase(context -> {
            JdbcTemplate jdbc = new JdbcTemplate(context.getBean(DataSource.class));
            SysMenuServiceImpl menus = new SysMenuServiceImpl();
            ReflectionTestUtils.setField(menus, "baseMapper", context.getBean(SysMenuMapper.class));
            SysDeptServiceImpl depts = context.getBean(SysDeptServiceImpl.class);
            SysMenu menu = new SysMenu();
            menu.setMenuName("fixture-menu");
            menu.setParentId(10L);
            SysDept dept = new SysDept();
            dept.setDeptName("fixture-dept");
            dept.setParentId(10L);
            jdbc.update("insert into sys_menu (menu_id, menu_name, parent_id) values (20, 'fixture-menu', 11)");
            jdbc.update("insert into sys_dept (dept_id, dept_name, parent_id) values (20, 'fixture-dept', 11)");
            assertThat(menus.checkMenuNameUnique(menu)).isTrue();
            assertThat(depts.checkDeptNameUnique(dept)).isTrue();
            menu.setParentId(11L);
            dept.setParentId(11L);
            assertThat(menus.checkMenuNameUnique(menu)).isFalse();
            assertThat(depts.checkDeptNameUnique(dept)).isFalse();
            menu.setMenuId(20L);
            dept.setDeptId(20L);
            assertThat(menus.checkMenuNameUnique(menu)).isTrue();
            assertThat(depts.checkDeptNameUnique(dept)).isTrue();
            jdbc.update("insert into sys_menu (menu_id, menu_name, parent_id) values (21, 'fixture-menu', 11)");
            jdbc.update("insert into sys_dept (dept_id, dept_name, parent_id) values (21, 'fixture-dept', 11)");
            assertThat(menus.checkMenuNameUnique(menu)).isFalse();
            assertThat(depts.checkDeptNameUnique(dept)).isFalse();
            jdbc.update("update sys_dept set del_flag = '2' where dept_id = 21");
            assertThat(depts.checkDeptNameUnique(dept)).isTrue();
        });
    }

    /** 配置键及字典类型校验直接访问内存库，覆盖新增冲突、编辑自身、配置重复及字典编辑冲突，不调用缓存。 */
    @Test
    void checksConfigAndDictionaryUniquenessWithoutDuplicateLimits() {
        withDatabase(context -> {
            JdbcTemplate jdbc = new JdbcTemplate(context.getBean(DataSource.class));
            SysConfigServiceImpl configs = new SysConfigServiceImpl();
            ReflectionTestUtils.setField(configs, "baseMapper", context.getBean(SysConfigMapper.class));
            SysDictTypeServiceImpl dicts = new SysDictTypeServiceImpl();
            ReflectionTestUtils.setField(dicts, "baseMapper", context.getBean(SysDictTypeMapper.class));
            SysConfig config = new SysConfig();
            config.setConfigKey("fixture-key");
            SysDictType dict = new SysDictType();
            dict.setDictType("fixture-type");
            assertThat(configs.checkConfigKeyUnique(config)).isTrue();
            assertThat(dicts.checkDictTypeUnique(dict)).isTrue();
            jdbc.update("insert into sys_config (config_id, config_key) values (20, 'fixture-key')");
            jdbc.update("insert into sys_dict_type (dict_id, dict_type) values (20, 'fixture-type')");
            assertThat(configs.checkConfigKeyUnique(config)).isFalse();
            assertThat(dicts.checkDictTypeUnique(dict)).isFalse();
            config.setConfigId(20L);
            dict.setDictId(20L);
            assertThat(configs.checkConfigKeyUnique(config)).isTrue();
            assertThat(dicts.checkDictTypeUnique(dict)).isTrue();
            jdbc.update("insert into sys_config (config_id, config_key) values (21, 'fixture-key')");
            jdbc.update("insert into sys_dict_type (dict_id, dict_type) values (21, 'fixture-other')");
            dict.setDictId(21L);
            assertThat(configs.checkConfigKeyUnique(config)).isFalse();
            assertThat(dicts.checkDictTypeUnique(dict)).isFalse();
        });
    }

    /** 通用 save 不自动设置创建时间；业务新增显式设置时间，参数配置读写保留字段策略与分页。 */
    @Test
    void mapsAllTablesAndExecutesConfigCrudWithDynamicConditions() {
        withDatabase(context -> {
            int mapperCount = 0;
            for (BaseMapper<?> mapper : context.getBeansOfType(BaseMapper.class).values()) {
                assertThat(mapper.selectList(null)).isEmpty();
                mapperCount++;
            }
            assertThat(mapperCount).isEqualTo(16);
            SysConfigServiceImpl service = new SysConfigServiceImpl();
            ReflectionTestUtils.setField(service, "baseMapper", context.getBean(SysConfigMapper.class));
            ReflectionTestUtils.setField(service, "redisCache", org.mockito.Mockito.mock(com.ruoyi.common.core.redis.RedisCache.class));
            assertThat(service).isInstanceOf(IService.class);
            assertThat(context.getBeansOfType(com.baomidou.mybatisplus.core.handlers.MetaObjectHandler.class)).isEmpty();
            SysConfig direct = config("fixture-direct", "direct save");
            assertThat(service.save(direct)).isTrue();
            assertThat(direct.getCreateTime()).isNull();
            assertThat(service.getById(direct.getConfigId()).getCreateTime()).isNull();
            assertThat(service.removeById(direct.getConfigId())).isTrue();
            SysConfig first = config("fixture-a", "first phone");
            first.setCreateBy("fixture-author");
            first.setCreateTime(new Date(1));
            first.setConfigValue("old value");
            assertThat(service.insertConfig(first)).isEqualTo(1);
            assertThat(first.getConfigId()).isPositive();
            SysConfig second = config("fixture-b", "second phone");
            assertThat(service.insertConfig(second)).isEqualTo(1);
            SysConfig stored = service.getById(first.getConfigId());
            assertThat(stored.getCreateTime()).isAfter(new Date(1));
            SysConfig patch = new SysConfig();
            patch.setConfigId(first.getConfigId());
            patch.setConfigName("");
            patch.setConfigValue("updated value");
            patch.setCreateBy("must-not-replace-author");
            assertThat(service.updateConfig(patch)).isEqualTo(1);
            SysConfig updated = service.getById(first.getConfigId());
            assertThat(updated.getConfigName()).isEqualTo("first phone");
            assertThat(updated.getConfigValue()).isEqualTo("updated value");
            assertThat(updated.getCreateBy()).isEqualTo("fixture-author");
            assertThat(updated.getCreateTime()).isEqualTo(stored.getCreateTime());
            assertThat(updated.getUpdateTime()).isNotNull();
            SysConfig query = new SysConfig();
            query.setConfigName("phone");
            query.setConfigType("N");
            query.setCreateBy("context-not-a-filter");
            query.setSearchValue("context-not-a-filter");
            query.getParams().put("unrelated", "ignored");
            PageDomain page = new PageDomain();
            page.setPageNum(2);
            page.setPageSize(1);
            page.setOrderByColumn("configId");
            page.setIsAsc("asc");
            List<SysConfig> result = service.selectConfigList(query, page);
            assertThat(result).extracting(SysConfig::getConfigId).containsExactly(second.getConfigId());
            assertThat(((Page<?>) result).getTotal()).isEqualTo(2);
            assertThat(PageHelper.getLocalPage()).isNull();
            query.setConfigName("' OR 1=1 --");
            assertThat(service.selectConfigList(query)).isEmpty();
            assertThatIllegalArgumentException().isThrownBy(() -> service.deleteConfigByIds(new Long[0]));
            service.deleteConfigByIds(new Long[]{first.getConfigId(), second.getConfigId()});
            assertThat(service.list()).isEmpty();
        });
    }

    /** 用户部分更新只清空允许清空的字段；详情继续加载部门与角色，密码时间和逻辑删除规则保留。 */
    @Test
    void preservesUserAssociationsNullPolicyAndLogicalDeletion() {
        withDatabase(context -> {
            JdbcTemplate jdbc = new JdbcTemplate(context.getBean(DataSource.class));
            jdbc.update("insert into sys_dept (dept_id, parent_id, dept_name) values (10, 0, 'fixture-dept')");
            jdbc.update("insert into sys_role (role_id, role_name, role_key, role_sort, status) values (20, 'fixture-role', 'fixture-key', 1, '0')");
            SysUserServiceImpl service = new SysUserServiceImpl();
            SysUserMapper mapper = context.getBean(SysUserMapper.class);
            ReflectionTestUtils.setField(service, "baseMapper", mapper);
            ReflectionTestUtils.setField(service, "userMapper", mapper);
            ReflectionTestUtils.setField(service, "userRoleMapper", context.getBean(SysUserRoleMapper.class));
            SysUser user = new SysUser();
            user.setDeptId(10L);
            user.setUserName("fixture-user");
            user.setNickName("original nick");
            user.setPassword("fixture-password");
            user.setAvatar("fixture-avatar");
            user.setEmail("fixture@example.test");
            user.setPhonenumber("fixture");
            user.setRoleIds(new Long[]{20L});
            assertThat(service.insertUser(user)).isEqualTo(1);
            assertThat(user.getUserId()).isPositive();
            assertThat(context.getBean(SysDeptServiceImpl.class).checkDeptExistUser(10L)).isTrue();
            assertThat(context.getBean(SysDeptServiceImpl.class).checkDeptExistUser(11L)).isFalse();
            SysUser details = service.selectUserById(user.getUserId());
            assertThat(details.getDept().getDeptName()).isEqualTo("fixture-dept");
            assertThat(details.getRoles()).hasSize(1);
            SysUser patch = new SysUser();
            patch.setUserId(user.getUserId());
            patch.setNickName("");
            patch.setAvatar("");
            patch.setEmail("");
            patch.setPhonenumber("");
            patch.setRoleIds(new Long[]{20L});
            assertThat(service.updateUser(patch)).isEqualTo(1);
            SysUser updated = mapper.selectById(user.getUserId());
            assertThat(updated.getNickName()).isEqualTo("original nick");
            assertThat(updated.getAvatar()).isEqualTo("fixture-avatar");
            assertThat(updated.getEmail()).isEmpty();
            assertThat(updated.getPhonenumber()).isEmpty();
            assertThat(updated.getStatus()).isEqualTo("0");
            assertThat(service.resetUserPwd(user.getUserId(), "fixture-new-password")).isEqualTo(1);
            assertThat(mapper.selectById(user.getUserId()).getPwdUpdateDate()).isNotNull();
            assertThatIllegalArgumentException().isThrownBy(() -> service.deleteUserByIds(new Long[0]));
            assertThat(jdbc.queryForObject("select count(*) from sys_user_role", Integer.class)).isEqualTo(1);
            assertThat(service.deleteUserById(user.getUserId())).isEqualTo(1);
            assertThat(mapper.selectById(user.getUserId()).getDelFlag()).isEqualTo("2");
            assertThat(jdbc.queryForObject("select count(*) from sys_user_role", Integer.class)).isZero();
        });
    }

    /** 公告正文可查询和清空，业务方法显式设置任务/登录/操作日志时间，筛选只读取允许字段。 */
    @Test
    void readsNoticeContentAndSetsLogTimesExplicitly() {
        withDatabase(context -> {
            SysNoticeServiceImpl notices = new SysNoticeServiceImpl(context.getBean(SysNoticeMapper.class),
                    context.getBean(SysNoticeReadMapper.class));
            SysNotice notice = new SysNotice();
            notice.setNoticeId(999L);
            notice.setNoticeTitle("fixture-notice");
            notice.setNoticeType("1");
            notice.setNoticeContent("<p>通知内容</p>");
            assertThat(notices.insertNotice(notice)).isEqualTo(1);
            assertThat(notice.getNoticeId()).isNotEqualTo(999L);
            assertThat(notices.selectNoticeById(notice.getNoticeId()).getNoticeContent()).isEqualTo("<p>通知内容</p>");
            SysNotice patch = new SysNotice();
            patch.setNoticeId(notice.getNoticeId());
            patch.setNoticeContent("");
            assertThat(notices.updateNotice(patch)).isEqualTo(1);
            assertThat(notices.getById(notice.getNoticeId()).getNoticeContent()).isEmpty();
            JdbcTemplate jdbc = new JdbcTemplate(context.getBean(DataSource.class));
            jdbc.update("insert into sys_notice_read (notice_id, user_id, read_time) values (?, 1, CURRENT_TIMESTAMP)", notice.getNoticeId());
            assertThat(notices.deleteNoticeByIds(new Long[]{notice.getNoticeId()})).isEqualTo(1);
            assertThat(jdbc.queryForObject("select count(*) from sys_notice_read", Integer.class)).isZero();
            SysJobLogServiceImpl logs = new SysJobLogServiceImpl();
            ReflectionTestUtils.setField(logs, "baseMapper", context.getBean(SysJobLogMapper.class));
            SysJobLog log = new SysJobLog();
            log.setJobName("fixture-job");
            log.setJobGroup("fixture-group");
            log.setInvokeTarget("fixture.target()");
            log.setStatus("0");
            log.setCreateTime(new Date(1));
            logs.addJobLog(log);
            assertThat(log.getJobLogId()).isPositive();
            assertThat(logs.getById(log.getJobLogId()).getCreateTime()).isAfter(new Date(1));
            SysJobLog query = new SysJobLog();
            query.setJobGroup("fixture-group");
            assertThat(logs.selectJobLogList(query)).hasSize(1);
            query.setJobGroup("missing-group");
            assertThat(logs.selectJobLogList(query)).isEmpty();
            SysLogininforServiceImpl logins = new SysLogininforServiceImpl();
            ReflectionTestUtils.setField(logins, "baseMapper", context.getBean(SysLogininforMapper.class));
            SysLogininfor login = new SysLogininfor();
            login.setUserName("fixture-user");
            login.setStatus("0");
            login.setLoginTime(new Date(1));
            logins.insertLogininfor(login);
            assertThat(logins.getById(login.getInfoId()).getLoginTime()).isAfter(new Date(1));
            SysOperLogServiceImpl operations = new SysOperLogServiceImpl();
            ReflectionTestUtils.setField(operations, "baseMapper", context.getBean(SysOperLogMapper.class));
            SysOperLog operation = new SysOperLog();
            operation.setTitle("fixture-operation");
            operation.setBusinessType(1);
            operation.setStatus(0);
            operation.setOperTime(new Date(1));
            operations.insertOperlog(operation);
            assertThat(operations.getById(operation.getOperId()).getOperTime()).isAfter(new Date(1));
        });
    }

    /** 为每个用例新建当前系统表结构，扫描真实 Mapper 并使用实际 Plus 自动装配，结束后销毁内存库。 */
    private void withDatabase(Consumer<AssertableApplicationContext> assertions) {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(MybatisPlusAutoConfiguration.class, PageHelperAutoConfiguration.class))
                .withUserConfiguration(ApplicationConfig.class)
                .withBean(SysDeptServiceImpl.class)
                .withBean(DataSource.class, () -> new DriverManagerDataSource(
                        "jdbc:h2:mem:plus_" + UUID.randomUUID() + ";MODE=MySQL;DB_CLOSE_DELAY=-1", "sa", ""))
                .withPropertyValues("mybatis-plus.mapper-locations=classpath*:mapper/**/*Mapper.xml",
                        "mybatis-plus.type-aliases-package=com.ruoyi.**.domain",
                        "mybatis-plus.configuration.map-underscore-to-camel-case=false", "pagehelper.helperDialect=h2")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    DataSource dataSource = context.getBean(DataSource.class);
                    ResourceDatabasePopulator schema = new ResourceDatabasePopulator(new ClassPathResource("mybatisplus/schema.sql"));
                    schema.setSqlScriptEncoding("UTF-8");
                    schema.execute(dataSource);
                    try {
                        assertions.accept(context);
                    } finally {
                        new JdbcTemplate(dataSource).execute("shutdown");
                    }
                });
    }

    /** 返回满足原表必填字段的参数配置数据，时间保持为空，供通用 save 与业务新增分别验证。 */
    private SysConfig config(String code, String name) {
        SysConfig config = new SysConfig();
        config.setConfigKey(code);
        config.setConfigName(name);
        config.setConfigValue("fixture-value");
        config.setConfigType("N");
        return config;
    }
}
