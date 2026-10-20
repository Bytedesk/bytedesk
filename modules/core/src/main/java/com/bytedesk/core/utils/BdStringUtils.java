package com.bytedesk.core.utils;

import java.nio.charset.StandardCharsets;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 字符串工具类
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BdStringUtils {

    private static final byte[] EMPTY_BYTES = new byte[0];

    /**
     * 按 UTF-8 字节长度截断字符串，不会截断多字节字符（避免产生乱码）。
     * 数据库 TEXT 等列的长度上限按字节计，中文等多字节字符按字符数截断时可能超限。
     *
     * @param text      原始字符串
     * @param maxBytes 最大 UTF-8 字节数；小于等于 0 时返回空字符串
     * @return 截断后的字符串，长度不超过 maxBytes
     */
    public static String truncateToUtf8Bytes(String text, int maxBytes) {
        if (text == null || text.isEmpty() || maxBytes <= 0) {
            return "";
        }
        if (text.getBytes(StandardCharsets.UTF_8).length <= maxBytes) {
            return text;
        }

        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        int end = Math.min(maxBytes, bytes.length);
        // 回退到多字节字符的起始字节，保证不产生半个字符
        while (end > 0 && (bytes[end] & 0xC0) == 0x80) {
            end--;
        }
        return new String(bytes, 0, end, StandardCharsets.UTF_8);
    }

    /**
     * 计算 UTF-8 编码后的字节长度
     */
    public static int utf8ByteLength(String text) {
        return text == null ? 0 : text.getBytes(StandardCharsets.UTF_8).length;
    }

    /**
     * 返回字符串的 UTF-8 字节表示；null 安全
     */
    public static byte[] utf8Bytes(String text) {
        return text == null ? EMPTY_BYTES : text.getBytes(StandardCharsets.UTF_8);
    }
}
