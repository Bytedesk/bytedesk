/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:14:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-12 10:17:39
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kanban.module;

import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.kanban.todo_list.TodoListEntity;
import com.bytedesk.core.member.MemberEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({ModuleEntityListener.class})
@Table(name = "bytedesk_plugin_kanban_module")
public class ModuleEntity extends BaseEntity {

    private String name;

    @Builder.Default
    private String description = I18Consts.I18N_DESCRIPTION;

    @Builder.Default
    @Column(name = "module_type")
    private String type = ModuleTypeEnum.CUSTOMER.name();

    @Builder.Default
    @Column(name = "module_color")
    private String color = "red";

    @Builder.Default
    @Column(name = "module_order")
    private int order = 0;

    // @Builder.Default
    // private String level = LevelEnum.ORGANIZATION.name();

    // @Builder.Default
    // private String platform = PlatformEnum.BYTEDESK.name();

    @Builder.Default
    @ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    private List<MemberEntity> members = new ArrayList<>();

    @Builder.Default
    @OneToMany(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    private List<TodoListEntity> todoLists = new ArrayList<>();

    // 是否公开，公开后，对外匿名可见
    @Builder.Default
    @Column(name = "is_public")
    private boolean isPublic = false;

    private String projectUid;

}
