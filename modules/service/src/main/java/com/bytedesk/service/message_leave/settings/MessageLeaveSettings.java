/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-17 23:33:40
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-27 12:20:17
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.message_leave.settings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.service.message_leave.MessageLeaveNotifyTypeEnum;
import com.bytedesk.service.worktime.WorktimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class MessageLeaveSettings implements Serializable {

    private static final long serialVersionUID = 1L;

    // 留言开关
    @Builder.Default
    @Column(name = "is_leave_msg_enabled")
    private boolean messageLeaveEnabled = true;

    @NotBlank
    @Builder.Default
    private String messageLeaveTip = I18Consts.I18N_LEAVEMSG_TIP;

    // 处理留言agent
    @Builder.Default
    private String messageLeaveAgentUid = BytedeskConsts.EMPTY_STRING;

    // 是否支持留言提醒
    @Builder.Default
    private Boolean messageLeaveNotify = false;

    // 留言提醒类型
    @Builder.Default
    private String messageLeaveNotifyType = MessageLeaveNotifyTypeEnum.EMAIL.name();

    // 留言提醒邮箱
    @Builder.Default
    private String messageLeaveNotifyEmail = BytedeskConsts.EMPTY_STRING;

    @Builder.Default
    private String messageLeaveNotifyMobile = BytedeskConsts.EMPTY_STRING;

    // @Builder.Default
    // private String messageLeaveNotifyWechat = BytedeskConsts.EMPTY_STRING;

    // 留言提醒时间
    // @Builder.Default
    // private String messageLeaveNotifyTime = BytedeskConsts.EMPTY_STRING;

    // 留言表单
    @Builder.Default
    @Column(length = BytedeskConsts.COLUMN_EXTRA_LENGTH)
    private String messageLeaveForm = BytedeskConsts.EMPTY_JSON_STRING;

    /** work time */
    @Builder.Default
    @OneToMany(fetch = FetchType.EAGER)
    private List<WorktimeEntity> worktimes = new ArrayList<>();

    // TODO: 通知：邮箱、企业微信、钉钉、飞书、短信等

    //
    public boolean isInServiceTime() {
        if (worktimes == null || worktimes.isEmpty()) {
            return true;
        }
        return worktimes.stream()
            .anyMatch(WorktimeEntity::isWorkTime);
    }

}
