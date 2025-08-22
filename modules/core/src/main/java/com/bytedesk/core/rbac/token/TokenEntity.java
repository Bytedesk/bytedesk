/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-08 11:22:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-22 07:34:51
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.token;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.ArrayList;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.converter.StringListConverter;
import com.bytedesk.core.enums.ChannelEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import com.bytedesk.core.utils.BdDateUtils;

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
@SuperBuilder
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

    @Builder.Default
    @Column(name = "token_type")
    private String type = TokenTypeEnum.BEARER.name();

    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(name = "token_scope")
    private List<String> scope = new ArrayList<>();

    private ZonedDateTime expiresAt;

    // 是否永久有效
    @Builder.Default
    @Column(name = "is_permanent")
    private Boolean permanent = false;

    @Builder.Default
    @Column(name = "is_revoked")
    private Boolean revoked = false;

    private String revokeReason;

    @Builder.Default
    private String channel = ChannelEnum.WEB.name();

    // 客户端clientId： uid + "/" + constants.HTTP_CHANNEL + "/" + deviceUid;
    private String clientId;

    // 设备信息，比如安卓手机信息、苹果手机信息等
    private String device;

    // 验证token是否有效
    public Boolean isValid() {
        return !Boolean.TRUE.equals(revoked) && !isDeleted() && 
               (Boolean.TRUE.equals(permanent) || (expiresAt != null && expiresAt.isAfter(BdDateUtils.now())));
    }

    // 撤销token
    public void revoke() {
        this.revoked = true;
    }

    // 刷新token过期时间
    public void refresh(ZonedDateTime newExpiresAt) {
        this.expiresAt = newExpiresAt;
    }

}
