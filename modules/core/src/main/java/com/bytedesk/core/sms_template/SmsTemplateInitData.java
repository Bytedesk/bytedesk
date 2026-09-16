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
package com.bytedesk.core.sms_template;

/**
 * 短信模板初始化数据
 * 定义系统默认短信模板
 */
public class SmsTemplateInitData {

    /** 工单新回复通知 - 统一工单短信模板（所有工单通知共用同一个模板） */
    public static final String TICKET_REPLY_NAME = "工单新回复通知";
    public static final String TICKET_REPLY_CONTENT = "尊敬的${name}，您的工单有新的回复，请注意查收。";

    /** 所有默认短信模板定义 */
    public static final SmsTemplateDef[] DEFAULT_TICKET_TEMPLATES = {
        new SmsTemplateDef(
            "SMS_TICKET_REPLY",
            TICKET_REPLY_NAME,
            TICKET_REPLY_CONTENT,
            SmsTemplateTypeEnum.TICKET.name(),
            "工单有新回复时通知访客的统一短信模板",
            null,  // signName: 留空由用户填写
            null   // templateCode: 留空由用户填写
        )
    };

    /**
     * 短信模板定义
     */
    public record SmsTemplateDef(
        String uid,
        String name,
        String content,
        String type,
        String description,
        String signName,
        String templateCode
    ) {}
}