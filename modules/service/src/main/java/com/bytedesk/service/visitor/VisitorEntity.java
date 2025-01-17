/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-17 12:25:17
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.enums.ClientEnum;
import com.bytedesk.core.enums.LanguageEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
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
@EntityListeners({ VisitorEntityListener.class })
@Table(name = "bytedesk_service_visitor")
public class VisitorEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;
	
	/**
	 * developers can set basic visitor info
	 */
	private String nickname;

	@Builder.Default
	private String avatar = AvatarConsts.DEFAULT_VISITOR_AVATAR_URL;

	@Builder.Default
	private String lang = LanguageEnum.ZH_CN.name();

	@Embedded
	private VisitorDevice device;

	// used for agent notation
	@Builder.Default
	private String mobile = BytedeskConsts.EMPTY_STRING;

	@Builder.Default
	private String email = BytedeskConsts.EMPTY_STRING;

	@Builder.Default
	private String note = BytedeskConsts.EMPTY_STRING;

	@Builder.Default
	private String client = ClientEnum.WEB.name();

	@Builder.Default
	private String status = VisitorStatusEnum.ONLINE.name();

	// extra info，开发者自定义URL参数，使用json格式存储，便于扩展
	@Builder.Default
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_JSON)
    @JdbcTypeCode(SqlTypes.JSON)
    private String extra = BytedeskConsts.EMPTY_JSON_STRING;

	// 方便后续扩展，比如用户被拉黑的时候，暂存于此
	// 浏览的IP
	private String ip;
	// 浏览的IP地址
	private String ipLocation;
	
}