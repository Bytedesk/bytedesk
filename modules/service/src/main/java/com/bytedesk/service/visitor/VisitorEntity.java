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
import com.bytedesk.core.converter.StringListConverter;
import com.bytedesk.core.enums.ClientEnum;
import com.bytedesk.core.enums.LanguageEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * visitor no need to login, without login can reduce the press of the database
 * TODO: 对于平台型app来说，visitor不属于某个org，所有备注信息都应该按照org单独存储，
 * TODO: 而不是和visitor合并到一起，将备注信息写入到customer表中
 */
@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
// @EntityListeners({ VisitorEntityListener.class })
@Table(name = "bytedesk_service_visitor")
public class VisitorEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	// 用户自定义uid，用于区别于自动生成uid
	private String visitorUid;

	private String nickname;

	@Builder.Default
	private String avatar = AvatarConsts.getDefaultVisitorAvatarUrl();

	@Builder.Default
	private String lang = LanguageEnum.ZH_CN.name();

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
	private String client = ClientEnum.WEB.name();

	@Builder.Default
	private String status = VisitorStatusEnum.ONLINE.name();

	// 标签
    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> tagList = new ArrayList<>();

	// extra info，开发者自定义URL参数，使用json格式存储，便于扩展
	@Builder.Default
	@Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String extra = BytedeskConsts.EMPTY_JSON_STRING;

	// 方便后续扩展，比如用户被拉黑的时候，暂存于此
	// 浏览的IP
	private String ip;
	// 浏览的IP地址
	private String ipLocation;

	// 会员等级
	@Builder.Default
	private Integer vipLevel = 0;
	
}