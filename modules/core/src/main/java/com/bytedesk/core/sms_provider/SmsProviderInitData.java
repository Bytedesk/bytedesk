/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-11 08:54:35
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-04 17:12:37
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.sms_provider;

/**
 * 短信服务提供商初始化数据
 * 定义各主流短信服务商的默认 Region/Endpoint 配置，用户只需填写 AccessKey 和签名模板即可使用
 */
public class SmsProviderInitData {

    /** 所有默认短信服务提供商定义 */
    public static final SmsProviderDef[] DEFAULT_SMS_PROVIDERS = {
        // ========== 阿里云短信 ==========
        new SmsProviderDef(
            "SMS_PROVIDER_ALIYUN",
            "阿里云短信",
            SmsProviderTypeEnum.ALIYUN.name(),
            "ALIYUN",
            "cn-hangzhou",
            "dysmsapi.aliyuncs.com"
        ),
        // ========== 腾讯云短信 ==========
        new SmsProviderDef(
            "SMS_PROVIDER_TENCENT",
            "腾讯云短信",
            SmsProviderTypeEnum.TENCENT.name(),
            "TENCENT",
            "ap-guangzhou",
            "sms.tencentcloudapi.com"
        ),
        // ========== 华为云短信 ==========
        new SmsProviderDef(
            "SMS_PROVIDER_HUAWEI",
            "华为云短信",
            SmsProviderTypeEnum.HUAWEI.name(),
            "HUAWEI",
            "cn-north-4",
            "sms.cn-north-4.myhuaweicloud.com"
        ),
        // ========== AWS 短信 ==========
        new SmsProviderDef(
            "SMS_PROVIDER_AWS",
            "AWS短信",
            SmsProviderTypeEnum.AWS.name(),
            "AWS",
            "us-east-1",
            "sns.us-east-1.amazonaws.com"
        ),
        // ========== 其他（通用模板） ==========
        new SmsProviderDef(
            "SMS_PROVIDER_OTHER",
            "其他短信服务商",
            SmsProviderTypeEnum.OTHER.name(),
            "OTHER",
            "",
            ""
        )
    };

    /**
     * 短信服务提供商定义
     * 预设了常见短信服务商的 Region/Endpoint 配置，敏感字段（AccessKey、签名、模板）留空由用户填写
     */
    public record SmsProviderDef(
        String uid,
        String name,
        String type,
        String providerType,
        String region,
        String endpoint
    ) {}
}