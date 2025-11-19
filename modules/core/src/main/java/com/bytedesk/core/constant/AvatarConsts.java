/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-16 11:21:32
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
import com.bytedesk.core.enums.ChannelEnum;

/**
 * 头像常量, 可以去这里获取 https://www.iconfont.cn/
 *
 * @author bytedesk.com
 */
public class AvatarConsts {
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

    public static String getDefaultQueueAssistantAvatarUrl() {
        return getDefaultHost() + "/avatars/queue.png";
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

    // 社交媒体渠道头像
    public static String getDefaultXiaohongshuAvatarUrl() {
        return getDefaultHost() + "/avatars/xiaohongshu.png";
    }

    public static String getDefaultDouyinAvatarUrl() {
        return getDefaultHost() + "/avatars/douyin.png";
    }

    public static String getDefaultKuaishouAvatarUrl() {
        return getDefaultHost() + "/avatars/kuaishou.png";
    }

    public static String getDefaultBilibiliAvatarUrl() {
        return getDefaultHost() + "/avatars/bilibili.png";
    }

    public static String getDefaultWeiboAvatarUrl() {
        return getDefaultHost() + "/avatars/weibo.png";
    }

    // 电商渠道头像
    public static String getDefaultJingdongAvatarUrl() {
        return getDefaultHost() + "/avatars/jingdong.png";
    }

    public static String getDefaultPinduoduoAvatarUrl() {
        return getDefaultHost() + "/avatars/pinduoduo.png";
    }

    public static String getDefaultQianniuAvatarUrl() {
        return getDefaultHost() + "/avatars/qianniu.png";
    }

    public static String getDefaultShopifyAvatarUrl() {
        return getDefaultHost() + "/avatars/shopify.png";
    }

    public static String getDefaultLazadaAvatarUrl() {
        return getDefaultHost() + "/avatars/lazada.png";
    }

    // 海外即时通讯头像
    public static String getDefaultTelegramAvatarUrl() {
        return getDefaultHost() + "/avatars/telegram.png";
    }

    public static String getDefaultLineAvatarUrl() {
        return getDefaultHost() + "/avatars/line.png";
    }

    public static String getDefaultWhatsappAvatarUrl() {
        return getDefaultHost() + "/avatars/whatsapp.png";
    }

    public static String getDefaultMessengerAvatarUrl() {
        return getDefaultHost() + "/avatars/messenger.png";
    }

    public static String getDefaultInstagramAvatarUrl() {
        return getDefaultHost() + "/avatars/instagram.png";
    }

    // 其他平台头像
    public static String getDefaultWordpressAvatarUrl() {
        return getDefaultHost() + "/avatars/wordpress.png";
    }

    public static String getDefaultMagentoAvatarUrl() {
        return getDefaultHost() + "/avatars/magento.png";
    }

    public static String getDefaultWooCommerceAvatarUrl() {
        return getDefaultHost() + "/avatars/woocommerce.png";
    }

    public static String getDefaultOpenCartAvatarUrl() {
        return getDefaultHost() + "/avatars/opencart.png";
    }

    public static String getDefaultPrestaShopAvatarUrl() {
        return getDefaultHost() + "/avatars/prestashop.png";
    }

    public static String getDefaultDifyAvatarUrl() {
        return getDefaultHost() + "/avatars/dify.png";
    }

    public static String getDefaultCozeAvatarUrl() {
        return getDefaultHost() + "/avatars/coze.png";
    }

    public static String getDefaultNpmAvatarUrl() {
        return getDefaultHost() + "/avatars/npm.png";
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

    // workflow avatar  
    public static String getDefaultWorkflowAvatar() {
        return getDefaultHost() + "/avatars/workflow.png";
    }

    public static String getAvatar(String channel) {
        if (channel == null) {
            return AvatarConsts.getDefaultVisitorAvatarUrl();
        }
        
        try {
            ChannelEnum channelEnum = ChannelEnum.fromValue(channel.toUpperCase());
            return getAvatarByChannel(channelEnum);
        } catch (IllegalArgumentException e) {
            // 如果无法解析为枚举，使用字符串匹配作为后备方案
            return getAvatarByString(channel);
        }
    }
    
    /**
     * 根据渠道枚举获取头像
     */
    private static String getAvatarByChannel(ChannelEnum channel) {
        switch (channel) {
            // 系统相关
            case SYSTEM:
                return getDefaultHost() + "/avatars/system_default_avatar.png";
                
            // Web相关
            case WEB:
            case WEB_PC:
            case WEB_H5:
            case WEB_VISITOR:
            case WEB_FLOAT:
            case WEB_ADMIN:
                return getDefaultHost() + "/avatars/web.png";
                
            // 移动端
            case IOS:
                return getDefaultHost() + "/avatars/apple.png";
            case ANDROID:
                return getDefaultHost() + "/avatars/android.png";
                
            // 桌面端
            case ELECTRON:
            case LINUX:
            case MACOS:
            case WINDOWS:
                return getDefaultHost() + "/avatars/chrome.png";
                
            // Flutter相关
            case FLUTTER:
            case FLUTTER_WEB:
                return getDefaultHost() + "/avatars/chrome.png";
            case FLUTTER_ANDROID:
                return getDefaultHost() + "/avatars/android.png";
            case FLUTTER_IOS:
                return getDefaultHost() + "/avatars/apple.png";
            case FLUTTER_MACOS:
                return getDefaultHost() + "/avatars/apple.png";
            case FLUTTER_WINDOWS:
            case FLUTTER_LINUX:
                return getDefaultHost() + "/avatars/chrome.png";
                
            // UniApp相关
            case UNIAPP:
            case UNIAPP_WEB:
                return getDefaultHost() + "/avatars/uniapp_default_avatar.png";
            case UNIAPP_ANDROID:
                return getDefaultHost() + "/avatars/android.png";
            case UNIAPP_IOS:
                return getDefaultHost() + "/avatars/apple.png";
                
            // 微信相关
            case WECHAT:
            case WECHAT_MINI:
            case WECHAT_MP:
            case WECHAT_WORK:
            case WECHAT_KEFU:
            case WECHAT_CHANNEL:
                return getDefaultHost() + "/avatars/wechat.png";
                
            // 社交媒体渠道
            case XIAOHONGSHU:
                return getDefaultXiaohongshuAvatarUrl();
            case DOUYIN:
                return getDefaultDouyinAvatarUrl();
            case KUAISHOU:
                return getDefaultKuaishouAvatarUrl();
            case BILIBILI:
                return getDefaultBilibiliAvatarUrl();
            case WEIBO:
                return getDefaultWeiboAvatarUrl();
            case ZHIHU:
                return getDefaultHost() + "/avatars/baidu.png"; // 知乎暂时使用百度图标
            case TOUTIAO:
                return getDefaultHost() + "/avatars/baidu.png"; // 头条暂时使用百度图标
            case DOUBAN:
                return getDefaultHost() + "/avatars/baidu.png"; // 豆瓣暂时使用百度图标
                
            // 电商渠道
            case TAOBAO:
            case TMALL:
                return getDefaultQianniuAvatarUrl();
            case JD:
                return getDefaultJingdongAvatarUrl();
            case PINDUODUO:
                return getDefaultPinduoduoAvatarUrl();
            case MEITUAN:
            case ELEME:
            case DIANPING:
                return getDefaultHost() + "/avatars/baidu.png"; // 暂时使用百度图标
                
            // 企业渠道
            case DINGTALK:
            case FEISHU:
            case LARK:
                return getDefaultHost() + "/avatars/baidu.png"; // 暂时使用百度图标
            case CUSTOM:
                return getDefaultHost() + "/avatars/app.png";
                
            // 其他渠道
            case EMAIL:
                return getDefaultHost() + "/avatars/email.png";
            case SMS:
            case PHONE:
                return getDefaultHost() + "/avatars/app.png";
                
            // Meta相关
            case MESSENGER:
                return getDefaultMessengerAvatarUrl();
            case INSTAGRAM:
                return getDefaultInstagramAvatarUrl();
            case WHATSAPP:
                return getDefaultWhatsappAvatarUrl();
                
            // 海外社交媒体
            case TWITTER:
            case FACEBOOK:
            case LINKEDIN:
            case YOUTUBE:
            case TIKTOK:
            case PINTEREST:
            case REDDIT:
            case SNAPCHAT:
                return getDefaultHost() + "/avatars/web.png"; // 暂时使用web图标
                
            // 海外即时通讯
            case TELEGRAM:
                return getDefaultTelegramAvatarUrl();
            case LINE:
                return getDefaultLineAvatarUrl();
            case KAKAO:
            case VIBER:
            case SIGNAL:
            case DISCORD:
            case SLACK:
                return getDefaultHost() + "/avatars/web.png"; // 暂时使用web图标
                
            // 海外电商
            case AMAZON:
            case EBAY:
                return getDefaultHost() + "/avatars/web.png"; // 暂时使用web图标
            case SHOPIFY:
                return getDefaultShopifyAvatarUrl();
            case LAZADA:
                return getDefaultLazadaAvatarUrl();
            case SHOPEE:
                return getDefaultHost() + "/avatars/web.png"; // 暂时使用web图标
                
            // 测试
            case TEST:
                return getDefaultHost() + "/avatars/app.png";
                
            default:
                return getDefaultVisitorAvatarUrl();
        }
    }
    
    /**
     * 根据字符串匹配获取头像（后备方案）
     */
    private static String getAvatarByString(String channel) {
        String upperChannel = channel.toUpperCase();
        
        if (upperChannel.contains("WEB")) {
            return getDefaultWebAvatarUrl();
        } else if (upperChannel.contains("ANDROID")) {
            return getDefaultAndroidAvatarUrl();
        } else if (upperChannel.contains("IOS")) {
            return getDefaultIosAvatarUrl();
        } else if (upperChannel.contains("UNIAPP")) {
            return getDefaultUniappAvatarUrl();
        } else if (upperChannel.contains("WECHAT")) {
            return getDefaultWechatMpAvatarUrl();
        } else if (upperChannel.contains("FLUTTER")) {
            return getDefaultFlutterWebAvatarUrl();
        } else if (upperChannel.contains("EMAIL")) {
            return getDefaultHost() + "/avatars/email.png";
        } else if (upperChannel.contains("TELEGRAM")) {
            return getDefaultTelegramAvatarUrl();
        } else if (upperChannel.contains("LINE")) {
            return getDefaultLineAvatarUrl();
        } else if (upperChannel.contains("WHATSAPP")) {
            return getDefaultWhatsappAvatarUrl();
        } else if (upperChannel.contains("MESSENGER")) {
            return getDefaultMessengerAvatarUrl();
        } else if (upperChannel.contains("INSTAGRAM")) {
            return getDefaultInstagramAvatarUrl();
        } else if (upperChannel.contains("DOUYIN")) {
            return getDefaultDouyinAvatarUrl();
        } else if (upperChannel.contains("XIAOHONGSHU")) {
            return getDefaultXiaohongshuAvatarUrl();
        } else if (upperChannel.contains("KUAISHOU")) {
            return getDefaultKuaishouAvatarUrl();
        } else if (upperChannel.contains("BILIBILI")) {
            return getDefaultBilibiliAvatarUrl();
        } else if (upperChannel.contains("WEIBO")) {
            return getDefaultWeiboAvatarUrl();
        } else if (upperChannel.contains("JD") || upperChannel.contains("JINGDONG")) {
            return getDefaultJingdongAvatarUrl();
        } else if (upperChannel.contains("PINDUODUO")) {
            return getDefaultPinduoduoAvatarUrl();
        } else if (upperChannel.contains("SHOPIFY")) {
            return getDefaultShopifyAvatarUrl();
        } else if (upperChannel.contains("LAZADA")) {
            return getDefaultLazadaAvatarUrl();
        } else if (upperChannel.contains("WORDPRESS")) {
            return getDefaultWordpressAvatarUrl();
        } else if (upperChannel.contains("MAGENTO")) {
            return getDefaultMagentoAvatarUrl();
        } else if (upperChannel.contains("WOOCOMMERCE")) {
            return getDefaultWooCommerceAvatarUrl();
        } else if (upperChannel.contains("OPENCART")) {
            return getDefaultOpenCartAvatarUrl();
        } else if (upperChannel.contains("PRESTASHOP")) {
            return getDefaultPrestaShopAvatarUrl();
        } else if (upperChannel.contains("DIFY")) {
            return getDefaultDifyAvatarUrl();
        } else if (upperChannel.contains("COZE")) {
            return getDefaultCozeAvatarUrl();
        } else if (upperChannel.contains("NPM")) {
            return getDefaultNpmAvatarUrl();
        }
        
        return getDefaultVisitorAvatarUrl();
    }

    /**
     * https://cdn.weiyuai.cn/avatars/girl.png
     * https://cdn.weiyuai.cn/avatars/boy.png
     */
    

}
