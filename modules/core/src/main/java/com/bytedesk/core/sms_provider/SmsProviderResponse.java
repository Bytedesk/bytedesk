/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:26:12
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-04 15:36:28
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.sms_provider;


import com.bytedesk.core.base.BaseResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class SmsProviderResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    private String name;

    private String description;

    private String type;

    private String color;

    private Integer order;

    // ============ 短信服务商配置字段 ============

    /**
     * 短信服务商类型 (ALIYUN, TENCENT, HUAWEI等)
     */
    private String providerType;

    /**
     * 短信服务访问密钥ID
     */
    private String accessKeyId;

    /**
     * 短信服务访问密钥Secret
     * 注意：响应中返回掩码处理后的值
     */
    private String accessKeySecret;

    /**
     * 短信服务区域/Region
     */
    private String region;

    /**
     * 短信服务端点/Endpoint
     */
    private String endpoint;

    /**
     * 配置是否启用
     */
    private Boolean enabled;

}
