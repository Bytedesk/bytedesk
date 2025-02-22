/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-22 16:17:11
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-09 16:33:25
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.browse;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.service.visitor.VisitorEntity;
import com.bytedesk.service.visitor.VisitorStatusEnum;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 访客浏览网站记录
 * visitor website browse record
 * 仅保留48小时内的浏览记录，超时则自动删除
 */
@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({ BrowseEntityListener.class })
@Table(name = "bytedesk_service_browse")
public class BrowseEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    // 来源
    private String referrer;

    // 浏览的URL
    private String url;

    // 浏览的标题
    private String title;

    // 浏览的IP
    private String ip;

    // 浏览的IP地址
    private String ipLocation;

    // 状态
    @Builder.Default
	private String status = VisitorStatusEnum.ONLINE.name();

    // 访客
    @ManyToOne
    private VisitorEntity visitor;

}
