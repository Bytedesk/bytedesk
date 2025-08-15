/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-03 15:41:55
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-15 19:24:40
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.thread.enums;

public enum ThreadTransferStatusEnum {
    NONE, // 无转接
    TRANSFER_PENDING, // 转接待处理
    TRANSFER_ACCEPTED, // 接受转接
    TRANSFER_REJECTED, // 拒绝转接
    TRANSFER_TIMEOUT, // 转接超时
    TRANSFER_CANCELED, // 取消转接
}
