/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-27 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-27 22:37:12
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.upload.watermark;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import javax.imageio.ImageIO;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

/**
 * 图片水印服务
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "bytedesk.watermark.enabled", havingValue = "true", matchIfMissing = false)
public class WatermarkService {

    /**
     * 默认水印文本
     */
    // private static final String DEFAULT_WATERMARK_TEXT = "bytedesk.com";

    /**
     * 默认水印字体
     */
    private static final String DEFAULT_FONT_NAME = "Arial";

    /**
     * 默认水印字体大小
     */
    private static final int DEFAULT_FONT_SIZE = 24;

    /**
     * 默认水印颜色
     */
    private static final Color DEFAULT_WATERMARK_COLOR = new Color(255, 255, 255, 128);

    /**
     * 默认水印位置
     */
    // private static final WatermarkPosition DEFAULT_POSITION = WatermarkPosition.BOTTOM_RIGHT;

    /**
     * 给图片添加文字水印
     * 
     * @param originalImage 原始图片
     * @param watermarkText 水印文字
     * @param position 水印位置
     * @return 添加水印后的图片字节数组
     */
    public byte[] addTextWatermark(BufferedImage originalImage, String watermarkText, WatermarkPosition position) {
        return addTextWatermark(originalImage, watermarkText, position, DEFAULT_FONT_SIZE, DEFAULT_WATERMARK_COLOR);
    }

    /**
     * 给图片添加文字水印
     * 
     * @param originalImage 原始图片
     * @param watermarkText 水印文字
     * @param position 水印位置
     * @param fontSize 字体大小
     * @param color 水印颜色
     * @return 添加水印后的图片字节数组
     */
    public byte[] addTextWatermark(BufferedImage originalImage, String watermarkText, WatermarkPosition position, 
                                   int fontSize, Color color) {
        try {
            // 创建新的图片，支持透明度
            BufferedImage watermarkedImage = new BufferedImage(
                originalImage.getWidth(), 
                originalImage.getHeight(), 
                BufferedImage.TYPE_INT_ARGB
            );

            // 绘制原始图片
            Graphics2D g2d = watermarkedImage.createGraphics();
            g2d.drawImage(originalImage, 0, 0, null);

            // 设置水印字体
            Font font = new Font(DEFAULT_FONT_NAME, Font.BOLD, fontSize);
            g2d.setFont(font);
            g2d.setColor(color);

            // 设置抗锯齿
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // 计算水印位置
            FontMetrics fontMetrics = g2d.getFontMetrics();
            int textWidth = fontMetrics.stringWidth(watermarkText);
            int textHeight = fontMetrics.getHeight();

            int x, y;
            int margin = 20; // 边距

            switch (position) {
                case TOP_LEFT:
                    x = margin;
                    y = margin + textHeight;
                    break;
                case TOP_RIGHT:
                    x = originalImage.getWidth() - textWidth - margin;
                    y = margin + textHeight;
                    break;
                case BOTTOM_LEFT:
                    x = margin;
                    y = originalImage.getHeight() - margin;
                    break;
                case BOTTOM_RIGHT:
                    x = originalImage.getWidth() - textWidth - margin;
                    y = originalImage.getHeight() - margin;
                    break;
                case CENTER:
                    x = (originalImage.getWidth() - textWidth) / 2;
                    y = (originalImage.getHeight() + textHeight) / 2;
                    break;
                default:
                    x = originalImage.getWidth() - textWidth - margin;
                    y = originalImage.getHeight() - margin;
            }

            // 绘制水印文字
            g2d.drawString(watermarkText, x, y);
            g2d.dispose();

            // 转换为字节数组
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(watermarkedImage, "png", baos);
            return baos.toByteArray();

        } catch (IOException e) {
            log.error("添加文字水印失败", e);
            throw new RuntimeException("添加文字水印失败", e);
        }
    }

    /**
     * 给图片添加图片水印
     * 
     * @param originalImage 原始图片
     * @param watermarkImage 水印图片
     * @param position 水印位置
     * @param opacity 透明度 (0.0-1.0)
     * @return 添加水印后的图片字节数组
     */
    public byte[] addImageWatermark(BufferedImage originalImage, BufferedImage watermarkImage, 
                                    WatermarkPosition position, float opacity) {
        try {
            // 创建新的图片，支持透明度
            BufferedImage watermarkedImage = new BufferedImage(
                originalImage.getWidth(), 
                originalImage.getHeight(), 
                BufferedImage.TYPE_INT_ARGB
            );

            // 绘制原始图片
            Graphics2D g2d = watermarkedImage.createGraphics();
            g2d.drawImage(originalImage, 0, 0, null);

            // 设置透明度
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));

            // 计算水印位置
            int x, y;
            int margin = 20; // 边距

            switch (position) {
                case TOP_LEFT:
                    x = margin;
                    y = margin;
                    break;
                case TOP_RIGHT:
                    x = originalImage.getWidth() - watermarkImage.getWidth() - margin;
                    y = margin;
                    break;
                case BOTTOM_LEFT:
                    x = margin;
                    y = originalImage.getHeight() - watermarkImage.getHeight() - margin;
                    break;
                case BOTTOM_RIGHT:
                    x = originalImage.getWidth() - watermarkImage.getWidth() - margin;
                    y = originalImage.getHeight() - watermarkImage.getHeight() - margin;
                    break;
                case CENTER:
                    x = (originalImage.getWidth() - watermarkImage.getWidth()) / 2;
                    y = (originalImage.getHeight() - watermarkImage.getHeight()) / 2;
                    break;
                default:
                    x = originalImage.getWidth() - watermarkImage.getWidth() - margin;
                    y = originalImage.getHeight() - watermarkImage.getHeight() - margin;
            }

            // 绘制水印图片
            g2d.drawImage(watermarkImage, x, y, null);
            g2d.dispose();

            // 转换为字节数组
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(watermarkedImage, "png", baos);
            return baos.toByteArray();

        } catch (IOException e) {
            log.error("添加图片水印失败", e);
            throw new RuntimeException("添加图片水印失败", e);
        }
    }

    /**
     * 给MultipartFile添加文字水印并保存到指定路径
     * 
     * @param file 原始文件
     * @param destinationPath 目标路径
     * @param watermarkText 水印文字
     * @param position 水印位置
     * @return 是否成功
     */
    public boolean addTextWatermarkToFile(MultipartFile file, Path destinationPath, String watermarkText, 
                                          WatermarkPosition position) {
        try {
            // 读取原始图片
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            if (originalImage == null) {
                log.error("无法读取图片文件: {}", file.getOriginalFilename());
                return false;
            }

            // 添加水印
            byte[] watermarkedImageBytes = addTextWatermark(originalImage, watermarkText, position);

            // 保存到文件
            try (InputStream inputStream = new ByteArrayInputStream(watermarkedImageBytes)) {
                Files.copy(inputStream, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            }

            return true;
        } catch (IOException e) {
            log.error("给文件添加水印失败: {}", file.getOriginalFilename(), e);
            return false;
        }
    }

    /**
     * 检查文件是否为图片
     * 
     * @param file 文件
     * @return 是否为图片
     */
    public boolean isImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }

    /**
     * 水印位置枚举
     */
    public enum WatermarkPosition {
        TOP_LEFT,      // 左上角
        TOP_RIGHT,     // 右上角
        BOTTOM_LEFT,   // 左下角
        BOTTOM_RIGHT,  // 右下角
        CENTER         // 中心
    }
} 