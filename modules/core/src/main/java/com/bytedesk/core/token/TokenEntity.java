/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-08 11:22:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-28 12:25:23
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.token;

import java.time.LocalDateTime;
import java.util.UUID;

import com.bytedesk.core.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 可用于强制用户重新登录，或者刷新token
 * access_token or refresh_token
 * 授权验证token时，不只是使用jwt解码验证，还需要跟数据库中进行对比，如果token被禁用或者过期，则拒绝访问
 * 存储第三方access_token，refresh_token，过期时间等，微信，qq等第三方授权登录
 * 
 * @author jackning
 */
@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_core_token")
public class TokenEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String name;

    private String description;

    private String accessToken;

    private String refreshToken;

    @Column(name = "token_type")
    private String type;

    private LocalDateTime expiresAt;

    @Builder.Default
    @Column(name = "is_revoked")
    private boolean revoked = false;

    // user, no need map, just uid
    @NotBlank
    @Column(nullable = false)
    private String userUid;

    // current auth clientIds
    // @Builder.Default
    // @Convert(converter = StringSetConverter.class)
    // private Set<String> clientIds = new HashSet<>();

    // 生成token
    public String generateToken() {
        return UUID.randomUUID().toString();
    }

    // 验证token是否有效
    public boolean isValid() {
        // return !revoked && expiresAt.after(LocalDateTime.now());
        return !revoked && expiresAt.isAfter(LocalDateTime.now());
    }

    // 撤销token
    public void revoke() {
        this.revoked = true;
    }

    // 刷新token过期时间
    public void refresh(LocalDateTime newExpiresAt) {
        this.expiresAt = newExpiresAt;
    }

}
