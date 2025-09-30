/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:14:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-12 10:17:46
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kanban.task;

import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.converter.StringListConverter;
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
 * Task entity for Kanban board task management
 * Represents individual tasks within a Kanban board system
 * 
 * Database Table: bytedesk_plugin_kanban_task
 * Purpose: Stores task information, status, and organization within Kanban boards
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({TaskEntityListener.class})
@Table(name = "bytedesk_plugin_kanban_task")
public class TaskEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;


    /**
     * Name or title of the task
     */
    private String name;

    /**
     * Description of the task
     */
    @Builder.Default
    private String description = I18Consts.I18N_DESCRIPTION;

    /**
     * Type of task (CUSTOMER, PROJECT, etc.)
     */
    @Builder.Default
    @Column(name = "task_type")
    private String type = TaskTypeEnum.CUSTOMER.name();

    /**
     * Color theme for the task display
     */
    @Builder.Default
    @Column(name = "task_color")
    private String color = "red";

    /**
     * Display order of the task within its todo list
     */
    @Builder.Default
    @Column(name = "task_order")
    private int order = 0;

    /**
     * Tags for task categorization and search
     */
    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> tagList = new ArrayList<>();

    /**
     * Whether the task has been completed
     */
    @Builder.Default
    @Column(name = "task_complete")
    private boolean complete = false;

    /**
     * Associated project UID for project-specific tasks
     */
    private String projectUid;

    /**
     * Associated module UID for module-specific tasks
     */
    private String moduleUid;

    /**
     * Associated todo list UID where this task belongs
     */
    private String todoListUid;

}
