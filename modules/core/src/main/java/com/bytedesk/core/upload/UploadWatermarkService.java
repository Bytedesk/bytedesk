package com.bytedesk.core.upload;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bytedesk.core.upload.watermark.WatermarkConfig;
import com.bytedesk.core.upload.watermark.WatermarkService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 上传水印服务
 * 负责处理文件上传时的水印添加逻辑
 */
@Slf4j
@Service
@AllArgsConstructor
public class UploadWatermarkService {

    private final WatermarkService watermarkService;
    private final WatermarkConfig watermarkConfig;

    /**
     * 判断是否需要添加水印（支持客户端控制）
     */
    public boolean shouldAddWatermark(MultipartFile file, UploadRequest request) {
        // 如果客户端明确指定不添加水印
        if (request != null && request.getAddWatermark() != null && !request.getAddWatermark()) {
            return false;
        }

        // 检查水印功能是否启用
        if (!watermarkConfig.isEnabled()) {
            return false;
        }

        // 检查是否只对图片文件添加水印
        if (watermarkConfig.isImageOnly() && !watermarkService.isImageFile(file)) {
            return false;
        }

        // 检查图片尺寸
        try (InputStream inputStream = file.getInputStream()) {
            BufferedImage image = ImageIO.read(inputStream);
            if (image != null) {
                int width = image.getWidth();
                int height = image.getHeight();
                
                // 检查最小尺寸
                if (width < watermarkConfig.getMinImageSize() || height < watermarkConfig.getMinImageSize()) {
                    log.debug("图片尺寸太小，不添加水印: {}x{}", width, height);
                    return false;
                }
                
                // 检查最大尺寸
                if (width > watermarkConfig.getMaxImageSize() || height > watermarkConfig.getMaxImageSize()) {
                    log.debug("图片尺寸太大，不添加水印: {}x{}", width, height);
                    return false;
                }
            }
        } catch (IOException e) {
            log.warn("无法读取图片尺寸，跳过水印检查: {}", file.getOriginalFilename(), e);
            return false;
        }

        return true;
    }

    /**
     * 为文件添加水印（支持自定义参数）
     */
    public void addWatermarkToFile(MultipartFile file, Path destinationPath, UploadRequest request) throws IOException {
        try {
            // 读取原始图片
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            if (originalImage == null) {
                log.error("无法读取图片文件: {}", file.getOriginalFilename());
                // 如果无法读取图片，直接保存原文件
                try (InputStream inputStream = file.getInputStream()) {
                    Files.copy(inputStream, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                }
                return;
            }

            // 获取水印参数
            String watermarkText = getWatermarkText(request);
            WatermarkService.WatermarkPosition position = getWatermarkPosition(request);
            int fontSize = getWatermarkFontSize(request);
            Color watermarkColor = getWatermarkColor(request);

            // 添加水印
            byte[] watermarkedImageBytes = watermarkService.addTextWatermark(
                originalImage, 
                watermarkText, 
                position,
                fontSize,
                watermarkColor
            );

            // 保存到文件
            try (InputStream inputStream = new ByteArrayInputStream(watermarkedImageBytes)) {
                Files.copy(inputStream, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            }

            log.info("成功为图片添加水印: {}", file.getOriginalFilename());

        } catch (Exception e) {
            log.error("添加水印失败，保存原文件: {}", file.getOriginalFilename(), e);
            // 如果添加水印失败，保存原文件
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    /**
     * 获取水印文字
     */
    public String getWatermarkText(UploadRequest request) {
        if (request != null && request.getWatermarkText() != null && !request.getWatermarkText().trim().isEmpty()) {
            return request.getWatermarkText();
        }
        return watermarkConfig.getText();
    }

    /**
     * 获取水印位置
     */
    public WatermarkService.WatermarkPosition getWatermarkPosition(UploadRequest request) {
        if (request != null && request.getWatermarkPosition() != null && !request.getWatermarkPosition().trim().isEmpty()) {
            try {
                return WatermarkService.WatermarkPosition.valueOf(request.getWatermarkPosition().toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("无效的水印位置: {}, 使用默认位置", request.getWatermarkPosition());
            }
        }
        return watermarkConfig.getPosition();
    }

    /**
     * 获取水印字体大小
     */
    public int getWatermarkFontSize(UploadRequest request) {
        if (request != null && request.getWatermarkFontSize() != null && request.getWatermarkFontSize() > 0) {
            return request.getWatermarkFontSize();
        }
        return watermarkConfig.getFontSize();
    }

    /**
     * 获取水印颜色
     */
    public Color getWatermarkColor(UploadRequest request) {
        if (request != null && request.getWatermarkColor() != null && !request.getWatermarkColor().trim().isEmpty()) {
            return parseColor(request.getWatermarkColor());
        }
        return parseColor(watermarkConfig.getColor());
    }

    /**
     * 解析颜色字符串
     */
    public Color parseColor(String colorStr) {
        try {
            String[] parts = colorStr.split(",");
            if (parts.length == 4) {
                int r = Integer.parseInt(parts[0].trim());
                int g = Integer.parseInt(parts[1].trim());
                int b = Integer.parseInt(parts[2].trim());
                int a = Integer.parseInt(parts[3].trim());
                return new Color(r, g, b, a);
            } else if (parts.length == 3) {
                int r = Integer.parseInt(parts[0].trim());
                int g = Integer.parseInt(parts[1].trim());
                int b = Integer.parseInt(parts[2].trim());
                return new Color(r, g, b, 128); // 默认透明度
            }
        } catch (Exception e) {
            log.warn("无法解析颜色配置: {}, 使用默认颜色", colorStr, e);
        }
        return new Color(255, 255, 255, 128); // 默认白色半透明
    }
}
