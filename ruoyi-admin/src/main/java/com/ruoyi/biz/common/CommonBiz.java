package com.ruoyi.biz.common;

import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUtils;
import org.springframework.stereotype.Component;

/**
 * CommonController 对应的应用编排，保留原业务执行顺序与响应语义。
 */
@Component
public class CommonBiz {

    /**
     * 校验下载文件名并解析服务器文件路径和下载名称；非法名称抛出异常，不写 HTTP 响应或删除文件。
     *
     * @param fileName 待下载的服务器文件名
     * @return 校验通过的文件路径和下载名称
     * @throws Exception 文件名非法
     */
    public DownloadFile fileDownload(String fileName) throws Exception {
        if (!FileUtils.checkAllowDownload(fileName)) {
            throw new Exception(StringUtils.format("文件名称({})非法，不允许下载。 ", fileName));
        }
        String realFileName = System.currentTimeMillis() + fileName.substring(fileName.indexOf("_") + 1);
        String filePath = RuoYiConfig.getDownloadPath() + fileName;

        return new DownloadFile(filePath, realFileName);
    }

    /**
     * 校验资源名称并解析本地资源路径和下载名称；非法名称抛出异常，不写 HTTP 响应。
     *
     * @param resource 已授权访问的资源相对地址
     * @return 校验通过的文件路径和下载名称
     * @throws Exception 文件名非法
     */
    public DownloadFile resourceDownload(String resource) throws Exception {
        if (!FileUtils.checkAllowDownload(resource)) {
            throw new Exception(StringUtils.format("资源文件({})非法，不允许下载。 ", resource));
        }
        // 本地资源路径
        String localPath = RuoYiConfig.getProfile();
        // 数据库资源地址
        String downloadPath = localPath + FileUtils.stripPrefix(resource);
        // 下载名称
        String downloadName = StringUtils.substringAfterLast(downloadPath, "/");
        return new DownloadFile(downloadPath, downloadName);
    }

    /**
     * 校验通过的本地文件信息，仅携带路径和名称，不依赖 Servlet。
     */
    public record DownloadFile(String path, String name) {
    }
}
