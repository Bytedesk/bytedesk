/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-11-16
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *   contact: 270580156@qq.com 
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.group;

/**
 * 群组通知常量
 */
public final class GroupNotificationConsts {

    private GroupNotificationConsts() {
    }

    /** 通知类型：NotificationTypeEnum.GROUP.name() */
    public static final String TYPE_GROUP = "GROUP";

    public static final String TITLE_APPLY_CREATED = "入群申请";
    public static final String TITLE_APPLY_APPROVED = "入群申请已通过";
    public static final String TITLE_APPLY_REJECTED = "入群申请被拒绝";
}
