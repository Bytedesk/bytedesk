/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-25 15:30:11
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-25 20:23:47
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.push;

import com.bytedesk.core.constant.StatusConsts;
import com.bytedesk.core.utils.AuditModel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 
 */
@Data
@Entity
@Builder
@EqualsAndHashCode(callSuper=false)
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "core_push")
public class Push extends AuditModel {
    
    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(unique = true, nullable = false)
    private String pid;

    private String sender;
    
    private String content;

    private String receiver;

    @Column(name = "by_type")
    private String type;
    
    @Builder.Default
    private String status = StatusConsts.CODE_STATUS_PENDING;
    
    private String client;
}
