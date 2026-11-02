package com.ruoyi.architecture;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ruoyi.biz.system.SysConfigBiz;
import com.ruoyi.biz.system.SysDeptBiz;
import com.ruoyi.biz.system.SysUserBiz;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.framework.web.exception.GlobalExceptionHandler;
import com.ruoyi.system.facade.ISysConfigFacade;
import com.ruoyi.system.facade.ISysDeptFacade;
import com.ruoyi.system.facade.ISysRoleFacade;
import com.ruoyi.system.facade.ISysUserFacade;
import com.ruoyi.system.model.bo.SysUserBO;
import com.ruoyi.web.controller.system.SysConfigController;
import com.ruoyi.web.controller.system.SysDeptController;
import com.ruoyi.web.controller.system.SysUserController;
import com.ruoyi.web.vo.system.SysUserVO;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 通过真实 Web 入口检查职责拆分后的分页、响应协议、业务异常及文件内容，不连接数据库。
 */
class BizWebBoundaryTest {
    /**
     * HTTP 分页参数经真实 Biz 显式传给 Facade，调用前没有线程分页；Web 转换后仍保留数据库总数。
     *
     * @throws Exception HTTP 调用失败
     */
    @Test
    void preservesPagingThroughControllerAndPlainBiz() throws Exception {
        ISysUserFacade users = mock(ISysUserFacade.class);
        when(users.queryUserList(any(SysUserBO.class), any(PageDomain.class))).thenAnswer(invocation -> {
            assertNull(PageHelper.getLocalPage(), "Controller 和 Biz 不应提前启动分页");
            PageDomain pageDomain = invocation.getArgument(1);
            assertEquals(2, pageDomain.getPageNum());
            assertEquals(10, pageDomain.getPageSize());
            Page<SysUserBO> page = new Page<SysUserBO>(2, 10);
            page.setTotal(87);
            SysUserBO user = new SysUserBO();
            user.setUserId(12L);
            user.setUserName("fixture-user");
            page.add(user);
            return page;
        });
        MockMvc mvc = mvc(new SysUserController(userBiz(users)));
        try {
            mvc.perform(get("/system/user/list").param("pageNum", "2").param("pageSize", "10"))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.total").value(87)).andExpect(jsonPath("$.rows[0].userId").value(12));
            assertNull(PageHelper.getLocalPage());
        } finally {
            PageHelper.clearPage();
        }
    }

    /**
     * Facade 在进入 Service 前失败时没有遗留分页状态，现有异常处理器保留原业务错误消息。
     *
     * @throws Exception HTTP 调用失败
     */
    @Test
    void clearsPagingWhenBusinessQueryFails() throws Exception {
        ISysUserFacade users = mock(ISysUserFacade.class);
        when(users.queryUserList(any(SysUserBO.class), any(PageDomain.class)))
                .thenThrow(new ServiceException("fixture-query-failed"));
        try {
            mvc(new SysUserController(userBiz(users)))
                    .perform(get("/system/user/list").param("pageNum", "1").param("pageSize", "10"))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.msg").value("fixture-query-failed"));
            assertNull(PageHelper.getLocalPage());
        } finally {
            PageHelper.clearPage();
        }
    }

    /**
     * 对组合数据只在 Web 补状态和消息，user 保持顶层并转换为 VO；字符串配置继续放 msg，不误放 data。
     *
     * @throws Exception HTTP 调用失败
     */
    @Test
    void preservesTopLevelFieldsAndStringMessage() throws Exception {
        SysUserBiz users = mock(SysUserBiz.class);
        SysUserBO user = new SysUserBO();
        user.setUserId(12L);
        when(users.getInfo(12L)).thenReturn(Map.of("data", user, "roles", List.of()));
        SysUserController controller = new SysUserController(users);
        assertInstanceOf(SysUserVO.class, controller.getInfo(12L).get("data"));
        mvc(controller).perform(get("/system/user/12"))
                .andExpect(jsonPath("$.code").value(200)).andExpect(jsonPath("$.data.userId").value(12))
                .andExpect(jsonPath("$.roles").isArray()).andExpect(jsonPath("$.posts").doesNotExist()).andExpect(jsonPath("$.postIds").doesNotExist());
        ISysConfigFacade config = mock(ISysConfigFacade.class);
        when(config.queryConfigByKey("fixture.key")).thenReturn("fixture-value");
        mvc(new SysConfigController(new SysConfigBiz(config))).perform(get("/system/config/configKey/fixture.key"))
                .andExpect(jsonPath("$.msg").value("fixture-value")).andExpect(jsonPath("$.data").doesNotExist());
    }

    /**
     * 删除被下级部门阻止时保留原 WARN 状态和消息，并确认未调用领域删除动作。
     *
     * @throws Exception HTTP 调用失败
     */
    @Test
    void preservesBusinessWarningWithoutDeleting() throws Exception {
        ISysDeptFacade departments = mock(ISysDeptFacade.class);
        when(departments.hasChildByDeptId(20L)).thenReturn(true);
        mvc(new SysDeptController(new SysDeptBiz(departments))).perform(delete("/system/dept/20"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(HttpStatus.WARN))
                .andExpect(jsonPath("$.msg").value("存在下级部门,不允许删除"));
        verify(departments, never()).removeDeptById(any());
    }

    /**
     * 用旧 BO 生成基线模板，再与当前 Controller 使用 Form 生成的 Excel 比较表头，确保导入字段契约未改变。
     *
     * @throws Exception Excel 生成或解析失败
     */
    @Test
    void preservesUserImportTemplateColumns() throws Exception {
        MockHttpServletResponse baseline = new MockHttpServletResponse();
        new ExcelUtil<SysUserBO>(SysUserBO.class).importTemplateExcel(baseline, "用户数据");
        MockHttpServletResponse actual = new MockHttpServletResponse();
        new SysUserController(mock(SysUserBiz.class)).importTemplate(actual);
        assertEquals(headers(baseline), headers(actual));
    }

    /**
     * 创建真实应用编排并仅替换领域出口，不启动 Spring 或访问数据库。
     */
    private SysUserBiz userBiz(ISysUserFacade users) {
        return new SysUserBiz(users, mock(ISysRoleFacade.class), mock(ISysDeptFacade.class));
    }

    /**
     * 装配独立 MVC 和现有异常处理器，保留参数绑定及响应序列化。
     */
    private MockMvc mvc(Object controller) {
        return MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    /**
     * 读取内存 Excel 第一行实际表头，忽略 ZIP 元数据与文档生成时间。
     */
    private List<String> headers(MockHttpServletResponse response) throws Exception {
        try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(response.getContentAsByteArray()))) {
            List<String> result = new ArrayList<String>();
            for (Cell cell : workbook.getSheetAt(0).getRow(0)) {
                result.add(cell.getStringCellValue());
            }
            assertFalse(result.isEmpty());
            return result;
        }
    }
}
