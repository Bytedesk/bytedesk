/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-06 16:02:35
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-16 18:02:09
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.organization;

import java.time.ZonedDateTime;
import java.util.Date;

import com.bytedesk.core.base.BaseRequestNoOrg;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationRequest extends BaseRequestNoOrg {

    private String name;

    private String logo;

    private String code;
    
    private String description;

    // 认证类型：企业认证、个人认证、政府事业单位认证
    @Builder.Default
    private String verifiedType = OrganizationVerifyTypeEnum.PERSONAL.name();

    // 证件类型：营业执照、身份证、护照、其他
    @Builder.Default
    private String identityType = OrganizationIdentityTypeEnum.ID_CARD.name();

    // 证件图片：营业执照、身份证、护照、其他
    private String identityImage;

    // 证件号码：企业信用代码、身份证号码
    private String identityNumber;

    // 认证时间
    private ZonedDateTime verifyDate;

    // 认证状态：未认证、已认证、审核中、审核失败
    @Builder.Default
    private String verifyStatus = OrganizationVerifyStatusEnum.UNVERIFIED.name();

    // 认证失败原因
    private String rejectReason;

    // 是否会员
    private Boolean vip;

    // 会员截止日期
    private ZonedDateTime vipExpireDate;

    // 是否启用，状态：启用/禁用
    private Boolean enabled;

    // 组织管理员， 父类中已存在
    // private String userUid;

}
