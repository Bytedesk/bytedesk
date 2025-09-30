/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:14:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-12 10:17:42
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kanban.todo_list;

import java.util.List;
import java.util.ArrayList;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.kanban.task.TaskEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * Todo list entity for Kanban board management
 * Represents a column or list in a Kanban board for organizing tasks
 * 
 * Database Table: bytedesk_plugin_kanban_todo_list
 * Purpose: Stores todo list configurations, task organization, and board structure
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({TodoListEntityListener.class})
@Table(name = "bytedesk_plugin_kanban_todo_list")
public class TodoListEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;


    /**
     * Name of the todo list
     */
    private String name;

    /**
     * Description of the todo list
     */
    @Builder.Default
    private String description = I18Consts.I18N_DESCRIPTION;

    /**
     * Type of todo list (CUSTOMER, PROJECT, etc.)
     */
    @Builder.Default
    @Column(name = "todo_type")
    private String type = TodoListTypeEnum.CUSTOMER.name();

    /**
     * Color theme for the todo list display
     */
    @Builder.Default
    @Column(name = "todo_color")
    private String color = "red";

    /**
     * Display order of the todo list in the Kanban board
     */
    @Builder.Default
    @Column(name = "todo_order")
    private int order = 0;

    /**
     * Tasks associated with this todo list
     */
    @Builder.Default
    @OneToMany(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    private List<TaskEntity> tasks = new ArrayList<>();

    /**
     * Associated project UID for project-specific todo lists
     */
    private String projectUid;

    /**
     * Associated module UID for module-specific todo lists
     */
    private String moduleUid;

}
