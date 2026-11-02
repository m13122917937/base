package com.ruoyi.architecture;

import java.util.UUID;
import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceAutoConfigure;
import com.alibaba.druid.support.jakarta.StatViewServlet;
import com.alibaba.druid.support.jakarta.WebStatFilter;
import com.ruoyi.common.exception.user.UserPasswordNotMatchException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 验证实际单数据源配置的属性绑定与事务行为，异常消息不依赖国际化容器。
 */
class SingleDataSourceTest {
    /**
     * 加载项目真实 Druid YAML，仅将连接替换为隔离 H2，检查只有一个连接池且事务回滚使用同一个数据源。
     * 在 Web 上下文中验证监控 Servlet 和 Web 统计过滤器均未注册。
     * 不注册动态数据源或路由切面，不连接开发/业务数据库。
     */
    @Test
    void bindsSingleDruidPoolAndRollsBackTransactions() {
        new WebApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(DruidDataSourceAutoConfigure.class,
                        DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class))
                .withInitializer(context -> {
                    try {
                        new YamlPropertySourceLoader().load("druid", new ClassPathResource("application-dev.yml"))
                                .forEach(source -> context.getEnvironment().getPropertySources().addLast(source));
                    } catch (java.io.IOException e) {
                        throw new IllegalStateException("无法加载实际 Druid 配置", e);
                    }
                })
                .withPropertyValues("spring.datasource.url=jdbc:h2:mem:single_" + UUID.randomUUID(),
                        "spring.datasource.driverClassName=org.h2.Driver", "spring.datasource.username=sa",
                        "spring.datasource.password=", "spring.datasource.druid.initialSize=0",
                        "spring.datasource.druid.minIdle=0")
                .run(context -> {
                    assertThat(context).hasNotFailed().hasSingleBean(DataSource.class);
                    assertThat(context.getBeansOfType(ServletRegistrationBean.class).values())
                            .noneMatch(registration -> registration.getServlet() instanceof StatViewServlet);
                    assertThat(context.getBeansOfType(FilterRegistrationBean.class).values())
                            .noneMatch(registration -> registration.getFilter() instanceof WebStatFilter);
                    DruidDataSource dataSource = assertInstanceOf(DruidDataSource.class, context.getBean(DataSource.class));
                    assertEquals(20, dataSource.getMaxActive());
                    assertEquals(60000, dataSource.getMaxWait());
                    assertFalse(context.containsBean("dynamicDataSource"));
                    assertFalse(context.containsBean("masterDataSource"));
                    assertFalse(context.containsBean("slaveDataSource"));
                    DataSourceTransactionManager manager = context.getBean(DataSourceTransactionManager.class);
                    assertSame(dataSource, manager.getDataSource());
                    JdbcTemplate jdbc = new JdbcTemplate(dataSource);
                    jdbc.execute("create table fixture_record (id int primary key)");
                    new TransactionTemplate(manager).executeWithoutResult(status -> {
                        jdbc.update("insert into fixture_record values (1)");
                        status.setRollbackOnly();
                    });
                    assertEquals(0, jdbc.queryForObject("select count(*) from fixture_record", Integer.class));
                });
    }

    /**
     * 在无 Spring 上下文或语言资源时直接生成用户密码错误的中文提示。
     */
    @Test
    void returnsChineseMessagesWithoutMessageSource() {
        assertEquals("用户不存在/密码错误", new UserPasswordNotMatchException().getMessage());
    }
}
