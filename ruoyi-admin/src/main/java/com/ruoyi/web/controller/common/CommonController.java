package com.ruoyi.web.controller.common;

import lombok.extern.slf4j.Slf4j;
import com.ruoyi.common.utils.file.FileUtils;
import org.springframework.http.MediaType;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.biz.common.CommonBiz;

/**
 * HTTP 协议入口；业务调用只通过 Biz，保留既有权限和审计注解。
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {
    private final CommonBiz biz;

    /**
     * 通过构造器接收应用入口，不在控制器内访问领域服务或数据库。
     *
     * @param biz 应用业务入口
     */
    public CommonController(CommonBiz biz) {
        this.biz = biz;
    }

    /**
     * 接收请求并保留原参数校验，调用应用层业务入口，再转换并返回原协议的响应。
     *
     * @param fileName fileName 请求参数或领域数据
     * @param delete   delete 请求参数或领域数据
     * @param response response 请求参数或领域数据
     * @param request  request 请求参数或领域数据
     */
    @GetMapping("/download")
    public void fileDownload(String fileName, Boolean delete, HttpServletResponse response, HttpServletRequest request) {
        try {
            CommonBiz.DownloadFile file = biz.fileDownload(fileName);
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            FileUtils.setAttachmentResponseHeader(response, file.name());
            FileUtils.writeBytes(file.path(), response.getOutputStream());
            if (delete) {
                FileUtils.deleteFile(file.path());
            }
        } catch (Exception e) {
            log.error("下载文件失败", e);
        }
    }

    /**
     * 接收请求并保留原参数校验，调用应用层业务入口，再转换并返回原协议的响应。
     *
     * @param resource resource 请求参数或领域数据
     * @param request  request 请求参数或领域数据
     * @param response response 请求参数或领域数据
     * @throws Exception 原处理过程声明的失败
     */
    @GetMapping("/download/resource")
    public void resourceDownload(String resource, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        try {
            CommonBiz.DownloadFile file = biz.resourceDownload(resource);
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            FileUtils.setAttachmentResponseHeader(response, file.name());
            FileUtils.writeBytes(file.path(), response.getOutputStream());
        } catch (Exception e) {
            log.error("下载文件失败", e);
        }
    }
}
