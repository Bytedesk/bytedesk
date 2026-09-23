/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-23 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-09-23 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.core.push_android;

/**
 * Android 推送记录类型
 *
 * <p>由调用方显式传入（router 侧通过 extras 受控键 source 识别），
 * 不能根据其它 key 是否存在隐式推断。
 *
 * <p>见 docs/plans/2026-09-23-android-push-record-entity-plan.md §3.3
 */
public enum PushAndroidTypeEnum {
    /** 新消息提醒 */
    MESSAGE,
    /** 工单状态通知（messageUid 复用存 ticketUid，与 iOS 惯例一致） */
    TICKET,
    /** 管理后台测试推送 */
    TEST,
}
