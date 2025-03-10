/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-04 17:05:59
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-10 17:57:29
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor;

import com.bytedesk.core.base.BaseResponse;
import com.bytedesk.core.enums.ClientEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * used for agent client
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class VisitorResponse extends BaseResponse {

	private static final long serialVersionUID = 1L;

	/**
	 * developers can set basic visitor info
	 */
	private String nickname;

	private String avatar;

	// language
	private String lang;

	private VisitorDevice device;
	
	// used for agent notation
	private String mobile;

	private String email;

	private String note;

	// from source
	// private String client;
	private ClientEnum client;

	// private LocalDateTime updatedAt;

	private String status;

	private String extra;

	// 方便后续扩展，比如用户被拉黑的时候，暂存于此
	// 浏览的IP
	private String ip;
	// 浏览的IP地址
	private String ipLocation;

	// 会员等级
	private Integer vipLevel;
}
