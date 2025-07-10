/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-25 13:07:20
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-10 10:02:35
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.enums;

public enum ChannelEnum {
    SYSTEM,
    // 
    WEB,
    WEB_PC, // pc端
    WEB_H5, // h5端
    WEB_VISITOR, // 访客端
    WEB_FLOAT, // 悬浮窗
    WEB_ADMIN, // 管理端
    // 
    IOS,
    ANDROID,
    // 
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
    LARK,            // 飞书（Lark）
    CUSTOM,          // 自定义渠道
    //
    // 其他渠道
    EMAIL,           // 邮件
    SMS,             // 短信
    PHONE,           // 电话
    
    // Meta
    MESSENGER,       // Facebook Messenger
    INSTAGRAM,       // Instagram
    WHATSAPP,        // WhatsApp
    //
    // 海外社交媒体
    TWITTER,         // Twitter/X
    FACEBOOK,        // Facebook
    LINKEDIN,        // LinkedIn
    YOUTUBE,         // YouTube
    TIKTOK,          // TikTok
    PINTEREST,       // Pinterest
    REDDIT,          // Reddit
    SNAPCHAT,        // Snapchat
    //
    // 海外即时通讯
    TELEGRAM,        // Telegram
    LINE,            // LINE
    KAKAO,           // KakaoTalk
    VIBER,           // Viber
    SIGNAL,          // Signal
    DISCORD,         // Discord
    SLACK,           // Slack
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
    public static ChannelEnum fromValue(String value) {
        for (ChannelEnum type : ChannelEnum.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant with value: " + value);
    }
    
    /**
     * 将客户端类型转换为中文显示
     * @param client 客户端类型字符串
     * @return 对应的中文名称
     */
    public static String toChineseDisplay(String client) {
        try {
            ChannelEnum clientEnum = fromValue(client);
            return clientEnum.toChineseDisplay();
        } catch (Exception e) {
            return client;
        }
    }
    
    /**
     * 获取当前枚举值的中文显示
     * @return 对应的中文名称
     */
    public String toChineseDisplay() {
        switch (this) {
            case SYSTEM:
                return "系统";
            case WEB:
                return "网页";
            case WEB_PC:
                return "网页PC端";
            case WEB_H5:
                return "网页H5端";
            case WEB_VISITOR:
                return "网页访客端";
            case WEB_ADMIN:
                return "网页管理端";
            case IOS:
                return "iOS";
            case ANDROID:
                return "安卓";
            case ELECTRON:
                return "桌面应用";
            case LINUX:
                return "Linux";
            case MACOS:
                return "macOS";
            case WINDOWS:
                return "Windows";
            case FLUTTER:
                return "Flutter";
            case FLUTTER_WEB:
                return "Flutter网页版";
            case FLUTTER_ANDROID:
                return "Flutter安卓版";
            case FLUTTER_IOS:
                return "Flutter iOS版";
            case FLUTTER_MACOS:
                return "Flutter macOS版";
            case FLUTTER_WINDOWS:
                return "Flutter Windows版";
            case FLUTTER_LINUX:
                return "Flutter Linux版";
            case UNIAPP:
                return "UniApp";
            case UNIAPP_WEB:
                return "UniApp网页版";
            case UNIAPP_ANDROID:
                return "UniApp安卓版";
            case UNIAPP_IOS:
                return "UniApp iOS版";
            case WECHAT:
                return "微信";
            case WECHAT_MINI:
                return "微信小程序";
            case WECHAT_MP:
                return "微信公众号";
            case WECHAT_WORK:
                return "企业微信";
            case WECHAT_KEFU:
                return "微信客服";
            case WECHAT_CHANNEL:
                return "微信渠道";
            case XIAOHONGSHU:
                return "小红书";
            case DOUYIN:
                return "抖音";
            case KUAISHOU:
                return "快手";
            case BILIBILI:
                return "B站";
            case WEIBO:
                return "微博";
            case ZHIHU:
                return "知乎";
            case TOUTIAO:
                return "头条";
            case DOUBAN:
                return "豆瓣";
            case TAOBAO:
                return "淘宝";
            case TMALL:
                return "天猫";
            case JD:
                return "京东";
            case PINDUODUO:
                return "拼多多";
            case MEITUAN:
                return "美团";
            case ELEME:
                return "饿了么";
            case DIANPING:
                return "大众点评";
            case DINGTALK:
                return "钉钉";
            case FEISHU:
                return "飞书";
            case LARK:
                return "飞书（Lark）";
            case EMAIL:
                return "邮件";
            case SMS:
                return "短信";
            case PHONE:
                return "电话";
            case TWITTER:
                return "Twitter/X";
            case FACEBOOK:
                return "Facebook";
            case INSTAGRAM:
                return "Instagram";
            case LINKEDIN:
                return "LinkedIn";
            case YOUTUBE:
                return "YouTube";
            case TIKTOK:
                return "TikTok";
            case PINTEREST:
                return "Pinterest";
            case REDDIT:
                return "Reddit";
            case SNAPCHAT:
                return "Snapchat";
            case WHATSAPP:
                return "WhatsApp";
            case TELEGRAM:
                return "Telegram";
            case LINE:
                return "LINE";
            case KAKAO:
                return "KakaoTalk";
            case VIBER:
                return "Viber";
            case SIGNAL:
                return "Signal";
            case DISCORD:
                return "Discord";
            case SLACK:
                return "Slack";
            case MESSENGER:
                return "Facebook Messenger";
            case AMAZON:
                return "亚马逊";
            case EBAY:
                return "eBay";
            case SHOPIFY:
                return "Shopify";
            case LAZADA:
                return "来赞达";
            case SHOPEE:
                return "虾皮";
            case TEST:
                return "测试";
            default:
                return this.name();
        }
    }
}
