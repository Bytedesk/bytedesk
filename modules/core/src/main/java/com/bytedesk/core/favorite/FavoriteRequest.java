/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:26:04
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-12 11:37:29
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.favorite;

import java.util.List;

import com.bytedesk.core.base.BaseRequestNoOrg;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteRequest extends BaseRequestNoOrg {

    /**
     * 收藏名称/标题
     */
    private String name;

    /**
     * 收藏类型：THREAD(会话收藏)、CUSTOMER(客户收藏)、MESSAGE(消息收藏)
     */
    private String favoriteType;

    /**
     * 收藏的消息内容
     */
    private String favoriteContent;

    /**
     * 消息类型：TEXT、IMAGE、FILE、AUDIO、VIDEO等
     */
    private String messageType;

    /**
     * 消息状态
     */
    private String messageStatus;

    /**
     * 消息发送人信息（JSON格式）
     */
    private String messageSender;

    /**
     * 消息接收人信息（JSON格式）
     */
    private String messageReceiver;

    /**
     * 会话信息（JSON格式）
     */
    private String threadInfo;

    /**
     * 消息来源渠道：WEB、ANDROID、IOS等
     */
    private String messageChannel;

    /**
     * 消息额外信息（JSON格式）
     */
    private String messageExtra;

    /**
     * 收藏的标签列表
     */
    private List<String> tagList;

    /**
     * 收藏描述/备注
     */
    private String description;

    /**
     * 收藏分类
     */
    private String category;

    /**
     * 是否置顶
     */
    private Boolean isPinned;

    /**
     * 收藏来源：手动收藏、自动收藏等
     */
    private String favoriteSource;

    /**
     * 原始消息ID（如果是从消息收藏的）
     */
    private String originalMessageUid;

    /**
     * 原始会话ID（如果是从会话收藏的）
     */
    private String originalThreadUid;

}
