/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-21 10:01:12
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-12 18:23:15
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.thread;

import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseRequest;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.rbac.user.UserProtobuf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class ThreadRequest extends BaseRequest {
    // 
    private String topic;

    private String state;

    // 意图类型
    @Builder.Default
    private String intentionType = ThreadIntentionTypeEnum.OTHER.name();

    // 情绪类型
    @Builder.Default
    private String emotionType = ThreadEmotionTypeEnum.OTHER.name();

    // 质检结果
    @Builder.Default
    private String qualityCheckResult = ThreadQualityCheckResultEnum.OTHER.name();

    // 是否被评价
    @Builder.Default
    private Boolean rated = false;


    private UserProtobuf user;
    
    private String userNickname;

    private String ownerNickname;

    private String ownerUid;

    // 
    @Builder.Default
    private Boolean top = false;

    @Builder.Default
    private Boolean unread = false;

    @Builder.Default
    private Integer queueNumber = 0;

    @Builder.Default
    private Integer unreadCount = 0;

    @Builder.Default
    private Boolean mute = false;

    @Builder.Default
    private Boolean hide = false;

    @Builder.Default
    private Integer star = 0;

    @Builder.Default
    private Boolean folded = false;

    @Builder.Default
    private Boolean autoClose = false;

    // 是否机器人
    // @Builder.Default
    // private Boolean robot = false;

    // 备注
    private String note;

    // 标签
    @Builder.Default
    private String tags = BytedeskConsts.EMPTY_ARRAY_STRING;

    // 用于更新robot-agent-llm配置，不能修改为UserProtobuf,否则会序列化出错
    private String agent;

    // group member uids
    @Builder.Default
    private List<String> memberUids = new ArrayList<>();

    private String searchText;

    // used for client query
    private String componentType;
}
