/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-22 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-16 11:57:49
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.util.StringUtils;

/**
 * 转人工关键词检测工具类
 */
public class TransferKeywordUtil {

    /**
     * 转人工相关关键词列表
     */
    private static final List<String> TRANSFER_KEYWORDS = Arrays.asList(
            "转人工",
            "人工客服",
            "真人客服",
            "人工服务",
            "真人服务",
            "转接人工",
            "转接客服",
            "真人",
            "人工",
            "客服",
            "转接",
            "转人",
            "人工解答",
            "找人工",
            "接通人工",
            "需要人工",
            "联系人工",
            "人工帮助",
            "不想机器人",
            "机器人没用",
            "换人工",
            "换客服",
            "真人咨询",
            "真实客服",
            "真人解答",
            "需要真人",
            "人工处理",
            "专员服务");

    /**
     * 返回默认的转人工关键词副本，供配置界面或实体初始化使用。
     *
     * @return 新的关键词列表，调用方可自由修改
     */
    public static List<String> getDefaultKeywords() {
        return new ArrayList<>(TRANSFER_KEYWORDS);
    }

    /**
     * 检查文本是否包含转人工关键词
     * 
     * @param text 待检查的文本内容
     * @return 如果包含关键词返回true，否则返回false
     */
    public static boolean containsTransferKeyword(String text) {
        if (!StringUtils.hasText(text)) {
            return false;
        }

        String lowerText = text.toLowerCase();
        for (String keyword : TRANSFER_KEYWORDS) {
            if (lowerText.contains(keyword)) {
                return true;
            }
        }

        return false;
    }
}
