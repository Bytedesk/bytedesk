/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:20:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-09 23:10:58
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.organization;

import java.util.Date;

import com.bytedesk.core.base.BaseEntityNoOrg;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.rbac.user.UserEntity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false, exclude = { "user" })
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({ OrganizationEntityListener.class })
@Table(name = "bytedesk_core_organization")
public class OrganizationEntity extends BaseEntityNoOrg {

    private static final long serialVersionUID = 1L;

    @NotBlank
    /** name should be unique */
    @Column(unique = true, nullable = false)
    private String name;

    // logo
    @Builder.Default
    private String logo = AvatarConsts.DEFAULT_AVATAR_URL;

    // organization code, 可读性强，供用户搜索
    @Column(unique = true)
    private String code;

    @Builder.Default
    private String description = I18Consts.I18N_DESCRIPTION;

    @Builder.Default
    private boolean forceValidateEmail = false;

    @Builder.Default
    private boolean forceValidateMobile = false;
    
    // TODO: Identity Verification 实名认证

    // 认证类型：企业认证、个人认证、政府事业单位认证
    @Builder.Default
    private String verifiedType = OrganizationVerifyTypeEnum.COMPANY.name();

    // 证件类型：营业执照、身份证、护照、其他
    @Builder.Default
    private String identityType = OrganizationIdentityTypeEnum.COMPANY_LICENSE.name();

    // 证件图片：营业执照、身份证、护照、其他
    @Builder.Default
    private String identityImage = BytedeskConsts.EMPTY_STRING;

    // 证件号码：企业信用代码、身份证号码
    @Builder.Default
    private String identityNumber = BytedeskConsts.EMPTY_STRING;

    // 认证时间
    private Date verifyDate;

    // 认证状态：未认证、已认证、审核中、审核失败
    @Builder.Default
    private String verifyStatus = OrganizationVerifyStatusEnum.UNVERIFIED.name();

    // 认证失败原因
    private String rejectReason;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

    @Override
    public String toString() {
        return "Organization{" +
                "id=" + super.getId() +
                ", uid=" + super.getUid() +
                ", name='" + name + '\'' +
                ", logo=" + logo +
                '}';
    }

}
