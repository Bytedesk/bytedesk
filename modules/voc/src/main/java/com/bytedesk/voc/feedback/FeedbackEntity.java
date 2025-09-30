/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:14:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-15 16:37:02
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.voc.feedback;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.converter.StringListConverter;
import com.bytedesk.core.enums.PriorityEnum;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.utils.BdDateUtils;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * Feedback entity for collecting and managing user feedback
 * Supports different types of feedback from various sources
 * 
 * Database Table: bytedesk_voc_feedback
 * Purpose: Stores feedback from customers, agents, workgroups and other sources
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({FeedbackEntityListener.class})
@Table(name = "bytedesk_voc_feedback")
public class FeedbackEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;


    /**
     * Feedback title/subject
     */
    private String title;
    
    /**
     * Feedback type - WORKGROUP, AGENT, CUSTOMER, etc.
     */
    @Builder.Default
    @Column(name = "feedback_type")
    private String type = FeedbackTypeEnum.GENERAL.name();

    /**
     * Main feedback content
     */
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String content;

    /**
     * Feedback processing status
     */
    @Builder.Default
    @Column(name = "feedback_status")
    private String status = FeedbackStatusEnum.PENDING.name();

    /**
     * Priority level of the feedback
     */
    @Builder.Default
    @Column(name = "feedback_priority")
    private String priority = PriorityEnum.MEDIUM.name();

    /**
     * Feedback rating/score (1-5 stars)
     */
    private Integer rating;

    /**
     * List of image URLs
     */
    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> images = new ArrayList<>();

    /**
     * List of attachment URLs
     */
    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> attachments = new ArrayList<>();

    /**
     * Category UIDs for classification
     */
    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> categoryUids = new ArrayList<>();

    /**
     * Tags for categorization and search
     */
    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> tagList = new ArrayList<>();

    /**
     * Associated thread UID if feedback is related to a conversation
     */
    private String threadUid;

    /**
     * Associated message UID if feedback is related to a specific message
     */
    private String messageUid;

    /**
     * Associated ticket UID if feedback is converted to or related to a ticket
     */
    private String ticketUid;

    /**
     * Customer/user information who submitted the feedback (stored as JSON)
     */
    @Builder.Default
    @Column(name = "feedback_user", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String user = BytedeskConsts.EMPTY_JSON_STRING;

    /**
     * Agent information who is handling the feedback (stored as JSON)
     */
    @Builder.Default
    @Column(name = "feedback_agent", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String agent = BytedeskConsts.EMPTY_JSON_STRING;

    /**
     * Workgroup information if feedback is related to a workgroup (stored as JSON)
     */
    @Builder.Default
    @Column(name = "feedback_workgroup", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String workgroup = BytedeskConsts.EMPTY_JSON_STRING;

    /**
     * Robot information if feedback is related to robot interaction (stored as JSON)
     */
    @Builder.Default
    @Column(name = "feedback_robot", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String robot = BytedeskConsts.EMPTY_JSON_STRING;

    /**
     * Agent's reply to the feedback
     */
    @Column(name = "reply_content", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String replyContent;

    /**
     * Images attached to the reply
     */
    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(name = "reply_images", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> replyImages = new ArrayList<>();

    /**
     * Files attached to the reply
     */
    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(name = "reply_attachments", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> replyAttachments = new ArrayList<>();

    /**
     * Agent who replied to the feedback (stored as JSON)
     */
    @Builder.Default
    @Column(name = "reply_user", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String replyUser = BytedeskConsts.EMPTY_JSON_STRING;

    /**
     * Timestamp when the feedback was replied to
     */
    private ZonedDateTime repliedAt;

    /**
     * Agent who read the feedback (stored as JSON)
     */
    @Builder.Default
    @Column(name = "read_user", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String readUser = BytedeskConsts.EMPTY_JSON_STRING;

    /**
     * Timestamp when the feedback was read
     */
    private ZonedDateTime readAt;

    /**
     * Agent who transferred the feedback (stored as JSON)
     */
    @Builder.Default
    @Column(name = "transfer_user", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String transferUser = BytedeskConsts.EMPTY_JSON_STRING;

    /**
     * Target agent UID for transfer
     */
    private String targetAgentUid;

    /**
     * Timestamp when the feedback was transferred
     */
    private ZonedDateTime transferredAt;

    /**
     * Agent who closed the feedback (stored as JSON)
     */
    @Builder.Default
    @Column(name = "close_user", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String closeUser = BytedeskConsts.EMPTY_JSON_STRING;

    /**
     * Timestamp when the feedback was closed
     */
    private ZonedDateTime closedAt;

    /**
     * Agent who resolved the feedback (stored as JSON)
     */
    @Builder.Default
    @Column(name = "resolve_user", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String resolveUser = BytedeskConsts.EMPTY_JSON_STRING;

    /**
     * Timestamp when the feedback was resolved
     */
    private ZonedDateTime resolvedAt;

    /**
     * Customer source channel
     */
    private String channel;

    /**
     * Customer device information
     */
    private String deviceInfo;

    /**
     * Customer's IP address
     */
    private String ipAddress;

    /**
     * Geographic location information
     */
    private String location;

    /**
     * Additional metadata stored as JSON
     */
    @Builder.Default
    @Column(name = "feedback_extra", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String extra = BytedeskConsts.EMPTY_JSON_STRING;

    // Type checking methods
    public Boolean isWorkgroupType() {
        return FeedbackTypeEnum.WORKGROUP.name().equals(getType());
    }

    public Boolean isAgentType() {
        return FeedbackTypeEnum.AGENT.name().equals(getType());
    }

    public Boolean isRobotType() {
        return FeedbackTypeEnum.ROBOT.name().equals(getType());
    }

    public Boolean isSystemType() {
        return FeedbackTypeEnum.SYSTEM.name().equals(getType());
    }

    public Boolean isGeneralType() {
        return FeedbackTypeEnum.GENERAL.name().equals(getType());
    }

    // Status checking methods
    public Boolean isPending() {
        return FeedbackStatusEnum.PENDING.name().equals(getStatus());
    }

    public Boolean isProcessing() {
        return FeedbackStatusEnum.PROCESSING.name().equals(getStatus());
    }

    public Boolean isRead() {
        return FeedbackStatusEnum.READ.name().equals(getStatus());
    }

    public Boolean isReplied() {
        return FeedbackStatusEnum.REPLIED.name().equals(getStatus());
    }

    public Boolean isTransferred() {
        return FeedbackStatusEnum.TRANSFERRED.name().equals(getStatus());
    }

    public Boolean isClosed() {
        return FeedbackStatusEnum.CLOSED.name().equals(getStatus());
    }

    public Boolean isResolved() {
        return FeedbackStatusEnum.RESOLVED.name().equals(getStatus());
    }

    public Boolean isRejected() {
        return FeedbackStatusEnum.REJECTED.name().equals(getStatus());
    }

    public Boolean isSpam() {
        return FeedbackStatusEnum.SPAM.name().equals(getStatus());
    }

    public Boolean isConfirmed() {
        return FeedbackStatusEnum.CONFIRMED.name().equals(getStatus());
    }

    // Status setting methods
    public FeedbackEntity setProcessing() {
        setStatus(FeedbackStatusEnum.PROCESSING.name());
        return this;
    }

    public FeedbackEntity setRead() {
        setStatus(FeedbackStatusEnum.READ.name());
        return this;
    }

    public FeedbackEntity setReplied() {
        setStatus(FeedbackStatusEnum.REPLIED.name());
        return this;
    }

    public FeedbackEntity setTransferred() {
        setStatus(FeedbackStatusEnum.TRANSFERRED.name());
        return this;
    }

    public FeedbackEntity setClosed() {
        setStatus(FeedbackStatusEnum.CLOSED.name());
        return this;
    }

    public FeedbackEntity setResolved() {
        setStatus(FeedbackStatusEnum.RESOLVED.name());
        return this;
    }

    public FeedbackEntity setRejected() {
        setStatus(FeedbackStatusEnum.REJECTED.name());
        return this;
    }

    public FeedbackEntity setSpam() {
        setStatus(FeedbackStatusEnum.SPAM.name());
        return this;
    }

    public FeedbackEntity setConfirmed() {
        setStatus(FeedbackStatusEnum.CONFIRMED.name());
        return this;
    }

    // JSON object retrieval methods
    public UserProtobuf getUserProtobuf() {
        return JSON.parseObject(getUser(), UserProtobuf.class);
    }

    public UserProtobuf getAgentProtobuf() {
        return JSON.parseObject(getAgent(), UserProtobuf.class);
    }

    public UserProtobuf getWorkgroupProtobuf() {
        return JSON.parseObject(getWorkgroup(), UserProtobuf.class);
    }

    public UserProtobuf getRobotProtobuf() {
        return JSON.parseObject(getRobot(), UserProtobuf.class);
    }

    public UserProtobuf getReplyUserProtobuf() {
        return JSON.parseObject(getReplyUser(), UserProtobuf.class);
    }

    public UserProtobuf getReadUserProtobuf() {
        return JSON.parseObject(getReadUser(), UserProtobuf.class);
    }

    public UserProtobuf getTransferUserProtobuf() {
        return JSON.parseObject(getTransferUser(), UserProtobuf.class);
    }

    public UserProtobuf getCloseUserProtobuf() {
        return JSON.parseObject(getCloseUser(), UserProtobuf.class);
    }

    public UserProtobuf getResolveUserProtobuf() {
        return JSON.parseObject(getResolveUser(), UserProtobuf.class);
    }

    // Utility methods for date formatting
    public String getRepliedAtString() {
        return BdDateUtils.formatDatetimeToString(repliedAt);
    }

    public String getReadAtString() {
        return BdDateUtils.formatDatetimeToString(readAt);
    }

    public String getTransferredAtString() {
        return BdDateUtils.formatDatetimeToString(transferredAt);
    }

    public String getClosedAtString() {
        return BdDateUtils.formatDatetimeToString(closedAt);
    }

    public String getResolvedAtString() {
        return BdDateUtils.formatDatetimeToString(resolvedAt);
    }

    // Business logic methods
    public Boolean hasReply() {
        return replyContent != null && !replyContent.trim().isEmpty();
    }

    public Boolean hasImages() {
        return images != null && !images.isEmpty();
    }

    public Boolean hasAttachments() {
        return attachments != null && !attachments.isEmpty();
    }

    public Boolean hasReplyImages() {
        return replyImages != null && !replyImages.isEmpty();
    }

    public Boolean hasReplyAttachments() {
        return replyAttachments != null && !replyAttachments.isEmpty();
    }

    public Boolean isHighPriority() {
        return getPriorityEnum().isHighPriority();
    }

    public Boolean isLowPriority() {
        return getPriorityEnum().isLowPriority();
    }

    public Boolean isUrgent() {
        return getPriorityEnum().isUrgent();
    }

    public PriorityEnum getPriorityEnum() {
        return PriorityEnum.fromValue(priority);
    }

    public void setPriorityEnum(PriorityEnum priorityEnum) {
        this.priority = priorityEnum.name();
    }

    public Boolean hasGoodRating() {
        return rating != null && rating >= 4;
    }

    public Boolean hasPoorRating() {
        return rating != null && rating <= 2;
    }

}
