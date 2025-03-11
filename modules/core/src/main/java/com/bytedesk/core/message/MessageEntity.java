/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-11 10:48:42
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.enums.ClientEnum;
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
@EntityListeners({ MessageEntityListener.class })
@Table(name = "bytedesk_core_message")
public class MessageEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Builder.Default
    @Column(name = "message_type", nullable = false)
    private String type = MessageTypeEnum.TEXT.name();

    // 仅对一对一/客服/技能组聊天有效，表示对方是否已读。群聊无效
    @Builder.Default
    private String status = MessageStatusEnum.SUCCESS.name();

    // 复杂类型可以使用json存储在此，通过type字段区分
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String content;

    // helpful/feedback 迁移至 MessageExtra

    // 是否是机器人
    @Builder.Default
    @Column(name = "is_robot", nullable = false)
    private boolean robot = false;

    // 是否是访客
    @Builder.Default
    @Column(name = "is_visitor", nullable = false)
    private boolean visitor = false;

    @Builder.Default
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String extra = BytedeskConsts.EMPTY_JSON_STRING;

    @Builder.Default
    // @Enumerated(EnumType.STRING)
    // private ClientEnum client;
    private String client = ClientEnum.WEB.name();

    /** message belongs to */
    private String threadTopic;

    /**
     * sender
     * 考虑到访客信息不存储在user表中，在visitor表中，此处使用json存储，加快查询速度，
     * 以空间换时间
     */
    // @ManyToOne(fetch = FetchType.EAGER)
    // private User user;
    // h2 db 不能使用 user, 所以重定义为 message_user
    @Builder.Default
    @Column(name = "message_user", length = BytedeskConsts.COLUMN_EXTRA_LENGTH)
    private String user = BytedeskConsts.EMPTY_JSON_STRING;

}
