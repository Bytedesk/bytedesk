/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-09-24 11:04:59
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-24 11:16:57
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.pipeline;

import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.ai.robot_message.RobotMessageUtils;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.thread.ThreadProtobuf;

import lombok.Data;

@Data
public class PipelineChatRequest {
    // 基本入参
    private String query;
    private String orgUid;
    private String threadTopic;
    private Integer topK;
    private Boolean enableRewrite; // 是否启用查询重写

    // 机器人解析策略：robotJson > robotUid > orgUid 默认
    private String robotJson;
    private String robotUid;

    // 预留：搜索类型/来源过滤/模型等
    private String searchType; // FULLTEXT/VECTOR/MIXED

    public MessageProtobuf toMessageQuery() {
        ThreadProtobuf thread = new ThreadProtobuf();
        thread.setTopic(this.threadTopic);

        MessageProtobuf mp = new MessageProtobuf();
        mp.setContent(this.query);
        mp.setThread(thread);
        return mp;
    }

    public MessageProtobuf toRobotReply(MessageProtobuf queryMsg, RobotProtobuf robot) {
        return RobotMessageUtils.createRobotMessage(queryMsg.getThread(), robot, queryMsg);
    }
}
