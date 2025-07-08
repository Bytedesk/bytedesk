/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:20:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-02 10:29:30
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.department;

import java.util.Set;
import java.util.HashSet;

import com.bytedesk.core.base.BaseEntity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * Department entity for organizational hierarchy management
 * Manages department structure with parent-child relationships
 * 
 * Database Table: bytedesk_team_department
 * Purpose: Stores department information and hierarchical organization structure
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false, exclude = { "children", "parent" })
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_team_department")
public class DepartmentEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Name of the department
     */
    private String name;

    /**
     * Description of the department's function
     */
    private String description;

    /**
     * Parent department in the hierarchy
     */
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private DepartmentEntity parent;

    /**
     * Child departments under this department
     */
    @Builder.Default
    @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<DepartmentEntity> children = new HashSet<>();

    /**
     * Add a child department to this department
     */
    public void addChild(DepartmentEntity child) {
        children.add(child);
        child.setParent(this);
    }

    /**
     * Remove a child department from this department
     */
    public void removeChild(DepartmentEntity child) {
        children.remove(child);
        child.setParent(null);
    }

}
