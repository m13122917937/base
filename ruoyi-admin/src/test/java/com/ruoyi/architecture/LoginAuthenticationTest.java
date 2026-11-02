package com.ruoyi.architecture;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;

import com.ruoyi.biz.system.SysLoginBiz;
import com.ruoyi.biz.system.SysRegisterBiz;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.domain.model.RegisterBody;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.framework.convert.FrameworkBoundaryConvert;
import com.ruoyi.framework.facade.*;
import com.ruoyi.framework.facade.impl.SysLoginFacade;
import com.ruoyi.framework.facade.impl.SysRegisterFacade;
import com.ruoyi.framework.manager.AsyncManager;
import com.ruoyi.framework.model.bo.RegisterParam;
import com.ruoyi.framework.web.exception.GlobalExceptionHandler;
import com.ruoyi.framework.web.service.SysLoginService;
import com.ruoyi.framework.web.service.SysRegisterService;
import com.ruoyi.framework.web.service.SysPermissionService;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.framework.web.service.UserDetailsServiceImpl;
import com.ruoyi.system.facade.ISysConfigFacade;
import com.ruoyi.system.facade.ISysMenuFacade;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.web.controller.system.SysLoginController;
import com.ruoyi.web.controller.system.SysRegisterController;
import com.ruoyi.web.convert.WebBoundaryConvert;
import com.ruoyi.web.form.framework.RegisterBodyForm;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 使用 Spring 实际认证配置和 BCrypt 验证精简后的账号密码流程，不连接数据库或 Redis。
 */
class LoginAuthenticationTest {
    /**
     * 连续输错超过原阈值后立即用正确密码登录；真实 MVC→Biz→Facade→Service→Security 链路只接收账号密码。
     * 成功前不能签发令牌；登录不读写错误次数缓存，保留登录日志任务与用户登录信息更新。
     *
     * @throws Exception 测试 HTTP 调用失败
     */
    @Test
    void usernamePasswordLoginWorksAfterRepeatedFailures() throws Exception {
        ISysUserService users = mock(ISysUserService.class);
        SysPermissionService permissions = mock(SysPermissionService.class);
        SysUser user = user();
        when(users.selectUserByUserName("fixture-user")).thenReturn(user);
        when(permissions.getMenuPermission(user)).thenReturn(Set.of("system:user:list"));
        authenticationContext(users, permissions).run(context -> {
            assertNull(context.getStartupFailure());
            AuthenticationManager manager = context.getBean(AuthenticationConfiguration.class).getAuthenticationManager();
            TokenService tokens = mock(TokenService.class);
            when(tokens.createToken(any(LoginUser.class))).thenReturn("fixture-token");
            ISysConfigService config = mock(ISysConfigService.class);
            when(config.selectConfigByKey("sys.login.blackIPList")).thenReturn("");
            SysLoginService service = new SysLoginService(tokens, manager, users, config);
            SysLoginBiz biz = new SysLoginBiz(new SysLoginFacade(service), mock(ISysMenuFacade.class),
                    mock(ISysPermissionFacade.class), mock(ITokenFacade.class), mock(ISysConfigFacade.class), mock(ILoginSessionFacade.class));
            MockMvc mvc = MockMvcBuilders.standaloneSetup(new SysLoginController(biz))
                    .setControllerAdvice(new GlobalExceptionHandler()).build();
            ScheduledExecutorService executor = mock(ScheduledExecutorService.class);
            try (MockedStatic<SpringUtils> spring = mockStatic(SpringUtils.class)) {
                spring.when(() -> SpringUtils.getBean("scheduledExecutorService")).thenReturn(executor);
                for (int attempt = 0; attempt < 8; attempt++) {
                    mvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON)
                                    .content("{\"username\":\"fixture-user\",\"password\":\"wrong-password\"}"))
                            .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(500))
                            .andExpect(jsonPath("$.msg").value("用户不存在/密码错误"))
                            .andExpect(jsonPath("$.token").doesNotExist());
                }
                verify(tokens, never()).createToken(any());
                mvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON)
                                .content("{\"username\":\"fixture-user\",\"password\":\"fixture-password\"}"))
                        .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200))
                        .andExpect(jsonPath("$.token").value("fixture-token"));
                verify(tokens).createToken(argThat(principal -> principal.getUserId().equals(12L)));
                verify(users).updateLoginInfo(eq(12L), anyString(), any());
            }
        });
    }

    /**
     * 直接使用 Spring 生成的认证管理器，确认密码错误仍被 BCrypt 拒绝，停用与删除账号仍不能认证。
     * 此上下文完全不注册 Redis 客户端或密码计数服务，避免精简功能时绕过真正的密码验证。
     */
    @Test
    void stillRejectsWrongPasswordAndUnavailableAccounts() {
        ISysUserService users = mock(ISysUserService.class);
        SysPermissionService permissions = mock(SysPermissionService.class);
        SysUser user = user();
        when(users.selectUserByUserName("fixture-user")).thenReturn(user);
        when(permissions.getMenuPermission(user)).thenReturn(Set.of());
        authenticationContext(users, permissions).run(context -> {
            AuthenticationManager manager = context.getBean(AuthenticationConfiguration.class).getAuthenticationManager();
            assertThrows(BadCredentialsException.class, () -> manager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated("fixture-user", "wrong-password")));
            user.setStatus("1");
            {
                assertThrows(AuthenticationException.class, () -> manager.authenticate(
                        UsernamePasswordAuthenticationToken.unauthenticated("fixture-user", "fixture-password")));
                user.setStatus("0");
                user.setDelFlag("2");
                assertThrows(AuthenticationException.class, () -> manager.authenticate(
                        UsernamePasswordAuthenticationToken.unauthenticated("fixture-user", "fixture-password")));
            }
        });
    }

    /**
     * 账号密码注册参数依次通过 Web 和领域 MapStruct 转换，保留账号与密码。
     */
    @Test
    void preservesRegistrationCredentials() {
        RegisterBodyForm form = new RegisterBodyForm();
        form.setUsername("fixture-user");
        form.setPassword("fixture-password");
        RegisterParam param = WebBoundaryConvert.INSTANCE.toRegisterParam(form);
        RegisterBody body = FrameworkBoundaryConvert.INSTANCE.toRegisterBody(param);
        assertEquals(form.getUsername(), body.getUsername());
        assertEquals(form.getPassword(), body.getPassword());
    }

    /**
     * 通过真实 MVC、Biz、Facade 和注册服务，仅提交账号密码即可注册；保存的密码必须是 BCrypt 哈希。
     * 用户服务及异步日志调度使用模拟对象，不连接数据库、Redis 或执行后台日志任务。
     *
     * @throws Exception HTTP 调用或断言失败
     */
    @Test
    void registersUsingOnlyUsernameAndPassword() throws Exception {
        ISysUserService users = mock(ISysUserService.class);
        when(users.checkUserNameUnique(any(SysUser.class))).thenReturn(true);
        when(users.registerUser(any(SysUser.class))).thenReturn(true);
        MockMvc mvc = registrationMvc(users, true);
        try (MockedStatic<SpringUtils> spring = mockStatic(SpringUtils.class)) {
            spring.when(() -> SpringUtils.getBean("scheduledExecutorService"))
                    .thenReturn(mock(ScheduledExecutorService.class));
            try (MockedStatic<AsyncManager> async = mockStatic(AsyncManager.class)) {
                AsyncManager manager = mock(AsyncManager.class);
                async.when(AsyncManager::me).thenReturn(manager);
                mvc.perform(post("/register").contentType(MediaType.APPLICATION_JSON)
                                .content("{\"username\":\"fixture-user\",\"password\":\"fixture-password\"}"))
                        .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
                ArgumentCaptor<SysUser> saved = ArgumentCaptor.forClass(SysUser.class);
                verify(users).registerUser(saved.capture());
                assertEquals("fixture-user", saved.getValue().getUserName());
                assertEquals("fixture-user", saved.getValue().getNickName());
                assertNotNull(saved.getValue().getPwdUpdateDate());
                assertTrue(SecurityUtils.matchesPassword("fixture-password", saved.getValue().getPassword()));
                verify(manager).execute(any(java.util.TimerTask.class));
            }
        }
    }

    /**
     * 无额外注册凭据时仍拒绝短密码和重复账号，并保留注册总开关；所有失败都不能保存用户。
     *
     * @throws Exception HTTP 调用或断言失败
     */
    @Test
    void registrationStillEnforcesValidationAndFeatureSwitch() throws Exception {
        ISysUserService users = mock(ISysUserService.class);
        MockMvc mvc = registrationMvc(users, true);
        mvc.perform(post("/register").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"fixture-user\",\"password\":\"123\"}"))
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("密码长度必须在5到20个字符之间"));
        mvc.perform(post("/register").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"fixture-user\",\"password\":\"fixture-password\"}"))
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("保存用户'fixture-user'失败，注册账号已存在"));
        verify(users, never()).registerUser(any());
        clearInvocations(users);
        registrationMvc(users, false).perform(post("/register").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"fixture-user\",\"password\":\"fixture-password\"}"))
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("当前系统没有开启注册功能！"));
        verifyNoInteractions(users);
    }

    /**
     * 装配真实注册调用链，模拟注册开关与用户持久化，保留生产异常响应适配。
     *
     * @param users 模拟用户服务
     * @param enabled 是否允许注册
     * @return 无数据库和缓存依赖的 MVC 测试入口
     */
    private MockMvc registrationMvc(ISysUserService users, boolean enabled) {
        ISysConfigFacade config = mock(ISysConfigFacade.class);
        when(config.queryConfigByKey("sys.account.registerUser")).thenReturn(Boolean.toString(enabled));
        SysRegisterBiz biz = new SysRegisterBiz(new SysRegisterFacade(new SysRegisterService(users)), config);
        return MockMvcBuilders.standaloneSetup(new SysRegisterController(biz))
                .setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    /**
     * 用与生产一致的 AuthenticationConfiguration 和用户服务装配认证提供器，不设置验证码或次数相关属性。
     */
    private ApplicationContextRunner authenticationContext(ISysUserService users, SysPermissionService permissions) {
        return new ApplicationContextRunner().withUserConfiguration(AuthenticationConfiguration.class)
                .withBean(UserDetailsService.class, () -> new UserDetailsServiceImpl(users, permissions))
                .withBean(PasswordEncoder.class, () -> new BCryptPasswordEncoder(4));
    }

    /**
     * 构造有效账号状态及 BCrypt 密码哈希，只用于本地认证回归，不使用业务账号。
     */
    private SysUser user() {
        SysUser user = new SysUser(12L);
        user.setUserName("fixture-user");
        user.setDeptId(7L);
        user.setStatus("0");
        user.setDelFlag("0");
        user.setRoles(List.of());
        user.setPassword(new BCryptPasswordEncoder(4).encode("fixture-password"));
        return user;
    }
}
