/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-04 10:50:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-15 13:41:37
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.constant;

import java.util.List;

import com.bytedesk.core.constant.I18Consts;

public class I18ServiceConsts {
    
    private I18ServiceConsts() {}

    // 客服相关
    public static final String I18N_AGENT_EXISTS = I18Consts.I18N_PREFIX + "agent.exists";
    
    // 转接相关
    public static final String I18N_TRANSFER_NOTICE_TITLE = I18Consts.I18N_PREFIX + "transfer.notice.title";
    public static final String I18N_TRANSFER_NOTICE_CONTENT = I18Consts.I18N_PREFIX + "transfer.notice.content";
    public static final String I18N_TRANSFER_ACCEPT_NOTICE_TITLE = I18Consts.I18N_PREFIX + "transfer.accept.notice.title";
    public static final String I18N_TRANSFER_ACCEPT_NOTICE_CONTENT = I18Consts.I18N_PREFIX + "transfer.accept.notice.content";
    public static final String I18N_TRANSFER_REJECT_NOTICE_TITLE = I18Consts.I18N_PREFIX + "transfer.reject.notice.title";
    public static final String I18N_TRANSFER_REJECT_NOTICE_CONTENT = I18Consts.I18N_PREFIX + "transfer.reject.notice.content";
    public static final String I18N_TRANSFER_TIMEOUT_NOTICE_TITLE = I18Consts.I18N_PREFIX + "transfer.timeout.notice.title";
    public static final String I18N_TRANSFER_TIMEOUT_NOTICE_CONTENT = I18Consts.I18N_PREFIX + "transfer.timeout.notice.content";
    public static final String I18N_TRANSFER_CANCEL_NOTICE_TITLE = I18Consts.I18N_PREFIX + "transfer.cancel.notice.title";
    public static final String I18N_TRANSFER_CANCEL_NOTICE_CONTENT = I18Consts.I18N_PREFIX + "transfer.cancel.notice.content";
    
    // 转接状态异常
    public static final String I18N_ALREADY_IN_TRANSFER_PENDING_STATE = I18Consts.I18N_PREFIX + "already.in.transfer.pending.state";
    public static final String I18N_ALREADY_IN_TRANSFER_ACCEPTED_STATE = I18Consts.I18N_PREFIX + "already.in.transfer.accepted.state";
    public static final String I18N_ALREADY_IN_TRANSFER_REJECTED_STATE = I18Consts.I18N_PREFIX + "already.in.transfer.rejected.state";
    public static final String I18N_ALREADY_IN_TRANSFER_TIMEOUT_STATE = I18Consts.I18N_PREFIX + "already.in.transfer.timeout.state";
    public static final String I18N_ALREADY_IN_TRANSFER_CANCELED_STATE = I18Consts.I18N_PREFIX + "already.in.transfer.canceled.state";
    
    // 邀请相关
    public static final String I18N_INVITE_NOTICE_TITLE = I18Consts.I18N_PREFIX + "invite.notice.title";
    public static final String I18N_INVITE_NOTICE_CONTENT = I18Consts.I18N_PREFIX + "invite.notice.content";
    public static final String I18N_INVITE_ACCEPT_NOTICE_TITLE = I18Consts.I18N_PREFIX + "invite.accept.notice.title";
    public static final String I18N_INVITE_ACCEPT_NOTICE_CONTENT = I18Consts.I18N_PREFIX + "invite.accept.notice.content";
    public static final String I18N_INVITE_REJECT_NOTICE_TITLE = I18Consts.I18N_PREFIX + "invite.reject.notice.title";
    public static final String I18N_INVITE_REJECT_NOTICE_CONTENT = I18Consts.I18N_PREFIX + "invite.reject.notice.content";
    public static final String I18N_INVITE_TIMEOUT_NOTICE_TITLE = I18Consts.I18N_PREFIX + "invite.timeout.notice.title";
    public static final String I18N_INVITE_TIMEOUT_NOTICE_CONTENT = I18Consts.I18N_PREFIX + "invite.timeout.notice.content";
    public static final String I18N_INVITE_CANCEL_NOTICE_TITLE = I18Consts.I18N_PREFIX + "invite.cancel.notice.title";
    public static final String I18N_INVITE_CANCEL_NOTICE_CONTENT = I18Consts.I18N_PREFIX + "invite.cancel.notice.content";
    public static final String I18N_INVITE_EXIT_NOTICE_TITLE = I18Consts.I18N_PREFIX + "invite.exit.notice.title";
    public static final String I18N_INVITE_EXIT_NOTICE_CONTENT = I18Consts.I18N_PREFIX + "invite.exit.notice.content";
    public static final String I18N_INVITE_REMOVE_NOTICE_TITLE = I18Consts.I18N_PREFIX + "invite.remove.notice.title";
    public static final String I18N_INVITE_REMOVE_NOTICE_CONTENT = I18Consts.I18N_PREFIX + "invite.remove.notice.content";
    
    // 邀请状态异常
    public static final String I18N_ALREADY_IN_INVITE_PENDING_STATE = I18Consts.I18N_PREFIX + "already.in.invite.pending.state";
    public static final String I18N_ALREADY_IN_INVITE_ACCEPTED_STATE = I18Consts.I18N_PREFIX + "already.in.invite.accepted.state";
    public static final String I18N_ALREADY_IN_INVITE_REJECTED_STATE = I18Consts.I18N_PREFIX + "already.in.invite.rejected.state";
    public static final String I18N_ALREADY_IN_INVITE_TIMEOUT_STATE = I18Consts.I18N_PREFIX + "already.in.invite.timeout.state";
    public static final String I18N_ALREADY_IN_INVITE_CANCELED_STATE = I18Consts.I18N_PREFIX + "already.in.invite.canceled.state";

    // 机器人转人工默认值
    public static final String I18N_DEFAULT_MANUAL_TRANSFER_LABEL = I18Consts.I18N_PREFIX + "default.manual.transfer.label";
    public static final String I18N_ORGANIZATION_WORKGROUP_LIMIT_EXCEEDED = I18Consts.I18N_PREFIX + "organization.workgroup.limit.exceeded";
    public static final int I18N_DEFAULT_MIN_CONFIDENCE = 65;
    public static final int I18N_DEFAULT_MAX_ROBOT_REPLIES = 3;
    public static final int I18N_DEFAULT_AUTO_TRANSFER_DELAY_SECONDS = 0;
    public static final int I18N_DEFAULT_COOLDOWN_SECONDS = 90;

    // 客服状态默认小休原因
    public static final String I18N_DEFAULT_AGENT_STATUS_REST_REASON_MEETING = I18Consts.I18N_PREFIX + "agent.status.rest.reason.meeting";
    public static final String I18N_DEFAULT_AGENT_STATUS_REST_REASON_TRAINING = I18Consts.I18N_PREFIX + "agent.status.rest.reason.training";
    public static final String I18N_DEFAULT_AGENT_STATUS_REST_REASON_LUNCH = I18Consts.I18N_PREFIX + "agent.status.rest.reason.lunch";
    public static final String I18N_DEFAULT_AGENT_STATUS_REST_REASON_BREAK = I18Consts.I18N_PREFIX + "agent.status.rest.reason.break";

    public static List<String> getDefaultAgentStatusRestReasons() {
        return List.of(
            I18N_DEFAULT_AGENT_STATUS_REST_REASON_MEETING,
            I18N_DEFAULT_AGENT_STATUS_REST_REASON_TRAINING,
            I18N_DEFAULT_AGENT_STATUS_REST_REASON_LUNCH,
            I18N_DEFAULT_AGENT_STATUS_REST_REASON_BREAK
        );
    }

}
