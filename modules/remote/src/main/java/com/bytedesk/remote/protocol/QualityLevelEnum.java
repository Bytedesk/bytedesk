/*
 * @Author: bytedesk.com
 * @Date: 2025-01-16
 * @Description: Quality Level Enumeration for Screen Streaming
 *   Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 */
package com.bytedesk.remote.protocol;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Screen Streaming Quality Level
 */
@Getter
@AllArgsConstructor
public enum QualityLevelEnum {

    /**
     * Low quality - optimized for slow networks
     * Resolution: 1280x720 max
     * JPEG Quality: 50
     * Target FPS: 15
     */
    LOW("low", "低质量", 50, 1280, 720, 15),

    /**
     * Medium quality - balanced performance
     * Resolution: 1920x1080 max
     * JPEG Quality: 70
     * Target FPS: 30
     */
    MEDIUM("medium", "中等质量", 70, 1920, 1080, 30),

    /**
     * High quality - best visual quality
     * Resolution: 2560x1440 max
     * JPEG Quality: 90
     * Target FPS: 30+
     */
    HIGH("high", "高质量", 90, 2560, 1440, 30),

    /**
     * Ultra quality - maximum quality
     * Resolution: Original resolution
     * JPEG Quality: 95
     * Target FPS: 60
     */
    ULTRA("ultra", "超高质量", 95, 3840, 2160, 60);

    private final String code;
    private final String description;

    /**
     * JPEG compression quality (0-100)
     */
    private final int jpegQuality;

    /**
     * Maximum width in pixels
     */
    private final int maxWidth;

    /**
     * Maximum height in pixels
     */
    private final int maxHeight;

    /**
     * Target frames per second
     */
    private final int targetFps;

    /**
     * Get enum by code
     */
    public static QualityLevelEnum fromCode(String code) {
        for (QualityLevelEnum quality : values()) {
            if (quality.code.equals(code)) {
                return quality;
            }
        }
        return MEDIUM; // Default to medium
    }

    /**
     * Get next quality level (for adaptive adjustment)
     */
    public QualityLevelEnum getNextLevel() {
        QualityLevelEnum[] levels = values();
        int currentIndex = this.ordinal();
        if (currentIndex < levels.length - 1) {
            return levels[currentIndex + 1];
        }
        return this;
    }

    /**
     * Get previous quality level (for adaptive adjustment)
     */
    public QualityLevelEnum getPreviousLevel() {
        QualityLevelEnum[] levels = values();
        int currentIndex = this.ordinal();
        if (currentIndex > 0) {
            return levels[currentIndex - 1];
        }
        return this;
    }
}
