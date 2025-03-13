/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-21 10:01:27
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-13 20:09:46
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.thread;

import java.util.List;

import com.bytedesk.core.base.BaseResponse;
import com.bytedesk.core.rbac.user.UserProtobuf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * for agent client
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ThreadResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    private String topic;

    private String content;

    private String type;

    // 意图类型
    private String intentionType;

    // 情绪类型
    private String emotionType;

    // 质检结果
    private String qualityCheckResult;

    // 是否被评价
    private Boolean rated;

    private String state;

    private Boolean top;

    private Boolean unread;

    private Integer queueNumber;

    private Integer unreadCount;

    private Boolean mute;

    private Boolean hide;

    private Integer star;

    private Boolean folded;

    private Boolean autoClose;

    // private Boolean robot;

    // 备注
    private String note;

    // 标签
    private List<String> tagList;

    private String client;

    private String extra;

    private String agent;

	// private LocalDateTime updatedAt;

    private UserProtobuf user;
    //
    private UserProtobuf owner;
}
