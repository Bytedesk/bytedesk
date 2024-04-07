/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-03-12 10:52:54
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.socket.protobuf.constant;

public class ProtobufConsts {

    private ProtobufConsts() {
    }

    // 消息
    public static final String MESSAGE_PREFIX = "bytedeskim:message";
    //
    public static final String MESSAGE_FETCH_PREFIX = "bytedeskim:message:fetch:";
    // 根据topic
    public static final String MESSAGE_TOPIC_PREFIX = "bytedeskim:message:fetch:topic:";
    // 客服未读消息
    public static final String MESSAGE_AGENT_UNREAD_PREFIX = "bytedeskim:message:fetch:agentunread:";
    // 访客未读消息
    public static final String MESSAGE_VISITOR_UNREAD_PREFIX = "bytedeskim:message:fetch:visitorunread:";
    //
    public static final String MESSAGE_OFFLINE_PREFIX = "bytedeskim:message:offline:";

}
