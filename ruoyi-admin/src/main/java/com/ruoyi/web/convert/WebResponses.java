package com.ruoyi.web.convert;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.model.bo.SysDeptBO;
import com.ruoyi.system.model.bo.NoticeReaderBO;
import com.ruoyi.system.model.bo.SysDictDataBO;
import com.ruoyi.system.model.bo.SysDictTypeBO;
import com.ruoyi.system.model.bo.SysMenuBO;
import com.ruoyi.system.model.bo.SysRoleBO;
import com.ruoyi.system.model.bo.SysUserBO;
import com.ruoyi.quartz.model.bo.SysJobBO;
import com.ruoyi.quartz.model.bo.SysJobLogBO;
import com.ruoyi.system.model.bo.SysConfigBO;
import com.ruoyi.system.model.bo.SysLogininforBO;
import com.ruoyi.system.model.bo.SysNoticeBO;
import com.ruoyi.system.model.bo.SysOperLogBO;
import com.ruoyi.system.model.bo.RouterMetaBO;
import com.ruoyi.system.model.bo.RouterBO;

/**
 * 保持若依动态响应信封，在 HTTP 出口将内部 BO 转为独立 VO。
 */
public final class WebResponses {
    /**
     * 限制实例化，响应适配不保存请求或用户状态。
     */
    private WebResponses() {
    }

    /**
     * 保留状态码、消息和所有附加键，递归将每个值中的领域 BO 转换为 VO。
     *
     * @param source Controller 构造的标准响应
     * @return 独立的 Web 响应，不修改传入 Map
     */
    public static AjaxResult toWeb(AjaxResult source) {
        if (source == null) {
            return null;
        }
        AjaxResult result = new AjaxResult();
        source.forEach((key, value) -> result.put(key, convertValue(value)));
        return result;
    }

    /**
     * 保留分页状态、消息及数据库总数，仅转换 rows 中的领域对象。
     *
     * @param source 由 Controller 封装分页元数据的响应
     * @return 分页语义不变的 Web 响应
     */
    public static TableDataInfo toWeb(TableDataInfo source) {
        if (source == null) {
            return null;
        }
        TableDataInfo result = new TableDataInfo();
        result.setCode(source.getCode());
        result.setMsg(source.getMsg());
        result.setTotal(source.getTotal());
        result.setRows((List<?>) convertValue(source.getRows()));
        return result;
    }

    /**
     * 将应用编排的组合数据放入若依响应顶层，在 Web 边界统一补充状态与消息并转换 BO。
     *
     * @param fields 业务组合数据，如 user、roles、menus；不含 HTTP 状态或消息
     * @return 保留原顶层字段结构的 Web 响应
     */
    public static AjaxResult withFields(Map<String, Object> fields) {
        AjaxResult result = AjaxResult.success();
        fields.forEach((key, value) -> result.put(key, convertValue(value)));
        return result;
    }

    /**
     * 按明确 BO 类型使用 MapStruct 转换，再递归处理动态 Map 和列表；基础类型及基础设施 DTO 原样保留。
     * 不使用反射、BeanUtils 或 JSON 序列化进行业务对象复制。
     *
     * @param value 响应中的单个值
     * @return Web 可序列化的对应值
     */
    private static Object convertValue(Object value) {
        if (value instanceof NoticeReaderBO) {
            return WebBoundaryConvert.INSTANCE.toNoticeReaderVO((NoticeReaderBO) value);
        }
        if (value instanceof SysDeptBO) {
            return WebBoundaryConvert.INSTANCE.toSysDeptVO((SysDeptBO) value);
        }
        if (value instanceof SysDictDataBO) {
            return WebBoundaryConvert.INSTANCE.toSysDictDataVO((SysDictDataBO) value);
        }
        if (value instanceof SysDictTypeBO) {
            return WebBoundaryConvert.INSTANCE.toSysDictTypeVO((SysDictTypeBO) value);
        }
        if (value instanceof SysMenuBO) {
            return WebBoundaryConvert.INSTANCE.toSysMenuVO((SysMenuBO) value);
        }
        if (value instanceof SysRoleBO) {
            return WebBoundaryConvert.INSTANCE.toSysRoleVO((SysRoleBO) value);
        }
        if (value instanceof SysUserBO) {
            return WebBoundaryConvert.INSTANCE.toSysUserVO((SysUserBO) value);
        }
        if (value instanceof SysJobBO) {
            return WebBoundaryConvert.INSTANCE.toSysJobVO((SysJobBO) value);
        }
        if (value instanceof SysJobLogBO) {
            return WebBoundaryConvert.INSTANCE.toSysJobLogVO((SysJobLogBO) value);
        }
        if (value instanceof SysConfigBO) {
            return WebBoundaryConvert.INSTANCE.toSysConfigVO((SysConfigBO) value);
        }
        if (value instanceof SysLogininforBO) {
            return WebBoundaryConvert.INSTANCE.toSysLogininforVO((SysLogininforBO) value);
        }
        if (value instanceof SysNoticeBO) {
            return WebBoundaryConvert.INSTANCE.toSysNoticeVO((SysNoticeBO) value);
        }
        if (value instanceof SysOperLogBO) {
            return WebBoundaryConvert.INSTANCE.toSysOperLogVO((SysOperLogBO) value);
        }
        if (value instanceof RouterMetaBO) {
            return WebBoundaryConvert.INSTANCE.toRouterMetaVO((RouterMetaBO) value);
        }
        if (value instanceof RouterBO) {
            return WebBoundaryConvert.INSTANCE.toRouterVO((RouterBO) value);
        }
        if (value instanceof Map<?, ?>) {
            Map<Object, Object> result = new LinkedHashMap<Object, Object>();
            ((Map<?, ?>) value).forEach((key, child) -> result.put(key, convertValue(child)));
            return result;
        }
        if (value instanceof List<?>) {
            List<Object> result = new ArrayList<Object>();
            for (Object child : (List<?>) value) {
                result.add(convertValue(child));
            }
            return result;
        }
        return value;
    }
}
