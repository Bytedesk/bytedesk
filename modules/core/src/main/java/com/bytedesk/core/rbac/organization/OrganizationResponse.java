/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-01 21:20:57
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-16 17:36:28
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.organization;

import java.util.Date;

import com.bytedesk.core.base.BaseResponseNoOrg;
import com.bytedesk.core.rbac.user.UserResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrganizationResponse extends BaseResponseNoOrg {

    private static final long serialVersionUID = 1L;
    
    private String name;

    private String logo;

    private String code;

    private String description;

    // 认证类型：企业认证、个人认证、政府事业单位认证
    private String verifiedType;
    // private OrganizationVerifyTypeEnum verifiedType;

    // 证件类型：营业执照、身份证、护照、其他
    private String identityType;
    // private OrganizationIdentityTypeEnum identityType;

    // 证件图片：营业执照、身份证、护照、其他
    private String identityImage;

    // 证件号码：企业信用代码、身份证号码
    private String identityNumber;

    // 认证时间
    private Date verifyDate;

    // 认证状态：未认证、已认证、审核中、审核失败
    private String verifyStatus;

    // 认证失败原因
    private String rejectReason;

    // 是否会员
    private Boolean vip;

    // 会员截止日期
    private Date vipExpireDate;

    // 是否启用，状态：启用/禁用
    private Boolean enabled;

    // 组织管理员
    private UserResponse user;

}
