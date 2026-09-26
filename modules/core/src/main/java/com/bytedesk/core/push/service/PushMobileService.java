/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-23 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-09-23 10:00:00
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

import com.bytedesk.core.message.MessageProtobuf;

/**
 * 移动端推送网关（坐席/工单通知场景）
 *
 * <p>开源模块（modules/service、modules/ticket 等）只依赖此接口；
 * 实现位于 enterprise/core（com.bytedesk.enterprise.core.push_apns.PushApnsService）。
 * 社区版无实现 bean，调用方需经 ObjectProvider 判空后优雅跳过。
 *
 * <p>接口只收录开源模块实际用到的方法；返回实体的方法（如
 * pushNotificationToUserForResult）只留在企业实现类上，避免接口反向依赖企业实体。
 */
public interface PushMobileService {

    /**
     * 消息事件推送（完整 MessageProtobuf，iOS 富通知路由用）
     *
     * @param userUid  接收用户 uid
     * @param message  消息体
     */
    void pushMessageToUser(String userUid, MessageProtobuf message);

    /**
     * 纯文本通知推送（工单通知等）
     *
     * @param userUid     接收用户 uid
     * @param title       通知标题
     * @param body        通知内容
     * @param trackingUid 通知去重/追踪 uid（工单 uid 等）
     */
    void pushNotificationToUser(String userUid, String title, String body, String trackingUid);
}
