/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:20:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-23 11:10:56
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
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.rbac.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
@Table(name = "core_organization")
public class Organization extends BaseEntityNoOrg {

    private static final long serialVersionUID = 1L;

    @NotBlank
    /** name should be unique */
    @Column(unique = true, nullable = false)
    private String name;

    // logo
    @Builder.Default
    private String logo = AvatarConsts.DEFAULT_AVATAR_URL;

    // organiztion code, 可读性强，供用户搜索
    @Column(unique = true)
    private String code;

    @Builder.Default
    private String description = I18Consts.I18N_DESCRIPTION;

    @Builder.Default
    private boolean forceValidateEmail = false;

    @Builder.Default
    private boolean forceValidateMobile = false;

    // @JsonIgnore
    // @Builder.Default
    // @OneToMany(fetch = FetchType.LAZY)
    // private Set<Department> departments = new HashSet<>();

    // Identity Verification 实名认证

    // 认证类型：企业认证、个人认证、政府事业单位认证
    // private String verifiedType;
    private OrganizationVerifyTypeEnum verifiedType;

    // 证件类型：营业执照、身份证、护照、其他
    // private String identityType;
    private OrganizationIdentityTypeEnum identityType;

    // 证件图片：营业执照、身份证、护照、其他
    private String identityImage;

    // 证件号码：企业信用代码、身份证号码
    private String identityNumber;

    // 认证时间
    private Date verifyDate;

    // 认证状态：未认证、已认证、审核中、审核失败
    // private String verifyStatus;
    private OrganizationVerifyStatusEnum verifyStatus;

    // 认证失败原因
    private String rejectReason;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

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
