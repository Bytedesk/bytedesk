/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:26:04
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-12 10:18:29
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

import com.bytedesk.core.base.BaseRequest;
import com.bytedesk.core.constant.I18Consts;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class TodoListRequest extends BaseRequest {

    private String name;

    @Builder.Default
    private String description = I18Consts.I18N_DESCRIPTION;

    // @Builder.Default
    // private String type = TodoListTypeEnum.CUSTOMER.name();

    @Builder.Default
    private String color = "red";

    @Builder.Default
    private Integer order = 0;

    // @Builder.Default
    // private String level = LevelEnum.ORGANIZATION.name();

    // @Builder.Default
    // private String platform = PlatformEnum.BYTEDESK.name();

    private String projectUid;

    private String moduleUid;

    @Builder.Default
    private List<String> taskUids = new ArrayList<>();
}
