/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:21:44
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-20 15:24:14
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
public class CategoryRequest extends BaseRequest {

    private String name;
    // private String icon;

    @Builder.Default
    private Integer order = 0;
    
    // 父类uid
    private String parentUid;

    @Builder.Default
    private List<String> childrenUids = new ArrayList<>();

    // knowledge base uid
    private String kbUid;

}
