/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:14:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-15 14:01:25
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.voc.complaint;

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
 * Complaint entity for collecting and managing user complaints
 * Supports different types of complaints from various sources
 * 
 * Database Table: bytedesk_voc_complaint
 * Purpose: Stores complaints from customers, agents, workgroups and other sources
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({ComplaintEntityListener.class})
@Table(name = "bytedesk_voc_complaint")
public class ComplaintEntity extends BaseEntity {

    /**
     * Complaint title/subject
     */
    private String title;
    
    /**
     * Complaint type - SERVICE, PRODUCT, AGENT, etc.
     */
    @Builder.Default
    @Column(name = "complaint_type")
    private String type = ComplaintTypeEnum.GENERAL.name();

    /**
     * Main complaint content
     */
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String content;

    /**
     * Complaint processing status
     */
    @Builder.Default
    @Column(name = "complaint_status")
    private String status = ComplaintStatusEnum.PENDING.name();

    /**
     * Priority level of the complaint
     */
    @Builder.Default
    @Column(name = "complaint_priority")
    private String priority = PriorityEnum.MEDIUM.name();

    /**
     * Complaint severity level (1-5)
     */
    private Integer severity;

    /**
     * Associated image URL
     */
    private String imageUrl;

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
     * Associated thread UID if complaint is related to a conversation
     */
    private String threadUid;

    /**
     * Associated message UID if complaint is related to a specific message
     */
    private String messageUid;

    /**
     * Associated ticket UID if complaint is converted to or related to a ticket
     */
    private String ticketUid;

    /**
     * Customer/user information who submitted the complaint (stored as JSON)
     */
    @Builder.Default
    @Column(name = "complaint_user", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String user = BytedeskConsts.EMPTY_JSON_STRING;

    /**
     * Agent information who is handling the complaint (stored as JSON)
     */
    @Builder.Default
    @Column(name = "complaint_agent", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String agent = BytedeskConsts.EMPTY_JSON_STRING;

    /**
     * Workgroup information if complaint is related to a workgroup (stored as JSON)
     */
    @Builder.Default
    @Column(name = "complaint_workgroup", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String workgroup = BytedeskConsts.EMPTY_JSON_STRING;

    /**
     * Agent's reply to the complaint
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
     * Agent who replied to the complaint (stored as JSON)
     */
    @Builder.Default
    @Column(name = "reply_user", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String replyUser = BytedeskConsts.EMPTY_JSON_STRING;

    /**
     * Timestamp when the complaint was replied to
     */
    private ZonedDateTime repliedAt;

    /**
     * Agent who read the complaint (stored as JSON)
     */
    @Builder.Default
    @Column(name = "read_user", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String readUser = BytedeskConsts.EMPTY_JSON_STRING;

    /**
     * Timestamp when the complaint was read
     */
    private ZonedDateTime readAt;

    /**
     * Agent who transferred the complaint (stored as JSON)
     */
    @Builder.Default
    @Column(name = "transfer_user", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String transferUser = BytedeskConsts.EMPTY_JSON_STRING;

    /**
     * Target agent UID for transfer
     */
    private String targetAgentUid;

    /**
     * Timestamp when the complaint was transferred
     */
    private ZonedDateTime transferredAt;

    /**
     * Agent who closed the complaint (stored as JSON)
     */
    @Builder.Default
    @Column(name = "close_user", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String closeUser = BytedeskConsts.EMPTY_JSON_STRING;

    /**
     * Timestamp when the complaint was closed
     */
    private ZonedDateTime closedAt;

    /**
     * Agent who resolved the complaint (stored as JSON)
     */
    @Builder.Default
    @Column(name = "resolve_user", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String resolveUser = BytedeskConsts.EMPTY_JSON_STRING;

    /**
     * Timestamp when the complaint was resolved
     */
    private ZonedDateTime resolvedAt;

    /**
     * Investigation notes and findings
     */
    @Column(name = "investigation_notes", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String investigationNotes;

    /**
     * Resolution description
     */
    @Column(name = "resolution_description", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String resolutionDescription;

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
    @Column(name = "complaint_extra", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String extra = BytedeskConsts.EMPTY_JSON_STRING;

    // Type checking methods
    public Boolean isServiceType() {
        return ComplaintTypeEnum.SERVICE.name().equals(type);
    }

    public Boolean isProductType() {
        return ComplaintTypeEnum.PRODUCT.name().equals(type);
    }

    public Boolean isAgentType() {
        return ComplaintTypeEnum.AGENT.name().equals(type);
    }

    public Boolean isWorkgroupType() {
        return ComplaintTypeEnum.WORKGROUP.name().equals(type);
    }

    public Boolean isSystemType() {
        return ComplaintTypeEnum.SYSTEM.name().equals(type);
    }

    public Boolean isBillingType() {
        return ComplaintTypeEnum.BILLING.name().equals(type);
    }

    public Boolean isTechnicalType() {
        return ComplaintTypeEnum.TECHNICAL.name().equals(type);
    }

    public Boolean isPrivacyType() {
        return ComplaintTypeEnum.PRIVACY.name().equals(type);
    }

    public Boolean isGeneralType() {
        return ComplaintTypeEnum.GENERAL.name().equals(type);
    }

    // Status checking methods
    public Boolean isPending() {
        return ComplaintStatusEnum.PENDING.name().equals(status);
    }

    public Boolean isProcessing() {
        return ComplaintStatusEnum.PROCESSING.name().equals(status);
    }

    public Boolean isInvestigating() {
        return ComplaintStatusEnum.INVESTIGATING.name().equals(status);
    }

    public Boolean isRead() {
        return ComplaintStatusEnum.READ.name().equals(status);
    }

    public Boolean isReplied() {
        return ComplaintStatusEnum.REPLIED.name().equals(status);
    }

    public Boolean isTransferred() {
        return ComplaintStatusEnum.TRANSFERRED.name().equals(status);
    }

    public Boolean isEscalated() {
        return ComplaintStatusEnum.ESCALATED.name().equals(status);
    }

    public Boolean isClosed() {
        return ComplaintStatusEnum.CLOSED.name().equals(status);
    }

    public Boolean isResolved() {
        return ComplaintStatusEnum.RESOLVED.name().equals(status);
    }

    public Boolean isRejected() {
        return ComplaintStatusEnum.REJECTED.name().equals(status);
    }

    public Boolean isSpam() {
        return ComplaintStatusEnum.SPAM.name().equals(status);
    }

    public Boolean isInvalid() {
        return ComplaintStatusEnum.INVALID.name().equals(status);
    }

    public Boolean isConfirmed() {
        return ComplaintStatusEnum.CONFIRMED.name().equals(status);
    }

    public Boolean isCancelled() {
        return ComplaintStatusEnum.CANCELLED.name().equals(status);
    }

    public Boolean isWithdrawn() {
        return ComplaintStatusEnum.WITHDRAWN.name().equals(status);
    }

    // Status setting methods
    public ComplaintEntity setProcessing() {
        this.status = ComplaintStatusEnum.PROCESSING.name();
        return this;
    }

    public ComplaintEntity setInvestigating() {
        this.status = ComplaintStatusEnum.INVESTIGATING.name();
        return this;
    }

    public ComplaintEntity setRead() {
        this.status = ComplaintStatusEnum.READ.name();
        this.readAt = BdDateUtils.now();
        return this;
    }

    public ComplaintEntity setReplied() {
        this.status = ComplaintStatusEnum.REPLIED.name();
        this.repliedAt = BdDateUtils.now();
        return this;
    }

    public ComplaintEntity setTransferred() {
        this.status = ComplaintStatusEnum.TRANSFERRED.name();
        this.transferredAt = BdDateUtils.now();
        return this;
    }

    public ComplaintEntity setEscalated() {
        this.status = ComplaintStatusEnum.ESCALATED.name();
        return this;
    }

    public ComplaintEntity setClosed() {
        this.status = ComplaintStatusEnum.CLOSED.name();
        this.closedAt = BdDateUtils.now();
        return this;
    }

    public ComplaintEntity setResolved() {
        this.status = ComplaintStatusEnum.RESOLVED.name();
        this.resolvedAt = BdDateUtils.now();
        return this;
    }

    public ComplaintEntity setRejected() {
        this.status = ComplaintStatusEnum.REJECTED.name();
        return this;
    }

    public ComplaintEntity setSpam() {
        this.status = ComplaintStatusEnum.SPAM.name();
        return this;
    }

    public ComplaintEntity setInvalid() {
        this.status = ComplaintStatusEnum.INVALID.name();
        return this;
    }

    public ComplaintEntity setConfirmed() {
        this.status = ComplaintStatusEnum.CONFIRMED.name();
        return this;
    }

    public ComplaintEntity setCancelled() {
        this.status = ComplaintStatusEnum.CANCELLED.name();
        return this;
    }

    public ComplaintEntity setWithdrawn() {
        this.status = ComplaintStatusEnum.WITHDRAWN.name();
        return this;
    }

    // JSON object retrieval methods
    public UserProtobuf getUserProtobuf() {
        return JSON.parseObject(user, UserProtobuf.class);
    }

    public UserProtobuf getAgentProtobuf() {
        return JSON.parseObject(agent, UserProtobuf.class);
    }

    public UserProtobuf getWorkgroupProtobuf() {
        return JSON.parseObject(workgroup, UserProtobuf.class);
    }

    public UserProtobuf getReplyUserProtobuf() {
        return JSON.parseObject(replyUser, UserProtobuf.class);
    }

    public UserProtobuf getReadUserProtobuf() {
        return JSON.parseObject(readUser, UserProtobuf.class);
    }

    public UserProtobuf getTransferUserProtobuf() {
        return JSON.parseObject(transferUser, UserProtobuf.class);
    }

    public UserProtobuf getCloseUserProtobuf() {
        return JSON.parseObject(closeUser, UserProtobuf.class);
    }

    public UserProtobuf getResolveUserProtobuf() {
        return JSON.parseObject(resolveUser, UserProtobuf.class);
    }
}
