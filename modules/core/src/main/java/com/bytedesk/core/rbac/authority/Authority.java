/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-03-29 14:01:47
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

import com.bytedesk.core.rbac.action.Action;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.experimental.Accessors;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author im.bytedesk.com
 */
@Data
@Accessors(chain = true)
@Entity
@Table(name = "core_authority")
// @JsonIgnoreProperties(value = { "handler", "hibernateLazyInitializer",
// "fieldHandler" })
public class Authority implements Serializable {

    private static final long serialVersionUID = 8868352778004638978L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

	@Column(unique = true, nullable = false, length = 127)
	private String aid;

    private String name;

    private String value;

    private Integer status = 1;

    private String description;

    @Column(name = "by_type")
    private String type;

    @JsonIgnore
    @OneToMany
    private List<Action> actions = new ArrayList<>();

    // @JsonIgnore
    // @ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    // @JoinTable(name = "core_authority_actions", 
    //     joinColumns = @JoinColumn(name = "authority_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT)), 
    //     inverseJoinColumns = @JoinColumn(name = "action_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    // )
    // private List<Action> actions;

}
