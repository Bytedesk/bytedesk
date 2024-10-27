/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:20:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-22 12:20:51
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.team.member;

import java.util.HashSet;
import java.util.Set;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.team.department.DepartmentEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true, exclude = { "departments" })
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({ MemberEntityListener.class })
// @DiscriminatorValue("Member")
@Table(name = "bytedesk_team_member", uniqueConstraints = {
    @UniqueConstraint(columnNames = { "email", "orgUid" }),
    @UniqueConstraint(columnNames = { "mobile", "orgUid" })
})
public class MemberEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String nickname;

    @Builder.Default
    private String avatar = AvatarConsts.DEFAULT_AVATAR_URL;

    @Builder.Default
    private String description = I18Consts.I18N_USER_DESCRIPTION;

    private String jobNo;

    private String jobTitle; // 职位

    private String seatNo;

    private String telephone;

    @Email(message = "email format error")
    private String email;

    private String mobile;

    @Builder.Default
    private MemberStatusEnum status = MemberStatusEnum.PENDING;

    @JsonIgnore
    // 关联多个Department
    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<DepartmentEntity> departments = new HashSet<>();

    /**
     * login user info
     */
    // @JsonIgnore
    // @OneToOne(fetch = FetchType.EAGER)
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

     // 添加、移除部门的方法
    public void addDepartment(DepartmentEntity department) {
        departments.add(department);
        // department.getMembers().add(this); // 假设Department类中有getMembers()方法返回成员列表
    }

    public void removeDepartment(DepartmentEntity department) {
        departments.remove(department);
        // department.getMembers().remove(this); // 假设Department类中有getMembers()方法返回成员列表
    }

}
