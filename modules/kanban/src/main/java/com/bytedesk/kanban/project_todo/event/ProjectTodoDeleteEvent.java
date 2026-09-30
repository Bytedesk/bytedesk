/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 12:31:16
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-25 12:31:19
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kanban.project_todo.event;

import org.springframework.context.ApplicationEvent;

import com.bytedesk.kanban.project_todo.ProjectTodoEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ProjectTodoDeleteEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    private ProjectTodoEntity todo;

    public ProjectTodoDeleteEvent(ProjectTodoEntity todo) {
        super(todo);
        this.todo = todo;
    }
}
