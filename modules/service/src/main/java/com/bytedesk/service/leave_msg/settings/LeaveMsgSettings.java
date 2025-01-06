/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-17 23:33:40
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-23 16:12:21
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.leave_msg.settings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.service.leave_msg.LeaveMsgNotifyTypeEnum;
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
public class LeaveMsgSettings implements Serializable {

    private static final long serialVersionUID = 1L;

    // 留言开关
    @Builder.Default
    @Column(name = "is_leave_msg_enabled")
    private boolean leaveMsgEnabled = true;

    @NotBlank
    @Builder.Default
    private String leaveMsgTip = I18Consts.I18N_LEAVEMSG_TIP;

    // 处理留言agent
    @Builder.Default
    private String leaveMsgAgentUid = BytedeskConsts.EMPTY_STRING;

    // 是否支持留言提醒
    @Builder.Default
    private Boolean leaveMsgNotify = false;

    // 留言提醒类型
    @Builder.Default
    private String leaveMsgNotifyType = LeaveMsgNotifyTypeEnum.EMAIL.name();

    // 留言提醒邮箱
    @Builder.Default
    private String leaveMsgNotifyEmail = BytedeskConsts.EMPTY_STRING;

    @Builder.Default
    private String leaveMsgNotifyMobile = BytedeskConsts.EMPTY_STRING;

    // @Builder.Default
    // private String leaveMsgNotifyWechat = BytedeskConsts.EMPTY_STRING;

    // 留言提醒时间
    // @Builder.Default
    // private String leaveMsgNotifyTime = BytedeskConsts.EMPTY_STRING;

    // 留言表单
    @Builder.Default
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    @JdbcTypeCode(SqlTypes.JSON)
    private String leaveMsgForm = BytedeskConsts.EMPTY_JSON_STRING;

    /** work time */
    @Builder.Default
    @OneToMany(fetch = FetchType.EAGER)
    private List<WorktimeEntity> worktimes = new ArrayList<>();

    //
    public boolean isInServiceTime() {
        if (worktimes == null || worktimes.isEmpty()) {
            return true;
        }
        return worktimes.stream()
            .anyMatch(WorktimeEntity::isWorkTime);
    }

}
