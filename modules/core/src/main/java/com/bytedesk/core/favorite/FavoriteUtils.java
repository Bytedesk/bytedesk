/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-07-12 11:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-12 11:40:41
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.favorite;

import com.bytedesk.core.message.MessageTypeEnum;

/**
 * 收藏工具类
 */
public class FavoriteUtils {

    private FavoriteUtils() {
        // 私有构造函数，防止实例化
    }

    /**
     * 根据消息类型生成收藏名称
     * 
     * @param messageType 消息类型
     * @param content 消息内容
     * @return 收藏名称
     */
    public static String generateFavoriteName(String messageType, String content) {
        if (content == null || content.isEmpty()) {
            return "收藏";
        }

        switch (messageType) {
            case "TEXT":
                // 文本消息，取前50个字符作为名称
                return content.length() > 50 ? content.substring(0, 50) + "..." : content;
            case "IMAGE":
                return "图片消息";
            case "FILE":
                return "文件消息";
            case "AUDIO":
            case "VOICE":
                return "语音消息";
            case "VIDEO":
                return "视频消息";
            case "LOCATION":
                return "位置消息";
            case "LINK":
                return "链接消息";
            default:
                return "收藏";
        }
    }

    /**
     * 验证消息类型是否支持收藏
     * 
     * @param messageType 消息类型
     * @return 是否支持收藏
     */
    public static boolean isMessageTypeSupported(String messageType) {
        return MessageTypeEnum.TEXT.name().equals(messageType) ||
               MessageTypeEnum.IMAGE.name().equals(messageType) ||
               MessageTypeEnum.FILE.name().equals(messageType) ||
               MessageTypeEnum.AUDIO.name().equals(messageType) ||
               MessageTypeEnum.VOICE.name().equals(messageType) ||
               MessageTypeEnum.VIDEO.name().equals(messageType) ||
               MessageTypeEnum.MUSIC.name().equals(messageType) ||
               MessageTypeEnum.LOCATION.name().equals(messageType) ||
               MessageTypeEnum.LINK.name().equals(messageType) ||
               MessageTypeEnum.CARD.name().equals(messageType);
    }

    /**
     * 获取消息类型的显示名称
     * 
     * @param messageType 消息类型
     * @return 显示名称
     */
    public static String getMessageTypeDisplayName(String messageType) {
        switch (messageType) {
            case "TEXT":
                return "文本";
            case "IMAGE":
                return "图片";
            case "FILE":
                return "文件";
            case "AUDIO":
            case "VOICE":
                return "语音";
            case "VIDEO":
                return "视频";
            case "MUSIC":
                return "音乐";
            case "LOCATION":
                return "位置";
            case "LINK":
                return "链接";
            case "CARD":
                return "卡片";
            default:
                return "未知";
        }
    }

    /**
     * 获取收藏类型的显示名称
     * 
     * @param favoriteType 收藏类型
     * @return 显示名称
     */
    public static String getFavoriteTypeDisplayName(String favoriteType) {
        switch (favoriteType) {
            case "THREAD":
                return "会话";
            case "CUSTOMER":
                return "客户";
            case "MESSAGE":
                return "消息";
            default:
                return "未知";
        }
    }

    /**
     * 获取收藏来源的显示名称
     * 
     * @param favoriteSource 收藏来源
     * @return 显示名称
     */
    public static String getFavoriteSourceDisplayName(String favoriteSource) {
        switch (favoriteSource) {
            case "MANUAL":
                return "手动收藏";
            case "AUTO":
                return "自动收藏";
            case "IMPORT":
                return "导入收藏";
            case "SHARE":
                return "分享收藏";
            case "FORWARD":
                return "转发收藏";
            default:
                return "未知";
        }
    }

    /**
     * 验证收藏请求参数
     * 
     * @param name 收藏名称
     * @param favoriteType 收藏类型
     * @param content 收藏内容
     * @return 验证结果
     */
    public static boolean validateFavoriteRequest(String name, String favoriteType, String content) {
        // 名称不能为空
        if (name == null || name.trim().isEmpty()) {
            return false;
        }

        // 收藏类型必须有效
        if (favoriteType == null || favoriteType.trim().isEmpty()) {
            return false;
        }

        // 如果是消息收藏，内容不能为空
        if ("MESSAGE".equals(favoriteType) && (content == null || content.trim().isEmpty())) {
            return false;
        }

        return true;
    }
} 