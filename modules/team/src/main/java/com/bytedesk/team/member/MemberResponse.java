/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-25 15:36:57
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-20 15:03:20
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.team.member;

import java.util.HashSet;
import java.util.Set;

import com.bytedesk.core.base.BaseResponse;
import com.bytedesk.core.rbac.user.UserProtobuf;
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
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MemberResponse extends BaseResponse {

    private String nickname;

    private String avatar;

    private String description;

    private String seatNo;

    private String jobNo;

    private String jobTitle; // 职位

    private String telephone;
    
    private String email;

    private String mobile;

    private String status;

    private String deptUid;

    @Builder.Default
	private Set<String> roleUids = new HashSet<>(); 

    private UserProtobuf user;
}
