/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-14 07:07:42
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-30 17:27:54
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot_message;

import java.util.List;

import com.bytedesk.core.base.BaseResponse;
import com.bytedesk.core.enums.ChannelEnum;
import com.bytedesk.core.message.MessageExtra;
import com.bytedesk.core.message.MessageStatusEnum;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.rbac.user.UserProtobuf;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class RobotMessageResponse extends BaseResponse {
    
    private String topic;

    private String threadUid;

    // private String type;
	private MessageTypeEnum type;

	private String content;

    // 使用content作为question
    // answer
    private String answer;

	// private String status;
	private MessageStatusEnum status;

	// private String channel;
	private ChannelEnum channel;

	private UserProtobuf user;

    // 使用user作为提问者，robot回答者
    private UserProtobuf robot;

	private MessageExtra extra;

    // 是否未搜索到到答案
    private Boolean isUnAnswered;

    /**
     * @{org.springframework.ai.chat.metadata.Usage}
     */
    private Integer promptTokens;

    private Integer completionTokens;

    private Integer totalTokens;

    // 区分是 rateUp 还是 rateDown
    private String rateType;
    
    // 点踩的情况下的反馈意见
    private List<String> rateDownTagList;

    // 点踩的原因
    private String rateDownReason;
}
