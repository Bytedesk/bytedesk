/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-21 11:37:18
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor;

import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.converter.CustomFieldItemListConverter;
import com.bytedesk.core.converter.StringListConverter;
import com.bytedesk.core.enums.ChannelEnum;
import com.bytedesk.core.enums.LanguageEnum;
import com.bytedesk.core.model.CustomFieldItem;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * visitor no need to login, without login can reduce the press of the database
 */
@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
// @EntityListeners({ VisitorEntityListener.class })
@Table(name = "bytedesk_service_visitor", 
	uniqueConstraints = {
		@UniqueConstraint(name = "uk_visitor_uid_org_uid", columnNames = {"visitor_uid", "org_uid"})
	})
public class VisitorEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	// 用户自定义uid，用于区别于自动生成uid
	@Column(name = "visitor_uid")
	private String visitorUid;

	private String nickname;

	@Builder.Default
	private String avatar = AvatarConsts.getDefaultVisitorAvatarUrl();

	@Builder.Default
	private String lang = LanguageEnum.ZH_CN.name();

	@Builder.Default
	@Column(name = "visitor_type")
	private String type = VisitorTypeEnum.ANONYMOUS.name();

	@Embedded
	private VisitorDevice deviceInfo;

	// used for agent notation
	@Builder.Default
	private String mobile = BytedeskConsts.EMPTY_STRING;

	@Builder.Default
	private String email = BytedeskConsts.EMPTY_STRING;

	@Builder.Default
	private String note = BytedeskConsts.EMPTY_STRING;

	@Builder.Default
	private String channel = ChannelEnum.WEB.name();

	@Builder.Default
	private String status = VisitorStatusEnum.ONLINE.name();

	// 标签
    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> tagList = new ArrayList<>();

	// 用户自定义字段：字段昵称/字段key/字段值（JSON）
	@Builder.Default
	@Convert(converter = CustomFieldItemListConverter.class)
	@Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
	private List<CustomFieldItem> customFieldList = new ArrayList<>();

	// extra info，开发者自定义URL参数，使用json格式存储，便于扩展
	@Builder.Default
	@Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String extra = BytedeskConsts.EMPTY_JSON_STRING;

	// 方便后续扩展，比如用户被拉黑的时候，暂存于此
	// 浏览的IP
	private String ip;
	// 浏览的IP地址
	private String ipLocation;

	// 会员等级，tier层级
	@Builder.Default
	private Integer vipLevel = 0;
	
}