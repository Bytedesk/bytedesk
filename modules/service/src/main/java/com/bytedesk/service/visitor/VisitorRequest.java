/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-04 17:05:48
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-24 13:01:36
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
import com.bytedesk.core.enums.ClientEnum;
import com.bytedesk.core.enums.LanguageEnum;
import com.bytedesk.core.thread.ThreadTypeEnum;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
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
	@Builder.Default
	private String ip = "127.0.0.1";

	@Builder.Default
	private String ipLocation = "localhost";

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

	@Builder.Default
	private String status = VisitorStatusEnum.ONLINE.name();

	// 自定义参数，从URL传入，使用json格式传入，例如：{"key1":"value1","key2":"value2"}
	private String extra;

	// wechat mp extra
	private String threadExtra;
	public Boolean isWeChat() {
		return this.client.contains(ClientEnum.WECHAT.name());
	}

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
