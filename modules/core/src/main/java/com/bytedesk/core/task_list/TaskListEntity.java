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
package com.bytedesk.core.task_list;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.I18Consts;
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
 * 待办任务列表
 * 
 * TaskList entity for content categorization and organization
 * Provides task_listging functionality for various system entities
 * 
 * Database Table: bytedesk_core_task_list
 * Purpose: Stores task_list definitions, colors, and organization settings
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
// @EntityListeners({TaskListEntityListener.class})
@Table(
    name = "bytedesk_core_task_list",
    indexes = {
        @Index(name = "idx_task_list_uid", columnList = "uuid"),
        @Index(name = "idx_task_list_org_uid", columnList = "org_uid"),
        @Index(name = "idx_task_list_user_uid", columnList = "user_uid"),
        @Index(name = "idx_task_list_type", columnList = "task_list_type")
    }
)
public class TaskListEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Name of the task_list
     */
    @Column(length = 128)
    private String name;

    /**
     * Description of the task_list
     */
    @Builder.Default
    @Column(length = 512)
    private String description = I18Consts.I18N_DESCRIPTION;

    /**
     * Type of task_list (TASK, etc.)
     */
    @Builder.Default
    @Column(name = "task_list_type")
    private String type = TaskListTypeEnum.TASK.name();

    /**
     * Color theme for the task list display
     */
    @Builder.Default
    @Column(name = "task_list_color", length = 32)
    private String color = "blue";

    /**
     * Display order
     */
    @Builder.Default
    @Column(name = "task_list_order")
    private Integer order = 0;

    /**
     * Whether this list is archived (hidden from default views)
     */
    @Builder.Default
    @Column(name = "is_archived")
    private Boolean archived = false;

}
