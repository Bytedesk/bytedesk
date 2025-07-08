/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-22 16:11:42
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-08 15:56:11
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.message_leave;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.converter.StringListConverter;
import com.bytedesk.core.utils.BdDateUtils;

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

/**
 * Customer leave message entity
 * Represents customer messages left when agents are offline or unavailable
 * 
 * Database Table: bytedesk_service_message_leave
 * Purpose: Stores offline messages from customers when agents are unavailable
 * 
 * TODO: Convert leave messages to tickets
 */
@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_service_message_leave")
public class MessageLeaveEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Customer contact information such as email or phone number
     */
    private String contact;

    /**
     * The main content of the leave message
     */
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String content;

    /**
     * URLs of images attached to the leave message
     */
    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> images = new ArrayList<>();

    /**
     * URLs of file attachments for the leave message
     */
    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> attachments = new ArrayList<>();

    /**
     * Agent's reply content to the leave message
     */
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String reply;

    /**
     * URLs of images attached to the agent's reply
     */
    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> replyImages = new ArrayList<>();

    /**
     * URLs of file attachments for the agent's reply
     */
    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> replyAttachments = new ArrayList<>();

    /**
     * Timestamp when the agent replied to the leave message
     */
    private ZonedDateTime repliedAt;

    /**
     * Current status of the leave message (PENDING, REPLIED, CLOSED, etc.)
     */
    @Builder.Default
    private String status = MessageLeaveStatusEnum.PENDING.name();
    
    /**
     * Category UID for message classification (consultation, complaint, suggestion, etc.)
     */
    private String categoryUid;
    
    /**
     * Priority level of the leave message (LOW, MEDIUM, HIGH, URGENT)
     */
    @Builder.Default
    private String priority = MessageLeavePriorityEnum.LOW.name();

    /**
     * Associated ticket UID if leave message is converted to ticket
     */
    private String ticketUid;
    
    /**
     * Associated message UID for updating leave message status
     */
    private String messageUid;
    
    /**
     * Associated thread UID for the conversation
     */
    private String threadUid;
    
    // Associated conversation (commented out for now)
    // private String threadTopic;
    // private ThreadEntity thread;

    /**
     * Customer source channel such as website, mobile app, mini-program, etc.
     */
    private String client;
    
    /**
     * Customer device information including browser and app version
     */
    private String deviceInfo;
    
    /**
     * Customer's IP address when leaving the message
     */
    private String ipAddress;
    
    /**
     * Geographic location information of the customer
     */
    private String location;
    
    // Leave messages don't need satisfaction rating for now
    // Satisfaction rating (users can rate after processing)
    // private Integer satisfaction;
    
    /**
     * Tags for message categorization and search
     */
    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> tagList = new ArrayList<>();

    /**
     * Customer user information stored as JSON string
     */
    @Builder.Default
    @Column(name = "leavemsg_user", length = BytedeskConsts.COLUMN_EXTRA_LENGTH)
    private String user = BytedeskConsts.EMPTY_JSON_STRING;

    /**
     * Agent information who replied to the message stored as JSON string
     */
    @Builder.Default
    @Column(name = "reply_user", length = BytedeskConsts.COLUMN_EXTRA_LENGTH)
    private String replyUser = BytedeskConsts.EMPTY_JSON_STRING;


    public String getRepliedAtString() {
        return BdDateUtils.formatDatetimeToString(repliedAt);
    }
}
