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

import java.util.Set;

import com.bytedesk.core.base.BaseRequest;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.rbac.user.UserEntity.Sex;

// import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
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

    private static final long serialVersionUID = 1L;

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

	// 性别（MALE, FEMALE, UNKNOWN）
	// 注意：此处默认不设置，null 表示“未提供”，更新时保留数据库原值；
	// 新建用户由 UserEntity 的 @Builder.Default 落为 UNKNOWN
	private Sex sex;

	private Boolean enabled;

	// private Boolean system;
	
	private Boolean emailVerified;

	private Boolean mobileVerified;

	// 注册来源（例如：USERNAME、EMAIL、MOBILE、GITHUB、WECHAT、GOOGLE、DINGTALK、FEISHU、FACEBOOK、DOUYIN、LDAP、OIDC、OPENID、CAS、ADMIN、SUPER、SYSTEM、IMPORT、UNKNOWN）
	// 注意：此处默认不设置，交由 UserService.register 按请求内容推断（邮箱/手机号/用户名），
	// 各登录注册入口（如手机验证码自动注册、OAuth）按需显式指定
	private String registerSource;
	
	// 注册验证码
	private String captchaUid;
	private String captchaCode;

	// 组织名称搜索
	private String currentOrganization;

	// 角色 uid 列表（用于管理员更新用户在当前组织下的角色）
	private Set<String> roleUids;

}
