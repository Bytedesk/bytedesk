/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-04 14:26:31
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-14 18:13:20
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.notice;

public enum NoticeTypeEnum {
    LOGIN,
    TRANSFER,
    TRANSFER_REJECT,
    TRANSFER_ACCEPT,
    TRANSFER_TIMEOUT,
    TRANSFER_CANCEL,
    INVITE,
    INVITE_REJECT,
    INVITE_ACCEPT,
    INVITE_TIMEOUT,
    INVITE_CANCEL,
    INVITE_EXIT,
    INVITE_REMOVE,
    INVITE_VISITOR,
    INVITE_VISITOR_REJECT,
    INVITE_VISITOR_ACCEPT,
    INVITE_VISITOR_TIMEOUT,
    INVITE_VISITOR_CANCEL,
    INVITE_GROUP,
    INVITE_GROUP_REJECT,
    INVITE_GROUP_ACCEPT,
    INVITE_GROUP_TIMEOUT,
    INVITE_GROUP_CANCEL,
    INVITE_KBASE,
    INVITE_KBASE_REJECT,
    INVITE_KBASE_ACCEPT,
    INVITE_KBASE_TIMEOUT,
    INVITE_KBASE_CANCEL,
    INVITE_ORGANIZATION,
    INVITE_ORGANIZATION_REJECT,
    INVITE_ORGANIZATION_ACCEPT,
    INVITE_ORGANIZATION_TIMEOUT,
    INVITE_ORGANIZATION_CANCEL;
}
