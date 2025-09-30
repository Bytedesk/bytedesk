/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 16:45:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-28 16:23:13
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot;

import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseRequest;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.kbase.settings.ServiceSettingsRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class RobotRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;


    private String name;
    
    private String nickname;
    
    private String avatar;

    private String description;

    @Builder.Default
    private ServiceSettingsRequest serviceSettings = new ServiceSettingsRequest();

    // 方便前端搜索
    private String prompt;

    // 使用基类中content，可能是用户输入问题：question，也可能是会话内容，或者是工单内容，或者是消息内容
    // private String content;

    @Builder.Default
    private RobotLlm llm = new RobotLlm();

    @Builder.Default
    private String defaultReply = I18Consts.I18N_ROBOT_DEFAULT_REPLY;

    // 机器人分类
    private String categoryUid;

    private Boolean kbEnabled;

    private String kbUid;

    // 优先判断是否启用流程，流程优先于知识库
    // flow enabled
    private Boolean flowEnabled;

    // flow uid
    private String flowUid;

    // 是否是系统自带
    @Builder.Default
    private Boolean system = false;

    // 用于客户端点击uid
    private String threadUid; // 对应会话
    // rate message helpful/unhelpful
    private String messageUid; // 对应消息
    // 点踩的情况下的反馈意见
    @Builder.Default
    private List<String> rateDownTagList = new ArrayList<>();
    // 点踩的原因
    private String rateDownReason;
    // 点踩的用户
    private String user;

}
