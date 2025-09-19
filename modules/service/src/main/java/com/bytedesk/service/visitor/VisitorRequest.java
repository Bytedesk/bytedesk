/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-04 17:05:48
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-19 17:03:52
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor;

import java.util.List;
import java.util.ArrayList;
import com.bytedesk.core.base.BaseRequest;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.enums.ChannelEnum;
import com.bytedesk.core.enums.LanguageEnum;
import com.bytedesk.core.thread.enums.ThreadTypeEnum;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.experimental.Accessors;


@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class VisitorRequest extends BaseRequest {

	private static final long serialVersionUID = 1L;

	// 前端自定义uid，用于区别于自动生成uid
	private String visitorUid;

	/**
	 * developers can set basic visitor info
	 */
	private String nickname;

	private String avatar;

	@Builder.Default
	private String lang = LanguageEnum.ZH_CN.name();

	@Builder.Default
	private String type = VisitorTypeEnum.ANONYMOUS.name();
	
	// device info
	private String browser;

	private String os;

	private String device;
	
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

	// 标签
    @Builder.Default
    private List<String> tagList = new ArrayList<>();

	// 商品信息
	private String goodsInfo;

	// 订单信息
	private String orderInfo;

	// 自定义参数，从URL传入，使用json格式传入，例如：{"key1":"value1","key2":"value2"}
	@Builder.Default
	private String extra = BytedeskConsts.EMPTY_JSON_STRING;

	// 浏览信息
	// 来源
	private String referrer;
	// 浏览的URL
	private String url;
	// 浏览的标题
	private String title;
	// 浏览的IP
	// @Builder.Default
	private String ip;// = "127.0.0.1";
	// 浏览的IP位置
	// @Builder.Default
	private String ipLocation;// = "localhost";

	// 会员等级
	@Builder.Default
	private Integer vipLevel = 0;

	/**
	 * 判断是否为社交渠道（微信、Meta、Telegram、WhatsApp）
	 * @return true 如果为社交渠道，否则为 false
	 */
	public Boolean isSocial() {
		return isWeChat() || isMeta() || isTelegram() || isWhatsApp();
	}
	
	public Boolean isWeChat() {
		// 忽略大小写，常量放在前面避免空指针异常
		return this.channel != null && ChannelEnum.WECHAT.name().toLowerCase().contains(this.channel.toLowerCase());
	}

	public Boolean isMeta() {
		// 忽略大小写，常量放在前面避免空指针异常
		return this.channel != null && ChannelEnum.MESSENGER.name().toLowerCase().contains(this.channel.toLowerCase());
	}
	
	/**
	 * 判断是否为企业微信客户端
	 * @return true 如果为企业微信客户端，否则为 false
	 */
	public Boolean isWeChatWork() {
		// 忽略大小写，常量放在前面避免空指针异常
		return this.channel != null && ChannelEnum.WECHAT_WORK.name().toLowerCase().contains(this.channel.toLowerCase());
	}

	/**
	 * 判断是否为Telegram客户端
	 * @return true 如果为Telegram客户端，否则为 false
	 */
	public Boolean isTelegram() {
		// 忽略大小写，常量放在前面避免空指针异常
		return this.channel != null && ChannelEnum.TELEGRAM.name().toLowerCase().contains(this.channel.toLowerCase());
	}

	/**
	 * 判断是否为WhatsApp客户端
	 * @return true 如果为WhatsApp客户端，否则为 false
	 */
	public Boolean isWhatsApp() {
		// 忽略大小写，常量放在前面避免空指针异常
		return this.channel != null && ChannelEnum.WHATSAPP.name().toLowerCase().contains(this.channel.toLowerCase());
	}

	public void setWorkgroupType() {
		this.type = String.valueOf(ThreadTypeEnum.WORKGROUP.getValue());
	}

	public void setAgentType() {
		this.type = String.valueOf(ThreadTypeEnum.AGENT.getValue());
	}

	public void setRobotType() {
		this.type = String.valueOf(ThreadTypeEnum.ROBOT.getValue());
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
