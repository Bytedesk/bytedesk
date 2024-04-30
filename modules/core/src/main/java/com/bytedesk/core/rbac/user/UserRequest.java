/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-26 10:53:19
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.user;

import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.BdConstants;
import com.bytedesk.core.utils.BaseRequest;
// import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest extends BaseRequest {

    private Long id;

    private String uid;

	private String num;

	private String username;

	private String nickname;

	private String password;

	@Email(message = "email format error")
	private String email;

	// country prefix, e.g. +86
	// @Digits(message = "phone length error", fraction = 0, integer = 11)
	private String mobile;
	private String code;

	@Builder.Default
	private String avatar = AvatarConsts.DEFAULT_AVATAR_URL;

	@Builder.Default
	private String description = BdConstants.DEFAULT_USER_DESCRIPTION;

	private Boolean enabled;
	
	// private boolean superUser;

	private Boolean emailVerified;

	private Boolean mobileVerified;

}
