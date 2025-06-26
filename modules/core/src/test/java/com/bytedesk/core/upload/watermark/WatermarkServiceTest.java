/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-27 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-26 21:00:43
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.upload.watermark;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

/**
 * 水印服务测试类
 */
class WatermarkServiceTest {

    private WatermarkService watermarkService;
    private BufferedImage testImage;

    @BeforeEach
    void setUp() {
        watermarkService = new WatermarkService();
        
        // 创建一个测试图片
        testImage = new BufferedImage(400, 300, BufferedImage.TYPE_INT_RGB);
        // 填充白色背景
        for (int x = 0; x < 400; x++) {
            for (int y = 0; y < 300; y++) {
                testImage.setRGB(x, y, Color.WHITE.getRGB());
            }
        }
    }

    @Test
    void testAddTextWatermark() {
        // 测试添加文字水印
        byte[] watermarkedImageBytes = watermarkService.addTextWatermark(
            testImage, 
            "Test Watermark", 
            WatermarkService.WatermarkPosition.BOTTOM_RIGHT
        );

        assertNotNull(watermarkedImageBytes);
        assertTrue(watermarkedImageBytes.length > 0);

        // 验证生成的图片可以正常读取
        try {
            BufferedImage watermarkedImage = ImageIO.read(new ByteArrayInputStream(watermarkedImageBytes));
            assertNotNull(watermarkedImage);
            assertEquals(400, watermarkedImage.getWidth());
            assertEquals(300, watermarkedImage.getHeight());
        } catch (IOException e) {
            fail("无法读取水印图片: " + e.getMessage());
        }
    }

    @Test
    void testAddTextWatermarkWithCustomParameters() {
        // 测试自定义参数的文字水印
        byte[] watermarkedImageBytes = watermarkService.addTextWatermark(
            testImage, 
            "Custom Watermark", 
            WatermarkService.WatermarkPosition.CENTER,
            32,  // 字体大小
            new Color(255, 0, 0, 128)  // 红色半透明
        );

        assertNotNull(watermarkedImageBytes);
        assertTrue(watermarkedImageBytes.length > 0);
    }

    @Test
    void testAddImageWatermark() {
        // 创建一个水印图片
        BufferedImage watermarkImage = new BufferedImage(50, 30, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < 50; x++) {
            for (int y = 0; y < 30; y++) {
                watermarkImage.setRGB(x, y, new Color(0, 0, 255, 128).getRGB());
            }
        }

        // 测试添加图片水印
        byte[] watermarkedImageBytes = watermarkService.addImageWatermark(
            testImage, 
            watermarkImage, 
            WatermarkService.WatermarkPosition.TOP_LEFT, 
            0.5f
        );

        assertNotNull(watermarkedImageBytes);
        assertTrue(watermarkedImageBytes.length > 0);
    }

    @Test
    void testIsImageFile() {
        // 测试图片文件检测
        MockMultipartFile imageFile = new MockMultipartFile(
            "file", 
            "test.jpg", 
            "image/jpeg", 
            "fake image content".getBytes()
        );

        assertTrue(watermarkService.isImageFile(imageFile));

        // 测试非图片文件
        MockMultipartFile textFile = new MockMultipartFile(
            "file", 
            "test.txt", 
            "text/plain", 
            "text content".getBytes()
        );

        assertFalse(watermarkService.isImageFile(textFile));

        // 测试空文件
        MockMultipartFile emptyFile = new MockMultipartFile(
            "file", 
            "test.jpg", 
            "image/jpeg", 
            new byte[0]
        );

        assertFalse(watermarkService.isImageFile(emptyFile));
    }

    @Test
    void testWatermarkPositions() {
        // 测试所有水印位置
        WatermarkService.WatermarkPosition[] positions = {
            WatermarkService.WatermarkPosition.TOP_LEFT,
            WatermarkService.WatermarkPosition.TOP_RIGHT,
            WatermarkService.WatermarkPosition.BOTTOM_LEFT,
            WatermarkService.WatermarkPosition.BOTTOM_RIGHT,
            WatermarkService.WatermarkPosition.CENTER
        };

        for (WatermarkService.WatermarkPosition position : positions) {
            byte[] watermarkedImageBytes = watermarkService.addTextWatermark(
                testImage, 
                "Position Test", 
                position
            );

            assertNotNull(watermarkedImageBytes);
            assertTrue(watermarkedImageBytes.length > 0);
        }
    }

    @Test
    void testNullImage() {
        // 测试空图片处理
        assertThrows(RuntimeException.class, () -> {
            watermarkService.addTextWatermark(
                null, 
                "Test", 
                WatermarkService.WatermarkPosition.BOTTOM_RIGHT
            );
        });
    }

    @Test
    void testEmptyWatermarkText() {
        // 测试空水印文字
        byte[] watermarkedImageBytes = watermarkService.addTextWatermark(
            testImage, 
            "", 
            WatermarkService.WatermarkPosition.BOTTOM_RIGHT
        );

        assertNotNull(watermarkedImageBytes);
        assertTrue(watermarkedImageBytes.length > 0);
    }
} 