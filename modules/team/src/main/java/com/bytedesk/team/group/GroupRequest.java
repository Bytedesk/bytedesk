/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-06 09:55:40
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-12 20:32:50
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.team.group;

import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseRequest;
import com.bytedesk.core.constant.AvatarConsts;
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
@NoArgsConstructor
@AllArgsConstructor
public class GroupRequest extends BaseRequest {

    @Builder.Default
    private String name = I18Consts.I18N_GROUP_NAME;

    @Builder.Default
    private String avatar = AvatarConsts.getDefaultGroupAvatarUrl();

    @Builder.Default
    private String description = I18Consts.I18N_GROUP_DESCRIPTION;

    @Builder.Default
    private Boolean showTopTip = false;

    private String topTip;

    @Builder.Default
    private Boolean isExternal = false;

    @Builder.Default
    private GroupStatusEnum status = GroupStatusEnum.NORMAL;

    @Builder.Default
    private List<String> memberUids = new ArrayList<>();

    @Builder.Default
    private List<String> adminUids = new ArrayList<>();

    @Builder.Default
    private List<String> robotUids = new ArrayList<>();

    // invite/remove member by uid
    private String memberUid;
}
