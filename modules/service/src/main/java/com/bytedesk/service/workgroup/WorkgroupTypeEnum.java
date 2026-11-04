/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-03-06 00:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-03-06 00:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.service.workgroup;

import java.util.Locale;

/**
 * Workgroup business types for different customer service scenarios.
 */
public enum WorkgroupTypeEnum {

    /** 通用接待（默认） */
    GENERAL,
    /** 售前咨询 */
    PRESALES,
    /** 售后服务 */
    AFTERSALES,
    /** 工单处理 */
    TICKET,
    /** 技术支持 */
    TECH_SUPPORT,
    /** 账单与支付 */
    BILLING,
    /** 投诉处理 */
    COMPLAINT,
    /** 会员/续费挽留 */
    RETENTION,
    /** VIP 专席 */
    VIP,
    /** 海外/多语言支持 */
    OVERSEAS,
    /** 渠道合作/商务拓展 */
    PARTNER,
    /** 回访/客户成功 */
    CUSTOMER_SUCCESS;

    public static String normalize(String rawType) {
        if (rawType == null || rawType.trim().isEmpty()) {
            return GENERAL.name();
        }
        String candidate = rawType.trim().toUpperCase(Locale.ROOT);
        try {
            return WorkgroupTypeEnum.valueOf(candidate).name();
        } catch (IllegalArgumentException ex) {
            return GENERAL.name();
        }
    }
}
