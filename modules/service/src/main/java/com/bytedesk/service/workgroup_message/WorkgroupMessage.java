/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-01 11:32:30
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-18 11:33:40
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.workgroup_message;

/**
 * 分表存储技能组客服消息
 * TODO: 同步message中客服消息，包括uid。用于查询技能组消息，减少message表压力
 */
// @Entity
// @Data
// @Builder
// @Accessors(chain = true)
// @EqualsAndHashCode(callSuper = false)
// @AllArgsConstructor
// @NoArgsConstructor
// @Table(name = "bytedesk_service_workgroup_message")
// public class WorkgroupMessage extends BaseEntity {

//     private static final long serialVersionUID = 1L;

//     @Builder.Default
//     // 如果使用int存储，enum中类型的顺序改变，会导致数据库中的数据类型改变，导致无法查询到数据
//     // @Enumerated(EnumType.STRING) // 默认使用int类型表示，如果为了可读性，可以转换为使用字符串存储
//     @Column(name = "message_type", nullable = false)
//     // private MessageTypeEnum type = MessageTypeEnum.TEXT;
//     private String type = MessageTypeEnum.TEXT.name();

//     // 复杂类型可以使用json存储在此，通过type字段区分
//     @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
//     private String content;

//     @Builder.Default
//     @Column(columnDefinition = TypeConsts.COLUMN_TYPE_JSONB)
//     @JdbcTypeCode(SqlTypes.JSON)
//     private String extra = BytedeskConsts.EMPTY_JSON_STRING;

//     // 仅对一对一/客服/技能组聊天有效，表示对方是否已读。群聊无效
//     @Builder.Default
//     private String status = MessageStatusEnum.SUCCESS.name();

//     @Builder.Default
//     // @Enumerated(EnumType.STRING)
//     // private ClientEnum client;
//     private String client = ClientEnum.WEB.name();

//     /** message belongs to */
//     private String threadTopic;

//     /**
//      * sender
//      * 考虑到访客信息不存储在user表中，在visitor表中，此处使用json存储，加快查询速度，
//      * 以空间换时间
//      */
//     // @ManyToOne(fetch = FetchType.EAGER)
//     // private User user;
//     // h2 db 不能使用 user, 所以重定义为 message_user
//     @Builder.Default
//     @Column(name = "message_user", columnDefinition = TypeConsts.COLUMN_TYPE_JSONB)
//     @JdbcTypeCode(SqlTypes.JSON)
//     private String user = BytedeskConsts.EMPTY_JSON_STRING;
// }
