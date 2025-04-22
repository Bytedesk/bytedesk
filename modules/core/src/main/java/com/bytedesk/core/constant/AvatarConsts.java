/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-22 13:07:05
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.constant;

import com.bytedesk.core.config.properties.BytedeskProperties;

/**
 * 头像常量, 可以去这里获取 https://www.iconfont.cn/
 *
 * @author bytedesk.com
 */
public class AvatarConsts {

    // EffectiveJava Item 22
    // Prevents instantiation
    private AvatarConsts() {}

    private static BytedeskProperties bytedeskProperties;
    private static final String FALLBACK_BASE_URL = "https://cdn.weiyuai.cn";

    private static String getDefaultHost() {
        try {
            if (bytedeskProperties == null) {
                bytedeskProperties = BytedeskProperties.getInstance();
            }
            String baseUrl = bytedeskProperties != null ? bytedeskProperties.getAvatarBaseUrl() : null;
            return baseUrl != null ? baseUrl : FALLBACK_BASE_URL;
        } catch (Exception e) {
            // If anything goes wrong, return the fallback URL
            return FALLBACK_BASE_URL;
        }
    }

    public static String getDefaultAvatarUrl() {
        return getDefaultHost() + "/avatars/admin.png";
    }

    public static String getDefaultAgentAvatarUrl() {
        return getDefaultHost() + "/avatars/agent.png";
    }

    public static String getDefaultVisitorAvatarUrl() {
        return getDefaultHost() + "/avatars/visitor_default_avatar.png";
    }

    public static String getDefaultUniappAvatarUrl() {
        return getDefaultHost() + "/avatars/uniapp.png";
    }

    public static String getDefaultUserAvatarUrl() {
        return getDefaultHost() + "/avatars/user.png";
    }

    public static String getDefaultWebAvatarUrl() {
        // return getDefaultHost() + "/avatars/chrome.png";
        return getDefaultHost() + "/avatars/visitor_default_avatar.png";
    }

    public static String getDefaultWapAvatarUrl() {
        // return getDefaultHost() + "/avatars/chrome.png";
        return getDefaultHost() + "/avatars/visitor_default_avatar.png";
    }

    public static String getDefaultWechatMpAvatarUrl() {
        return getDefaultHost() + "/avatars/wechat.png";
    }

    public static String getDefaultWechatMiniAvatarUrl() {
        return getDefaultHost() + "/avatars/wechat.png";
    }

    public static String getDefaultWechatKefuAvatarUrl() {
        return getDefaultHost() + "/avatars/wechat.png";
    }

    public static String getDefaultAndroidAvatarUrl() {
        return getDefaultHost() + "/avatars/android.png";
    }

    public static String getDefaultIosAvatarUrl() {
        return getDefaultHost() + "/avatars/apple.png";
    }

    public static String getDefaultFlutterAndroidAvatarUrl() {
        return getDefaultHost() + "/avatars/android.png";
    }

    public static String getDefaultFlutterIosAvatarUrl() {
        return getDefaultHost() + "/avatars/apple.png";
    }

    public static String getDefaultFlutterWebAvatarUrl() {
        return getDefaultHost() + "/avatars/chrome.png";
    }

    public static String getDefaultSystemAvatarUrl() {
        return getDefaultHost() + "/avatars/system_default_avatar.png";
    }

    public static String getDefaultWorkGroupAvatarUrl() {
        return getDefaultHost() + "/avatars/workgroup_default_avatar.png";
    }

    public static String getDefaultGroupAvatarUrl() {
        return getDefaultHost() + "/avatars/group.png";
    }

    public static String getDefaultFileAssistantAvatarUrl() {
        return getDefaultHost() + "/avatars/file.png";
    }

    public static String getDefaultClipboardAssistantAvatarUrl() {
        return getDefaultHost() + "/avatars/clipboard.png";
    }

    public static String getDefaultIntentClassificationAssistantAvatarUrl() {
        return getDefaultHost() + "/avatars/intent_classification.png";
    }

    public static String getDefaultIntentRewriteAssistantAvatarUrl() {
        return getDefaultHost() + "/avatars/intent_rewrite.png";
    }

    public static String getDefaultEmotionAssistantAvatarUrl() {
        return getDefaultHost() + "/avatars/emotion.png";
    }

    public static String getDefaultSystemNotificationAvatarUrl() {
        return getDefaultHost() + "/avatars/notification.png";
    }

    public static String getLlmThreadDefaultAvatar() {
        return getDefaultHost() + "/assets/images/llm/provider/ollama.png";
    }

    public static String getLlmThreadDefaultAvatarBaseUrl() {
        return getDefaultHost() + "/assets/images/llm/provider/";
    }

    public static String getDefaultRobotAvatar() {
        return getDefaultHost() + "/avatars/robot.png";
    }

    /**
     * https://cdn.weiyuai.cn/avatars/girl.png
     * https://cdn.weiyuai.cn/avatars/boy.png
     */
    

}
