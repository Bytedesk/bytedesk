/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:14:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-12 11:01:12
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.favorite;

import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.converter.StringListConverter;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.message.MessageStatusEnum;
import com.bytedesk.core.enums.ChannelEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_core_favorite")
public class FavoriteEntity extends BaseEntity {

    /**
     * 收藏名称/标题
     */
    private String name;

    /**
     * 收藏类型：THREAD(会话收藏)、CUSTOMER(客户收藏)、MESSAGE(消息收藏)
     */
    @Builder.Default
    @Column(name = "favorite_type")
    private String type = FavoriteTypeEnum.MESSAGE.name();

    /**
     * 收藏的消息内容
     */
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String content;

    /**
     * 消息类型：TEXT、IMAGE、FILE、AUDIO、VIDEO等
     */
    @Builder.Default
    @Column(name = "message_type")
    private String messageType = MessageTypeEnum.TEXT.name();

    /**
     * 消息状态
     */
    @Builder.Default
    @Column(name = "message_status")
    private String messageStatus = MessageStatusEnum.SUCCESS.name();

    /**
     * 消息发送人信息（JSON格式）
     */
    @Builder.Default
    @Column(name = "message_sender", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String messageSender = BytedeskConsts.EMPTY_JSON_STRING;

    /**
     * 消息接收人信息（JSON格式）
     */
    @Builder.Default
    @Column(name = "message_receiver", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String messageReceiver = BytedeskConsts.EMPTY_JSON_STRING;

    /**
     * 会话信息（JSON格式）
     */
    @Builder.Default
    @Column(name = "thread_info", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String threadInfo = BytedeskConsts.EMPTY_JSON_STRING;

    /**
     * 消息来源渠道：WEB、ANDROID、IOS等
     */
    @Builder.Default
    @Column(name = "message_channel")
    private String messageChannel = ChannelEnum.WEB.name();

    /**
     * 消息额外信息（JSON格式）
     */
    @Builder.Default
    @Column(name = "message_extra", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String messageExtra = BytedeskConsts.EMPTY_JSON_STRING;

    /**
     * 收藏的标签列表
     */
    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> tagList = new ArrayList<>();

    /**
     * 收藏描述/备注
     */
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String description;

    /**
     * 收藏分类
     */
    private String category;

    /**
     * 是否置顶
     */
    @Builder.Default
    @Column(name = "is_pinned")
    private Boolean isPinned = false;

    /**
     * 收藏来源：手动收藏、自动收藏等
     */
    @Builder.Default
    @Column(name = "favorite_source")
    private String favoriteSource = FavoriteSourceEnum.MANUAL.name();

    /**
     * 原始消息ID（如果是从消息收藏的）
     */
    @Column(name = "original_message_uid")
    private String originalMessageUid;

    /**
     * 原始会话ID（如果是从会话收藏的）
     */
    @Column(name = "original_thread_uid")
    private String originalThreadUid;

}
