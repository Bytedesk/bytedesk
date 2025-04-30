/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-04 16:09:34
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-30 17:22:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot_message;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.message.AbstractMessageEntity;
import com.bytedesk.core.message.MessageStatusEnum;
import com.bytedesk.core.rbac.user.UserProtobuf;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 机器人：问答消息
 */
@Entity
@Data
@SuperBuilder
@NoArgsConstructor // 添加无参构造函数
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Table(name = "bytedesk_ai_robot_message")
public class RobotMessageEntity extends AbstractMessageEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "thread_topic")
    private String topic;

    @Column(name = "thread_uid")
    private String threadUid;
    
    // 可以在这里添加特有的字段（如果有的话）

    // 使用content作为question
    // 回答内容
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String answer;

    // 使用user作为提问者，robot回答者
    @Builder.Default
    @Column(name = "message_robot", length = BytedeskConsts.COLUMN_EXTRA_LENGTH)
    private String robot = BytedeskConsts.EMPTY_JSON_STRING;

    // 区分是 rateUp 还是 rateDown、rateFeedback
    @Builder.Default    
    private String rateType = MessageStatusEnum.RATE_UP.name();

    // 是否未搜索到到答案
    @Builder.Default
    private boolean isUnAnswered = false;

    /**
     * @{org.springframework.ai.chat.metadata.Usage}
     */
    @Builder.Default
    private int promptTokens = 0;

    @Builder.Default
    private int completionTokens = 0;

    @Builder.Default
    private int totalTokens = 0;

    // 可以在这里添加 MessageEntity 特有的字段（如果有的话）
    public UserProtobuf getUserProtobuf() {
        return UserProtobuf.fromJson(getUser());
    }

    // robotProtobuf
    public UserProtobuf getRobotProtobuf() {
        return UserProtobuf.fromJson(getRobot());
    }
    
}
