/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-17 23:33:40
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-05 21:31:58
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.leave_msg;

import java.io.Serializable;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.bytedesk.core.constant.BdConstants;
import com.bytedesk.core.constant.TypeConsts;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    @Column(name = "is_enabled")
    private Boolean enabled = true;

    // 是否支持留言提醒
    @Builder.Default
    private Boolean leaveMsgNotify = false;

    // 留言提醒类型
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private LeaveMsgNotifyTypeEnum leaveMsgNotifyType = LeaveMsgNotifyTypeEnum.EMAIL;

    // 留言提醒邮箱
    @Builder.Default
    private String leaveMsgNotifyEmail = BdConstants.EMPTY_STRING;

    @Builder.Default
    private String leaveMsgNotifyMobile = BdConstants.EMPTY_STRING;

    // @Builder.Default
    // private String leaveMsgNotifyWechat = BdConstants.EMPTY_STRING;

    // 留言提醒时间
    // @Builder.Default
    // private String leaveMsgNotifyTime = BdConstants.EMPTY_STRING;

    // 留言表单
    @Builder.Default
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    @JdbcTypeCode(SqlTypes.JSON)
    private String leaveMsgForm = BdConstants.EMPTY_JSON_STRING;

}
