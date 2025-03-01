package com.bytedesk.core.upload;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.upload.storage.UploadStorageException;

public class UploadUtils {
    
    private UploadUtils() {}

    private static final BytedeskProperties properties = BytedeskProperties.getInstance();

    private static Path uploadDir = Paths.get(properties.getUploadDir());

    // 静态方法：给出图片URL地址，将其保存到本地
    public static String storeFromUrl(String url, String fileName) {
        // 根据当前日期创建文件夹，格式如：2021/03/15
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String currentDateFolder = LocalDate.now().format(formatter);

        try {
            // 构建包含日期文件夹的文件路径
            Path dateFolderPath = uploadDir.resolve(currentDateFolder);
            Files.createDirectories(dateFolderPath); // 创建日期文件夹（如果不存在）

            Path destinationFile = dateFolderPath.resolve(fileName).normalize().toAbsolutePath();

            if (!destinationFile.getParent().equals(dateFolderPath.toAbsolutePath())) {
                // 这是一个安全检查
                throw new UploadStorageException("Cannot store file outside current directory.");
            }

            // 下载并保存图片
            URL imageUrl = new URL(url);
            try (InputStream inputStream = imageUrl.openStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            // return destinationFile.toString();
            String uploadPath = currentDateFolder + "/" + fileName;
			String fileUrl = String.format("%s/file/%s", properties.getUploadUrl(), uploadPath);
			return fileUrl;
        } catch (IOException e) {
            throw new UploadStorageException("Failed to store file from URL.", e);
        }
    }
}
