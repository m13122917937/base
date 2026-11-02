package com.ruoyi.architecture;

import java.util.List;
import java.util.UUID;
import javax.sql.DataSource;

import com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceAutoConfigure;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInterceptor;
import com.github.pagehelper.autoconfigure.PageHelperAutoConfiguration;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.biz.system.SysConfigBiz;
import com.ruoyi.framework.config.ApplicationConfig;
import com.ruoyi.quartz.domain.SysJob;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.mapper.SysConfigMapper;
import com.ruoyi.system.facade.impl.SysConfigFacade;
import com.ruoyi.system.model.bo.SysConfigBO;
import com.ruoyi.system.service.impl.SysConfigServiceImpl;
import com.ruoyi.web.controller.system.SysConfigController;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.SqlSessionTemplate;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.autoconfigure.SpringBootVFS;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 使用实际 YAML、Mapper XML 和自动装配，在隔离数据库中验证配置精简后的持久化行为。
 */
class MyBatisAutoConfigurationTest {
    /**
     * 自动创建唯一会话工厂和模板，检查原配置、多模块别名、Mapper 与分页插件均正确装配。
     * 检查原参数配置写入语句的主键生成器，经真实 Controller→Biz→Facade→Service→Mapper 验证分页及全量导出，再验证删除事务回滚。
     * 测试数据使用标准时间函数写入 H2，不执行 MySQL 专属的 sysdate()，不修改生产 SQL。
     */
    @Test
    void autoConfiguresExistingMappersAndPreservesDatabaseBehavior() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(DruidDataSourceAutoConfigure.class,
                        DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class,
                        MybatisPlusAutoConfiguration.class, PageHelperAutoConfiguration.class))
                .withUserConfiguration(ApplicationConfig.class)
                .withInitializer(context -> {
                    try {
                        YamlPropertySourceLoader loader = new YamlPropertySourceLoader();
                        for (String resource : List.of("application.yml", "application-dev.yml")) {
                            loader.load(resource, new ClassPathResource(resource)).forEach(source ->
                                    context.getEnvironment().getPropertySources().addLast(source));
                        }
                    } catch (java.io.IOException e) {
                        throw new IllegalStateException("无法加载项目 MyBatis 配置", e);
                    }
                })
                .withPropertyValues("spring.datasource.url=jdbc:h2:mem:mybatis_" + UUID.randomUUID() + ";MODE=MySQL",
                        "spring.datasource.driverClassName=org.h2.Driver", "spring.datasource.username=sa",
                        "spring.datasource.password=", "spring.datasource.druid.initialSize=0",
                        "spring.datasource.druid.minIdle=0", "spring.datasource.druid.statViewServlet.enabled=false",
                        "spring.datasource.druid.webStatFilter.enabled=false")
                .run(context -> {
                    assertThat(context).hasNotFailed().hasSingleBean(SqlSessionFactory.class)
                            .hasSingleBean(SqlSessionTemplate.class).hasSingleBean(DataSource.class);
                    SqlSessionFactory factory = context.getBean(SqlSessionFactory.class);
                    assertSame(factory, context.getBean(SqlSessionTemplate.class).getSqlSessionFactory());
                    assertNull(context.getBean(MybatisPlusProperties.class).getConfigLocation());
                    org.apache.ibatis.session.Configuration configuration = factory.getConfiguration();
                    assertTrue(configuration.isCacheEnabled());
                    assertTrue(configuration.isUseGeneratedKeys());
                    assertEquals(ExecutorType.SIMPLE, configuration.getDefaultExecutorType());
                    assertEquals(Slf4jImpl.class, configuration.getLogImpl());
                    assertFalse(configuration.isMapUnderscoreToCamelCase());
                    assertEquals(SpringBootVFS.class, configuration.getVfsImpl());
                    assertEquals(SysUser.class, configuration.getTypeAliasRegistry().resolveAlias("SysUser"));
                    assertEquals(SysJob.class, configuration.getTypeAliasRegistry().resolveAlias("SysJob"));
                    assertTrue(configuration.hasStatement("com.ruoyi.quartz.mapper.SysJobMapper.selectList"));
                    assertEquals(1L, configuration.getInterceptors().stream().filter(PageInterceptor.class::isInstance).count());
                    org.apache.ibatis.mapping.MappedStatement insert = configuration.getMappedStatement("com.ruoyi.system.mapper.SysConfigMapper.insert");
                    assertInstanceOf(Jdbc3KeyGenerator.class, insert.getKeyGenerator());
                    assertArrayEquals(new String[]{"configId"}, insert.getKeyProperties());

                    JdbcTemplate jdbc = new JdbcTemplate(context.getBean(DataSource.class));
                    jdbc.execute("create table sys_config (config_id bigint auto_increment primary key, config_key varchar(64),"
                            + " config_name varchar(64), config_value varchar(64), config_type varchar(1), create_by varchar(64),"
                            + " create_time timestamp, update_by varchar(64), update_time timestamp, remark varchar(255))");
                    jdbc.update("insert into sys_config (config_id, config_name, config_value, config_type) values (1, 'fixture-first', '1', 'N')");
                    jdbc.update("insert into sys_config (config_id, config_name, config_value, config_type) values (2, 'fixture-second', '2', 'N')");
                    SysConfigMapper mapper = context.getBean(SysConfigMapper.class);
                    SysConfig config = mapper.selectById(1L);
                    assertEquals("fixture-first", config.getConfigName());
                    assertEquals("1", config.getConfigValue());
                    SysConfigServiceImpl service = new SysConfigServiceImpl();
                    ReflectionTestUtils.setField(service, "baseMapper", mapper);
                    SysConfigBiz biz = new SysConfigBiz(new SysConfigFacade(service));
                    MockMvc mvc = MockMvcBuilders.standaloneSetup(new SysConfigController(biz)).build();
                    mvc.perform(get("/system/config/list").param("pageNum", "2").param("pageSize", "1")
                                    .param("orderByColumn", "configId").param("isAsc", "descending")
                                    .param("reasonable", "false"))
                            .andExpect(status().isOk()).andExpect(jsonPath("$.total").value(2))
                            .andExpect(jsonPath("$.rows.length()").value(1))
                            .andExpect(jsonPath("$.rows[0].configId").value(1));
                    assertNull(PageHelper.getLocalPage());
                    mvc.perform(get("/system/config/list").param("pageNum", "9").param("pageSize", "1")
                                    .param("reasonable", "false"))
                            .andExpect(jsonPath("$.total").value(2)).andExpect(jsonPath("$.rows").isEmpty());
                    assertNull(PageHelper.getLocalPage());
                    mvc.perform(get("/system/config/list"))
                            .andExpect(jsonPath("$.total").value(2)).andExpect(jsonPath("$.rows.length()").value(2));
                    assertEquals(2, biz.export(new SysConfigBO()).size());
                    assertNull(PageHelper.getLocalPage());
                    DataSourceTransactionManager transactions = context.getBean(DataSourceTransactionManager.class);
                    new TransactionTemplate(transactions).executeWithoutResult(status -> {
                        assertEquals(1, mapper.deleteById(config.getConfigId()));
                        assertNull(mapper.selectById(config.getConfigId()));
                        status.setRollbackOnly();
                    });
                    assertEquals(2, mapper.selectList(null).size());
                    assertNotNull(mapper.selectById(config.getConfigId()));
                });
    }
}
