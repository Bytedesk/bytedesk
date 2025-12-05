/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 13:43:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-20 18:11:55
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.member;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.PermissionEnum;
import com.bytedesk.core.rbac.authority.AuthorityRestService;
import com.bytedesk.core.department.DepartmentConsts;

import lombok.AllArgsConstructor;

@Component("memberInitializer")
@AllArgsConstructor
public class MemberInitializer implements SmartInitializingSingleton {

    private final MemberRestService memberService;

    private final BytedeskProperties bytedeskProperties;

    private final AuthorityRestService authorityRestService;

    @Override
    public void afterSingletonsInstantiated() {
        // 迁移到 WorkgroupInitializer 执行
        // init();
        //
        initPermissions();
    }

    // @PostConstruct
    // UnifiedInitializer 中调用
    public void init() {
        //
        String orgUid = BytedeskConsts.DEFAULT_ORGANIZATION_UID;
        String nickname = bytedeskProperties.getNickname();
        String email = bytedeskProperties.getEmail();
        String mobile = bytedeskProperties.getMobile();
        //
        Set<String> roleUids = new HashSet<>(Arrays.asList(
                BytedeskConsts.DEFAULT_ROLE_SUPER_UID));
        //
        MemberRequest memberRequest = MemberRequest.builder()
                .uid(BytedeskConsts.DEFAULT_MEMBER_UID)
                .jobNo("001")
                .jobTitle(I18Consts.I18N_ADMIN)
                .nickname(nickname)
                .seatNo("001")
                .telephone("001")
                .mobile(mobile)
                .email(email)
                .roleUids(roleUids)
                .status(MemberStatusEnum.ACTIVE.name())
                .deptUid(DepartmentConsts.DEFAULT_DEPT_ADMIN_UID)
                .orgUid(orgUid)
                .build();
        memberService.create(memberRequest);

    }

    private void initPermissions() {
        for (PermissionEnum permission : PermissionEnum.values()) {
            String permissionValue = MemberPermissions.MEMBER_PREFIX + permission.name();
            authorityRestService.createForPlatform(permissionValue);
        }
    }
}
