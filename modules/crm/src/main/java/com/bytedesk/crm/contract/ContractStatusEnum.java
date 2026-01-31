/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-01-31
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-01-31
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.crm.contract;

/**
 * 合同统一状态枚举。
 *
 * 现在合并为单一 status 字段，便于统一展示与筛选。
 */
public enum ContractStatusEnum {
    DRAFT,
    APPROVING,
    APPROVED,
    REJECTED,
    ACTIVE,
    FINISHED,
    VOIDED
}
