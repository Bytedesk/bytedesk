/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:14:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-04 15:35:31
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.task;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.I18Consts;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.ZonedDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
// import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 待办任务：
 * 一个工单内可以关联多个任务，每个人都可以创建待办任务
 * 可以单纯创建待办，也可以关联工单等创建待办任务
 * 
 * Task entity for content categorization and organization
 * Provides taskging functionality for various system entities
 * 
 * Database Table: bytedesk_core_task
 * Purpose: Stores task definitions, colors, and organization settings
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
// @EntityListeners({TaskEntityListener.class})
@Table(
    name = "bytedesk_core_task",
    indexes = {
        @Index(name = "idx_task_uid", columnList = "uuid"),
        @Index(name = "idx_task_org_uid", columnList = "org_uid"),
        @Index(name = "idx_task_user_uid", columnList = "user_uid"),
        @Index(name = "idx_task_type", columnList = "task_type"),
        @Index(name = "idx_task_list_uid", columnList = "task_list_uid"),
        @Index(name = "idx_task_due_at", columnList = "due_at")
    }
)
public class TaskEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Name of the task
     */
    @Column(length = 128)
    private String name;

    /**
     * Description of the task
     */
    @Builder.Default
    @Column(length = 2048)
    private String description = I18Consts.I18N_DESCRIPTION;

    /**
     * Type of task (CUSTOMER, TICKET, ARTICLE, etc.)
     */
    @Builder.Default
    @Column(name = "task_type")
    private String type = TaskTypeEnum.CUSTOMER.name();

    /**
     * Color theme for the task display
     */
    @Builder.Default
    @Column(name = "task_color", length = 32)
    private String color = "blue";

    /**
     * Display order within the list
     */
    @Builder.Default
    @Column(name = "task_order")
    private Integer order = 0;

    /**
     * Task list uid that this task belongs to (optional)
     */
    @Column(name = "task_list_uid", length = 64)
    private String taskListUid;

    /**
     * Status (TODO, IN_PROGRESS, DONE, etc.)
     */
    @Builder.Default
    @Column(name = "task_status", length = 32)
    private String status = "TODO";

    /**
     * Priority (LOW, MEDIUM, HIGH, URGENT)
     */
    @Builder.Default
    @Column(name = "task_priority", length = 32)
    private String priority = "MEDIUM";

    /**
     * Optional planned start time
     */
    @Column(name = "start_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    private ZonedDateTime startAt;

    /**
     * Optional due time
     */
    @Column(name = "due_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    private ZonedDateTime dueAt;

    /**
     * Completion time
     */
    @Column(name = "completed_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    private ZonedDateTime completedAt;

}
