package com.ruoyi.architecture;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.PageHelper;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.framework.aspectj.DataScopeAspect;
import com.ruoyi.framework.facade.impl.TokenFacade;
import com.ruoyi.framework.facade.impl.SysPermissionFacade;
import com.ruoyi.framework.web.service.SysPermissionService;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.system.convert.BoundaryConvert;
import com.ruoyi.system.facade.impl.SysUserFacade;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.system.model.bo.LoginSessionBO;
import com.ruoyi.system.model.bo.SysUserBO;
import com.ruoyi.system.model.bo.SysRoleBO;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.system.service.impl.SysUserServiceImpl;
import com.ruoyi.web.convert.WebBoundaryConvert;
import com.ruoyi.web.convert.WebResponses;
import com.ruoyi.web.form.system.SysUserForm;
import com.ruoyi.web.vo.system.SysUserVO;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.ruoyi.framework.security.context.PermissionContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 验证分层后最容易静默改变的分页、密码、认证缓存及实际数据权限切面。
 */
class BoundaryCompatibilityTest {
    /**
     * 模拟权限 Service 更新逐角色 permissions，确认转换后的 BO 保留该状态供 DataScope 使用。
     */
    @Test
    void keepsRolePermissionsWhenRecomputingMenuPermissions() {
        SysRoleBO role = new SysRoleBO();
        role.setRoleId(2L);
        role.setPermissions(Set.of("old:permission"));
        SysUserBO user = new SysUserBO(12L);
        user.setRoles(List.of(role));
        SysPermissionService service = mock(SysPermissionService.class);
        when(service.getMenuPermission(any(SysUser.class))).thenAnswer(invocation -> {
            SysUser principal = invocation.getArgument(0);
            principal.getRoles().get(0).setPermissions(Set.of("system:user:list"));
            return Set.of("system:user:list");
        });
        Set<String> permissions = new SysPermissionFacade(service).getMenuPermission(user);
        assertEquals(Set.of("system:user:list"), permissions);
        assertEquals(permissions, user.getRoles().get(0).getPermissions());
        assertEquals(Set.of("old:permission"), role.getPermissions(), "只替换当前 BO 的角色快照，不污染原共享角色");
    }

    /**
     * 模拟令牌服务续期，验证缓存的原对象类型与调用方会话 BO 同时保留新的时间。
     */
    @Test
    void synchronizesRefreshedSessionTimestamps() {
        LoginSessionBO session = new LoginSessionBO();
        session.setLoginTime(1L);
        session.setExpireTime(2L);
        TokenService service = mock(TokenService.class);
        doAnswer(invocation -> {
            LoginUser cached = invocation.getArgument(0);
            cached.setLoginTime(100L);
            cached.setExpireTime(200L);
            return null;
        }).when(service).refreshToken(any(LoginUser.class));
        new TokenFacade(service, mock(SysPermissionService.class)).refreshToken(session);
        assertEquals(100L, session.getLoginTime());
        assertEquals(200L, session.getExpireTime());
    }

    /**
     * 模拟带总数的数据库分页，依次通过 Service→Facade→Web，检查原分页总数及独立 VO。
     */
    @Test
    void preservesPageTotalAcrossBothBoundaries() {
        ISysUserService service = mock(ISysUserService.class);
        Page<SysUser> page = new Page<SysUser>(2, 10);
        page.setTotal(87);
        SysUser user = new SysUser(12L);
        user.setUserName("fixture-user");
        page.add(user);
        when(service.selectUserList(any(SysUser.class))).thenReturn(page);
        List<SysUserBO> data = new SysUserFacade(service).queryUserList(new SysUserBO());
        assertEquals(87, new PageInfo<SysUserBO>(data).getTotal());
        TableDataInfo table = new TableDataInfo(data, new PageInfo<SysUserBO>(data).getTotal());
        table.setCode(200);
        table.setMsg("查询成功");
        TableDataInfo response = WebResponses.toWeb(table);
        assertEquals(87, response.getTotal());
        assertInstanceOf(SysUserVO.class, response.getRows().get(0));
        assertEquals("fixture-user", ((SysUserVO) response.getRows().get(0)).getUserName());
        assertSame(user, page.get(0), "转换不能改写原持久化列表");
    }

    /**
     * 将密码表单转换到领域，确认写入可用；响应的 data 与附加键都转为 VO 且不序列化密码。
     *
     * @throws Exception Jackson 序列化失败
     */
    @Test
    void preservesPasswordWriteOnlyAndDynamicResponseKeys() throws Exception {
        SysUserForm form = new SysUserForm();
        form.setUserName("fixture-user");
        form.setPassword("fixture-password");
        SysUserBO user = WebBoundaryConvert.INSTANCE.toSysUserBO(form);
        assertEquals("fixture-password", user.getPassword());
        AjaxResult original = AjaxResult.success(user);
        original.put("selectedUser", user);
        original.put("roleIds", new Long[]{2L, 3L});
        AjaxResult response = WebResponses.toWeb(original);
        assertInstanceOf(SysUserVO.class, response.get("data"));
        assertInstanceOf(SysUserVO.class, response.get("selectedUser"));
        JsonNode json = new ObjectMapper().valueToTree(response);
        assertFalse(json.get("data").has("password"));
        assertFalse(json.get("selectedUser").has("password"));
        assertEquals(2, json.get("roleIds").size());
        assertSame(user, original.get("data"));
    }

    /**
     * 往返转换认证主体后写回原 TokenService，检查 Redis 入参仍是 LoginUser，令牌、时间和权限完整。
     */
    @Test
    void keepsExistingLoginCacheTypeAndMetadata() {
        SysUser user = new SysUser(12L);
        user.setUserName("fixture-user");
        user.setPassword("fixture-hash");
        user.setPwdUpdateDate(new Date(12345L));
        LoginUser cached = new LoginUser(12L, 7L, user, Set.of("system:user:list"));
        cached.setToken("fixture-token");
        cached.setExpireTime(45678L);
        cached.setLoginTime(12345L);
        cached.setIpaddr("127.0.0.1");
        LoginSessionBO session = BoundaryConvert.INSTANCE.toLoginSessionBO(cached);
        session.getUser().setNickName("修改后的显示名");
        TokenService tokens = mock(TokenService.class);
        new TokenFacade(tokens, mock(SysPermissionService.class)).setLoginUser(session);
        org.mockito.ArgumentCaptor<LoginUser> capture = org.mockito.ArgumentCaptor.forClass(LoginUser.class);
        verify(tokens).setLoginUser(capture.capture());
        LoginUser restored = capture.getValue();
        assertEquals(LoginUser.class, restored.getClass());
        assertEquals("fixture-token", restored.getToken());
        assertEquals(45678L, restored.getExpireTime());
        assertEquals(cached.getPermissions(), restored.getPermissions());
        assertEquals("fixture-hash", restored.getPassword());
        assertEquals(user.getPwdUpdateDate(), restored.getUser().getPwdUpdateDate());
        assertEquals("修改后的显示名", restored.getUser().getNickName());
        assertNull(cached.getUser().getNickName(), "失败前的应用修改不能提前污染缓存主体");
    }

    /**
     * 通过真实 DataScopeAspect 代理调用用户 Service 的全量与分页入口，检查 BO 转换后仍清空客户端片段并追加仅本人范围。
     * 不连接数据库；Mapper 捕获的实参就是切面处理后真实使用的 SQL 参数。
     */
    @Test
    void dataScopeStillAppliesAfterFacadeConversion() {
        SysRole role = new SysRole();
        role.setRoleId(2L);
        role.setStatus("0");
        role.setDataScope("5");
        role.setPermissions(Set.of("system:user:list"));
        SysUser principal = new SysUser(12L);
        principal.setDeptId(7L);
        principal.setRoles(List.of(role));
        LoginUser login = new LoginUser(12L, 7L, principal, Set.of("system:user:list"));
        SysUserMapper mapper = mock(SysUserMapper.class);
        AtomicReference<SysUser> captured = new AtomicReference<SysUser>();
        when(mapper.selectUserList(any(SysUser.class))).thenAnswer(invocation -> {
            captured.set(invocation.getArgument(0));
            return List.of();
        });
        SysUserServiceImpl target = new SysUserServiceImpl();
        ReflectionTestUtils.setField(target, "userMapper", mapper);
        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        factory.addAspect(new DataScopeAspect());
        ISysUserService proxied = factory.getProxy();
        SysUserBO query = new SysUserBO();
        query.getParams().put("dataScope", "OR 1=1");
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(login, null, List.of()));
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
        PermissionContextHolder.setContext("system:user:list");
        try {
            new SysUserFacade(proxied).queryUserList(query);
            new SysUserFacade(proxied).queryUserList(query, new PageDomain());
            assertNull(PageHelper.getLocalPage(), "目标查询完成后 Service 必须清理分页状态");
            String scope = (String) captured.get().getParams().get("dataScope");
            assertTrue(scope.contains("u.user_id = 12"), scope);
            assertFalse(scope.contains("1=1"), scope);
            assertEquals("OR 1=1", query.getParams().get("dataScope"), "领域入参转换必须与客户端对象隔离");
        } finally {
            SecurityContextHolder.clearContext();
            RequestContextHolder.resetRequestAttributes();
        }
    }
}
