/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 13:43:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-22 11:24:20
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.team.member;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

import com.bytedesk.core.config.BytedeskProperties;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.team.department.DepartmentConsts;

import lombok.AllArgsConstructor;

@Component("memberInitializer")
@AllArgsConstructor
public class MemberInitializer implements SmartInitializingSingleton {

    private final MemberRestService memberService;

    private final BytedeskProperties bytedeskProperties;

    @Override
    public void afterSingletonsInstantiated() {
        // 迁移到 WorkgroupInitializer 执行
        // init();
    }

    // @PostConstruct
    public void init() {
        //
        String orgUid = BytedeskConsts.DEFAULT_ORGANIZATION_UID;
        String nickname = bytedeskProperties.getNickname();
        String email = bytedeskProperties.getEmail();
        String mobile = bytedeskProperties.getMobile();
        if (!memberService.existsByEmailAndOrgUid(email, orgUid)) {
            MemberRequest memberRequest = MemberRequest.builder()
                    .jobNo("001")
                    .jobTitle(I18Consts.I18N_ADMIN)
                    .nickname(nickname)
                    .seatNo("001")
                    .telephone("001")
                    .mobile(mobile)
                    .email(email)
                    .status(MemberStatusEnum.ACTIVE.name())
                    .deptUid(DepartmentConsts.DEFAULT_DEPT_ADMIN_UID)
                    .build();
            memberRequest.setOrgUid(orgUid);
            memberService.create(memberRequest);
        }
    }

}
