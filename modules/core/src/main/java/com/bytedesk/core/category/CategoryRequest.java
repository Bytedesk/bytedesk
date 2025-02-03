/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:21:44
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-03 13:59:43
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.category;

import java.util.List;
import java.util.ArrayList;

import com.bytedesk.core.base.BaseRequest;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.enums.LevelEnum;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class CategoryRequest extends BaseRequest {

    private String name;
    // private String icon;

    @Builder.Default
    private Integer orderNo = 0;

    @Builder.Default
    private String level = LevelEnum.ORGANIZATION.name();

    // 需要前端传递字符串的情况下，使用string类型
    @NotBlank
    @Builder.Default
    private String platform = BytedeskConsts.PLATFORM_BYTEDESK;

    // 父类uid
    private String parentUid;

    @Builder.Default
    private List<String> children = new ArrayList<>();

    // knowledge base uid
    private String kbUid;

    // 用户uid
    private String userUid;
}
