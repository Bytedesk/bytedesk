/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-21 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-09-21 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.push.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * 设备推送供应商配置
 *
 * <p>配置前缀 bytedesk.push，与既有 push.properties 中的历史 key（bytedesk.push.apns.*，代码未消费）同前缀互不冲突。
 *
 * <p>示例（各 profile 的 push.properties）：
 * <pre>
 * # 设备推送供应商：apns-direct（默认，直连 APNs）| aliyun（阿里云 EMAS Push）
 * bytedesk.push.provider=apns-direct
 * bytedesk.push.aliyun.app-key=123456
 * bytedesk.push.aliyun.app-secret=ENC(...)
 * bytedesk.push.aliyun.access-key-id=LTAI...
 * bytedesk.push.aliyun.access-key-secret=ENC(...)
 * </pre>
 *
 * <p>密钥约定：app-secret / access-key-secret 使用 Jasypt ENC(...) 加密存储，
 * 对齐 SmsProviderEntity.accessKeySecret 的加密约定，禁止明文入库入仓。
 */
@Data
@Component
@ConfigurationProperties(prefix = "bytedesk.push")
public class PushProviderProperties {

    public static final String PROVIDER_ALIYUN = "aliyun";
    public static final String PROVIDER_APNS_DIRECT = "apns-direct";

    /** 设备推送供应商：aliyun | apns-direct（默认 apns-direct，保持存量行为，灰度/回滚入口） */
    private String provider = PROVIDER_APNS_DIRECT;

    private final Aliyun aliyun = new Aliyun();

    @Data
    public static class Aliyun {

        /**
         * 阿里云 EMAS Android 应用 AppKey（仅作服务端推送兑底值）。
         *
         * <p>优先级：后台 SuperSystemConfig「推送配置」push.aliyun.android.appKey
         * （与移动端 /api/v1/push/config 同源）→ 本兑底 key。
         *
         * <p>命名带 android：本通道仅面向 Android 设备（iOS 服务端推送走 apns-direct，
         * 不读此处）；appSecret 服务端推送不消费（仅移动端 SDK initPush 用，
         * 由后台「推送配置」鉴权下发），故不提供未属性，避免与 iOS/移动端凭据混淆。
         *
         * <p>绑定 key：bytedesk.push.aliyun.android-app-key
         */
        private String androidAppKey = "";

        /** 阿里云账号 AccessKey ID（RAM 子账号，仅授予推送权限） */
        private String accessKeyId = "";

        /** 阿里云账号 AccessKey Secret（Jasypt ENC 加密） */
        private String accessKeySecret = "";

        /** OpenAPI endpoint，默认 cloudpush.aliyuncs.com */
        private String endpoint = "cloudpush.aliyuncs.com";

        /** Push API DeviceType：ALL（默认，Android+iOS 都发）| ANDROID | IOS */
        private String deviceType = "ALL";

        /** iOS APNs 环境：DEV | PRODUCT（对齐 ApnsTokenEnvironmentEnum，避免开发环境推到生产） */
        private String iosApnsEnv = "PRODUCT";

        /** Android 通知类型：NOTIFICATION | MESSAGE | BOTH（默认 BOTH） */
        private String androidNotifyType = "BOTH";

        /** Android 点击通知后打开的 Activity（空=打开应用），如 com.kefux.im.MainActivity */
        private String androidActivity = "";

        /** OpenAPI 调用超时（秒） */
        private int timeoutSeconds = 5;
    }
}
