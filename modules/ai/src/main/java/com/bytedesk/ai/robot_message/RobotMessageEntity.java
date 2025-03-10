/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-04 16:09:34
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-10 17:26:37
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot_message;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.enums.ClientEnum;
import com.bytedesk.core.message.MessageHelpfulEnum;
import com.bytedesk.core.message.MessageTypeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
// @EntityListeners({ MessageEntityListener.class })
@Table(name = "bytedesk_ai_robot_message")
public class RobotMessageEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Builder.Default
    // 如果使用int存储，enum中类型的顺序改变，会导致数据库中的数据类型改变，导致无法查询到数据
    // @Enumerated(EnumType.STRING) // 默认使用int类型表示，如果为了可读性，可以转换为使用字符串存储
    @Column(name = "message_type", nullable = false)
    private String type = MessageTypeEnum.TEXT.name();

    // 仅对一对一/客服/技能组聊天有效，表示对方是否已读。群聊无效
    // @Builder.Default
    // private String status = MessageStatusEnum.SUCCESS.name();

    // 复杂类型可以使用json存储在此，通过type字段区分
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String question;

    // answer
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String answer;

    // 有帮助、没帮助
    @Builder.Default
    private String helpful = MessageHelpfulEnum.HELPFUL.name();

    // 是否是机器人
    // @Builder.Default
    // @Column(name = "is_robot", nullable = false)
    // private boolean robot = false;

    // 是否是访客
    // @Builder.Default
    // @Column(name = "is_visitor", nullable = false)
    // private boolean visitor = false;


    @Builder.Default
    @Column(length = BytedeskConsts.COLUMN_EXTRA_LENGTH)
    private String extra = BytedeskConsts.EMPTY_JSON_STRING;

    @Builder.Default
    // @Enumerated(EnumType.STRING)
    // private ClientEnum client;
    private String client = ClientEnum.WEB.name();

    /** message belongs to */
    private String threadTopic;


    @Builder.Default
    @Column(name = "message_user", length = BytedeskConsts.COLUMN_EXTRA_LENGTH)
    private String user = BytedeskConsts.EMPTY_JSON_STRING;

    @Builder.Default
    @Column(name = "message_robot", length = BytedeskConsts.COLUMN_EXTRA_LENGTH)
    private String robot = BytedeskConsts.EMPTY_JSON_STRING;

}
