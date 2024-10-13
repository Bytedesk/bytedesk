/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-04 17:05:48
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-10 10:23:35
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

import com.bytedesk.core.base.BaseRequest;
import com.bytedesk.core.enums.LanguageEnum;
import com.bytedesk.core.thread.ThreadTypeEnum;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class VisitorRequest extends BaseRequest {

	private static final long serialVersionUID = 1L;

	/**
	 * developers can set basic visitor info
	 */
	private String nickname;

	private String avatar;

	@Builder.Default
	private String lang = LanguageEnum.ZH_CN.name();

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

	// for thread request
	private String sid;

	// 强制转人工服务，默认false
	@Builder.Default
	private Boolean forceAgent = false;

	private String status;

	public ThreadTypeEnum formatType() {
		int typeInt;
		try {
			typeInt = Integer.parseInt(super.type);
		} catch (NumberFormatException e) {
			// 处理异常，比如记录日志、返回默认值等
			e.printStackTrace();
			// 假设有一个默认值
			typeInt = 0;
		}
		return ThreadTypeEnum.fromValue(typeInt);
	}

	
	
}
