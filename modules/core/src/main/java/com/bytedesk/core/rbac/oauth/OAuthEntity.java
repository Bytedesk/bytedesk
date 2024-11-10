/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-10 23:20:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-22 12:13:38
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.oauth;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.rbac.user.UserEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

// github：https://www.ruanyifeng.com/blog/2019/04/github-oauth.html
// 第三方授权登录
@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_core_oauth")
public class OAuthEntity extends BaseEntity {

    private String openId;

    private String unionId;

    private String accessToken;

    private String refreshToken;

    @Builder.Default
    // @Enumerated(EnumType.STRING)
    // private OAuthProviderEnum provider = OAuthProviderEnum.GITHUB;
    private String provider = OAuthProviderEnum.GITHUB.name();

    // private String scope;
    // private String tokenType;
    // private Integer expiresIn;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;
}