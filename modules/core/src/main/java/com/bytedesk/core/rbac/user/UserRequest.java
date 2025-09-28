/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-01 21:40:38
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.user;

import com.bytedesk.core.base.BaseRequest;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.PlatformEnum;
import com.bytedesk.core.rbac.user.UserEntity.Sex;

// import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest extends BaseRequest {

    private String uid;

	private String num;

	private String username;

	private String nickname;

	private String password;

	// 专门用于修改密码
	private String oldPassword;
	private String newPassword;

	@Email(message = "email format error")
	private String email;

	private String country;
	
	private String mobile;
	
	private String code;

	@Builder.Default
	private String avatar = AvatarConsts.getDefaultAvatarUrl();

	@Builder.Default
	private String description = I18Consts.I18N_USER_DESCRIPTION;

	@Builder.Default
	private Sex sex = Sex.UNKNOWN;

	private Boolean enabled;
	
	private Boolean emailVerified;

	private Boolean mobileVerified;

	// 需要前端传递字符串的情况下，使用string类型
	@NotBlank(message = "platform required")
	@Builder.Default
	private String platform = PlatformEnum.BYTEDESK.name();

	// 注册来源（例如：USERNAME、EMAIL、MOBILE、GITHUB、WECHAT、GOOGLE、DINGTALK、FEISHU、FACEBOOK、DOUYIN、LDAP、OIDC、OPENID、CAS、ADMIN、IMPORT、UNKNOWN）
	@Builder.Default
	private String registerSource = "UNKNOWN";
	
	// 注册验证码
	private String captchaUid;
	private String captchaCode;

}
