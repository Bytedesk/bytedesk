/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-06 09:55:40
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-16 09:56:32
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.group;

import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseRequest;
import com.bytedesk.core.group.enums.GroupStatusEnum;
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
@NoArgsConstructor
@AllArgsConstructor
public class GroupRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;


    private String name;

    private String avatar;

    private String description;

    private Boolean showTopTip;

    private String topTip;

    private Boolean isExternal;

    /** 群可见性：PUBLIC / PRIVATE（GroupVisibilityEnum.name()），非法值回退 PRIVATE */
    private String visibility;

    /** 公开群加入是否需要审批（仅 visibility=PUBLIC 时有意义） */
    private Boolean needApproval;

    private GroupStatusEnum status;

    @Builder.Default
    private List<String> memberUids = new ArrayList<>();

    @Builder.Default
    private List<String> adminUids = new ArrayList<>();

    @Builder.Default
    private List<String> robotUids = new ArrayList<>();

    /** 公开群搜索模式（query/public 端点使用，服务端强制置 true） */
    private Boolean publicOnly;

}
