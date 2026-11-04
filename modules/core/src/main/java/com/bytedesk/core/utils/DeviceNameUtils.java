package com.bytedesk.core.utils;

import org.springframework.util.StringUtils;

/**
 * 登录设备/机器名的清洗工具：
 * - 去除首尾空白与不可见控制字符；
 * - 长度截断，防止异常字符破坏展示与超长入库；
 * - 空值/清洗后为空时回退 "Unknown Device"。
 * 设备名仅用于展示，不作为身份、去重或授权依据。
 */
public class DeviceNameUtils {

    public static final String UNKNOWN_DEVICE = "Unknown Device";

    /**
     * 上限 64：device 会拼入 description（前缀 "login token for ... device=" 约 100+ 字符），
     * description 列为 varchar(255)，64 可保证两者均不超长。
     */
    public static final int MAX_LENGTH = 64;

    private DeviceNameUtils() {
    }

    public static String sanitize(String device) {
        if (!StringUtils.hasText(device)) {
            return UNKNOWN_DEVICE;
        }
        // 去除 ASCII 控制字符（0x00-0x1F、0x7F）与部分 Unicode 控制字符，
        // 保留常规可见字符（含中文、空格等）
        StringBuilder builder = new StringBuilder(device.length());
        for (int i = 0; i < device.length(); i++) {
            char c = device.charAt(i);
            if (c == '\n' || c == '\r' || c == '\t') {
                continue;
            }
            if (Character.isISOControl(c)) {
                continue;
            }
            // 跳过 Unicode 格式字符（零宽字符等不可见字符）
            if (Character.getType(c) == Character.FORMAT) {
                continue;
            }
            builder.append(c);
        }
        String cleaned = builder.toString().trim();
        if (cleaned.isEmpty()) {
            return UNKNOWN_DEVICE;
        }
        if (cleaned.length() > MAX_LENGTH) {
            cleaned = cleaned.substring(0, MAX_LENGTH).trim();
        }
        return cleaned.isEmpty() ? UNKNOWN_DEVICE : cleaned;
    }
}
