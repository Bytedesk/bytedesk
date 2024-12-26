/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-25 13:07:20
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-26 15:26:42
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.enums;

public enum ClientEnum {
    SYSTEM,
    SYSTEM_AUTO, // auto reply
    SYSTEM_BOT, // robot reply
    // 
    WEB,
    WEB_PC, // pc端
    WEB_H5, // h5端
    WEB_VISITOR, // 访客端
    WEB_ADMIN, // 管理端
    // 
    IOS,
    ANDROID,
    ELECTRON,
    LINUX,
    MACOS,
    WINDOWS,
    // 
    FLUTTER,
    FLUTTER_WEB,
    FLUTTER_ANDROID,
    FLUTTER_IOS,
    FLUTTER_MACOS,
    FLUTTER_WINDOWS,
    FLUTTER_LINUX,
    // 
    UNIAPP,
    UNIAPP_WEB,
    UNIAPP_ANDROID,
    UNIAPP_IOS,
    // 
    WECHAT,
    WECHAT_MINI,
    WECHAT_MP,
    WECHAT_WORK,
    WECHAT_KEFU,
    WECHAT_CHANNEL,
    //
    // 社交媒体渠道
    XIAOHONGSHU,     // 小红书
    DOUYIN,          // 抖音
    KUAISHOU,        // 快手
    BILIBILI,        // B站
    WEIBO,           // 微博
    ZHIHU,           // 知乎
    TOUTIAO,         // 头条
    DOUBAN,          // 豆瓣
    //
    // 电商渠道
    TAOBAO,          // 淘宝
    TMALL,           // 天猫
    JD,              // 京东
    PINDUODUO,       // 拼多多
    MEITUAN,         // 美团
    ELEME,           // 饿了么
    DIANPING,        // 大众点评
    //
    // 企业渠道
    DINGTALK,        // 钉钉
    FEISHU,          // 飞书
    WECOM,           // 企业微信
    //
    // 其他渠道
    EMAIL,           // 邮件
    SMS,             // 短信
    PHONE,           // 电话
    //
    // 海外社交媒体
    TWITTER,         // Twitter/X
    FACEBOOK,        // Facebook
    INSTAGRAM,       // Instagram
    LINKEDIN,        // LinkedIn
    YOUTUBE,         // YouTube
    TIKTOK,          // TikTok
    PINTEREST,       // Pinterest
    REDDIT,          // Reddit
    SNAPCHAT,        // Snapchat
    //
    // 海外即时通讯
    WHATSAPP,        // WhatsApp
    TELEGRAM,        // Telegram
    LINE,            // LINE
    KAKAO,           // KakaoTalk
    VIBER,           // Viber
    SIGNAL,          // Signal
    DISCORD,         // Discord
    SLACK,           // Slack
    MESSENGER,       // Facebook Messenger
    //
    // 海外电商
    AMAZON,          // 亚马逊
    EBAY,            // eBay
    SHOPIFY,         // Shopify
    LAZADA,          // 来赞达
    SHOPEE,          // 虾皮
    // 
    TEST,
    ;

    // 根据字符串查找对应的枚举常量
    public static ClientEnum fromValue(String value) {
        for (ClientEnum type : ClientEnum.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant with value: " + value);
    }
}
