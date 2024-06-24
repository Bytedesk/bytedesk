/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:20:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-23 11:18:02
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.team.department;

import java.util.Set;
import java.util.HashSet;

import com.bytedesk.core.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 部门
 */
@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false, exclude = { "children", "parent" })
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "team_department")
public class Department extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String name;

    private String description;

    @Column(name = "department_type")
    private String type;

    // 关联上级部门
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Department parent;

    // 关联下级部门（集合形式）
    @Builder.Default
    @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Department> children = new HashSet<>();

    // 使用Set防止重复加入
    // @JsonIgnore
    // @Builder.Default
    // @ManyToMany(mappedBy = "departments", fetch = FetchType.LAZY)
    // private Set<Member> members = new HashSet<>();

    // @JsonIgnore
    // @ManyToOne(fetch = FetchType.LAZY)
    // // @JoinColumn(name = "organization_id", foreignKey = @ForeignKey(name =
    // "none", value = ConstraintMode.NO_CONSTRAINT))
    // @JsonBackReference("organization-departments")
    // private Organization organization;
    // private String orgUid;

    /** created by */
    // @JsonIgnore
    // @ManyToOne(fetch = FetchType.LAZY)
    // // @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "none",
    // value = ConstraintMode.NO_CONSTRAINT))
    // private User user;

    public void addChild(Department child) {
        children.add(child);
        child.setParent(this);
    }

    public void removeChild(Department child) {
        children.remove(child);
        child.setParent(null);
    }

    // public void addMember(Member member) {
    // members.add(member);
    // member.addDepartment(this);
    // }

    // public void removeMember(Member member) {
    // members.remove(member);
    // member.removeDepartment(this);
    // }

}
