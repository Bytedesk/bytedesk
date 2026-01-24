/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-01-23
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-01-23
 * @Description: File chunking strategy
 */
package com.bytedesk.kbase.llm_file;

public enum FileChunkingStrategyEnum {
    TOKEN,
    CHARACTER,
    PARAGRAPH;

    public static FileChunkingStrategyEnum fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return TOKEN;
        }
        try {
            return FileChunkingStrategyEnum.valueOf(value.trim().toUpperCase());
        } catch (Exception e) {
            return TOKEN;
        }
    }
}
