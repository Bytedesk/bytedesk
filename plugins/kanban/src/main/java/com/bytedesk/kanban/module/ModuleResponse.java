/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:26:12
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-10 17:55:28
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kanban.module;

import java.util.List;

import com.bytedesk.core.base.BaseResponse;
import com.bytedesk.kanban.todo_list.TodoListResponse;
import com.bytedesk.core.member.MemberProtobuf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class ModuleResponse extends BaseResponse {

    private String name;

    private String description;

    private String type;

    // private String color;

    private Integer order;

    private List<MemberProtobuf> members;

    private List<TodoListResponse> todoLists;

    private String projectUid;

    private String userUid;

    private Boolean isPublic;

    // private ZonedDateTime createdAt;

    // private ZonedDateTime updatedAt;
}
