/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-22 16:12:30
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-03 17:14:56
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.quick_reply;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.base.BaseContent;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.converter.StringListConverter;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.message.content.ArticleContent;
import com.bytedesk.core.message.content.AudioContent;
import com.bytedesk.core.message.content.DocumentContent;
import com.bytedesk.core.message.content.EmailAddressContent;
import com.bytedesk.core.message.content.EmailContent;
import com.bytedesk.core.message.content.FaqContent;
import com.bytedesk.core.message.content.FileContent;
import com.bytedesk.core.message.content.FormContent;
import com.bytedesk.core.message.content.GoodsContent;
import com.bytedesk.core.message.content.ImageContent;
import com.bytedesk.core.message.content.LinkContent;
import com.bytedesk.core.message.content.LocationContent;
import com.bytedesk.core.message.content.MusicContent;
import com.bytedesk.core.message.content.OrderContent;
import com.bytedesk.core.message.content.PhoneNumberContent;
import com.bytedesk.core.message.content.UrlContent;
import com.bytedesk.core.message.content.VideoContent;
import com.bytedesk.core.message.content.VoiceContent;
import com.bytedesk.core.message.content.WechatNumberContent;

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

/**
 * 常用语-快捷回复
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_kbase_quick_reply")
public class QuickReplyEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String title;

    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String content;
    
    // 快捷键
    private String shortCut;

    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> tagList = new ArrayList<>();

    @Builder.Default
    @Column(name = "is_enabled")
    private Boolean enabled = true;

    @Builder.Default
    @Column(name = "reply_type")
    private String type = MessageTypeEnum.TEXT.name();

    // 被点击次数
    @Builder.Default
    private Integer clickCount = 0;

    // 有效开始日期
    private ZonedDateTime startDate;

    // 有效结束日期
    private ZonedDateTime endDate;

    private String categoryUid; // 文章分类

    private String kbUid; // 对应知识库

    // 某人工客服快捷回复知识库
    private String agentUid;

    // elastic 索引状态 (ArticleStatusEnum: PENDING, PROCESSING, SUCCESS, ERROR)
    private String elasticStatus;

    // 向量索引状态 (ArticleStatusEnum: PENDING, PROCESSING, SUCCESS, ERROR)
    private String vectorStatus;

    /**
     * Parse the stored `content` string into a typed object according to `type`.
     *
     * - For TEXT: returns raw String
     * - For structured types: returns corresponding BaseContent subclass
     */
    public Object getContentObject() {
        MessageTypeEnum messageType = MessageTypeEnum.fromValue(this.type);
        switch (messageType) {
            case TEXT:
                return this.content;
            case FAQ:
                return BaseContent.fromJson(this.content, FaqContent.class);
            case ARTICLE:
                return BaseContent.fromJson(this.content, ArticleContent.class);
            case URL:
                return BaseContent.fromJson(this.content, UrlContent.class);
            case LINK:
                return BaseContent.fromJson(this.content, LinkContent.class);
            case IMAGE:
                return BaseContent.fromJson(this.content, ImageContent.class);
            case FILE:
                return BaseContent.fromJson(this.content, FileContent.class);
            case DOCUMENT:
                return BaseContent.fromJson(this.content, DocumentContent.class);
            case AUDIO:
                return BaseContent.fromJson(this.content, AudioContent.class);
            case VOICE:
                return BaseContent.fromJson(this.content, VoiceContent.class);
            case VIDEO:
                return BaseContent.fromJson(this.content, VideoContent.class);
            case MUSIC:
                return BaseContent.fromJson(this.content, MusicContent.class);
            case LOCATION:
                return BaseContent.fromJson(this.content, LocationContent.class);
            case GOODS:
                return BaseContent.fromJson(this.content, GoodsContent.class);
            case ORDER:
                return BaseContent.fromJson(this.content, OrderContent.class);
            case PHONE_NUMBER:
                return BaseContent.fromJson(this.content, PhoneNumberContent.class);
            case EMAILL_ADDRESS:
                return BaseContent.fromJson(this.content, EmailAddressContent.class);
            case WECHAT_NUMBER:
                return BaseContent.fromJson(this.content, WechatNumberContent.class);
            case FORM:
                return BaseContent.fromJson(this.content, FormContent.class);
            case EMAIL:
                return BaseContent.fromJson(this.content, EmailContent.class);
            default:
                return this.content;
        }
    }

    public <T extends BaseContent> T getContentAs(Class<T> clazz) {
        return BaseContent.fromJson(this.content, clazz);
    }

    public void setContentObject(Object contentObject) {
        if (contentObject == null) {
            this.content = null;
            return;
        }
        if (contentObject instanceof BaseContent bc) {
            this.content = bc.toJson();
            return;
        }
        if (contentObject instanceof String s) {
            this.content = s;
            return;
        }
        // Fallback: store as JSON
        this.content = com.alibaba.fastjson2.JSON.toJSONString(contentObject);
    }

}
