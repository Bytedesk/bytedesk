/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-03-29 14:02:22
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.action;

import com.bytedesk.core.utils.AuditModel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 对应权限Authority中操作：query/create/update/delete/export/import
 *
 * @author bytedesk.com on 2019-08-05
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "core_action")
public class Action extends AuditModel {

    private static final long serialVersionUID = -4364459013162121569L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

	@Column(unique = true, nullable = false, length = 127)
	private String aid;

    private String name;

    private String value;

    private String description;

    // @JsonIgnore
    // @ManyToMany(mappedBy = "actions", fetch = FetchType.LAZY)
    // private Set<Authority> authorities = new HashSet<>();

}
