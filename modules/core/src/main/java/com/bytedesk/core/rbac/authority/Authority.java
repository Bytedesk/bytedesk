/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-05-04 10:39:34
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.authority;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import jakarta.persistence.*;

import com.bytedesk.core.utils.AbstractEntity;

/**
 * @author im.bytedesk.com
 */
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper=false)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "core_authority")
public class Authority extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    // @NotBlank
	// @Column(unique = true, nullable = false, length = 127)
	// private String aid;

    private String name;

    /** value is a keyword in h2 db */
    @Column(name = "by_value")
    private String value;

    // private Integer status = 1;

    private String description;

    @Column(name = "by_type")
    private String type;

    // @JsonIgnore
    // @OneToMany
    // private List<Action> actions = new ArrayList<>();

}
