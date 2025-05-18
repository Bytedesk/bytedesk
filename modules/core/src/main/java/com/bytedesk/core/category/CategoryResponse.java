/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:21:53
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-11 08:58:28
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

import com.bytedesk.core.base.BaseResponse;

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
public class CategoryResponse extends BaseResponse {
    
    private String name;

    private String type;
    
    // private String icon;

    private Integer order;

    // 父类uid
    private String parentUid;

    // 子类列表
    private List<CategoryResponse> children;
    
    // knowledge base uid
    private String kbUid;

    // 用户uid
    private String userUid;

    // member count of this category
    private Integer memberCount;
}
