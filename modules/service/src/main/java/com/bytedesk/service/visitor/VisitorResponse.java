/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-04 17:05:59
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-05-04 12:02:09
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor;

import com.bytedesk.core.utils.BaseResponse;


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
    
    // private String uid;

	/**
	 * developers can set basic visitor info
	 */
	private String nickname;

	private String avatar;

	// location info
	private String ip;

	private String ipLocation;

	// device info
	private String browser;

	private String os;

	private String device;

	private String referrer;

	// used for agent notation
	private String mobile;

	private String email;

	private String note;
	
	// from source
	private String client;

    
}
