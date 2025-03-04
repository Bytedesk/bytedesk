/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:26:04
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-04 16:45:47
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.split;

import com.bytedesk.core.base.BaseRequest;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.enums.PlatformEnum;

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
public class SplitRequest extends BaseRequest {

    private String name;

    // private String content;

    // @Builder.Default
    // private String type = SplitTypeEnum.CUSTOMER.name();

    @Builder.Default
    private String level = LevelEnum.ORGANIZATION.name();

    @Builder.Default
    private String platform = PlatformEnum.BYTEDESK.name();

    // 对应 拆分 document 的 id
    private String docId;

    // 合并fileEntity/textEntity/qaEntity/websiteEntity 的 uid
    private String typeUid;

    // 所属分类
    private String categoryUid;

    // kbaseEntity 的 uid
    private String kbUid;

    // 对应 userEntity 的 uid
    // private String userUid;
}
