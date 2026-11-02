package com.ruoyi.architecture;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.common.utils.file.ImageUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 验证移除上传工具后，Excel 图片提取仍能写入导入目录并供导出读取。
 */
class LocalImportImageTest {
    /**
     * 将图片写入隔离临时目录，检查资源地址、磁盘内容及图片读取结果，最后恢复全局路径配置。
     *
     * @param directory JUnit 管理的临时目录，不访问业务文件
     * @throws Exception 图片写入或读取失败
     */
    @Test
    void preservesExcelImageStorageAndReading(@TempDir Path directory) throws Exception {
        RuoYiConfig config = new RuoYiConfig();
        String previousProfile = RuoYiConfig.getProfile();
        byte[] image = Base64.getDecoder().decode(
                "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMCAO+aObkAAAAASUVORK5CYII=");
        try {
            config.setProfile(directory.toString());
            String resource = FileUtils.writeImportBytes(image);
            assertTrue(resource.matches("/profile/import/\\d{4}/\\d{2}/\\d{2}/[^/]+\\.png"), resource);
            Path file = directory.resolve(FileUtils.stripPrefix(resource).substring(1)).normalize();
            assertTrue(file.startsWith(directory));
            assertArrayEquals(image, Files.readAllBytes(file));
            assertArrayEquals(image, ImageUtils.getImage(resource));
        } finally {
            config.setProfile(previousProfile);
        }
    }
}
