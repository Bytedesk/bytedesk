/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:20:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-12 12:10:32
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.team.member;

import java.util.Set;
import java.util.HashSet;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.converter.StringSetConverter;
import com.bytedesk.core.rbac.user.UserEntity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true) // exclude = { "departments" }
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({ MemberEntityListener.class })
@Table(name = "bytedesk_team_member"
// 去掉表级别的唯一约束，使用代码级别的唯一约束
//, uniqueConstraints = {
//     @UniqueConstraint(columnNames = { "email", "orgUid", "is_deleted" }),
//     @UniqueConstraint(columnNames = { "mobile", "orgUid", "is_deleted" })
// }
)
public class MemberEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String nickname;

    @Builder.Default
    private String avatar = AvatarConsts.getDefaultUserAvatarUrl();

    @Builder.Default
    private String description = I18Consts.I18N_USER_DESCRIPTION;

    private String jobNo;

    private String jobTitle; // 职位

    private String seatNo;

    private String telephone;

    @Email(message = "email format error")
    private String email;

    // only support chinese mobile number, 
    // TODO: support other country mobile number using libphonenumber library
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "Invalid mobile number format")
    private String mobile;

    @Builder.Default
    private String status = MemberStatusEnum.PENDING.name();

    @Builder.Default
    @Convert(converter = StringSetConverter.class)
    @Column(length = 512)
	private Set<String> roleUids = new HashSet<>(); 

    // 一个人只能属于一个部门，一个部门可以有多个成员
    // 
    // // 关联多个Department
    // @Builder.Default
    // @ManyToMany(fetch = FetchType.LAZY)
    // private Set<DepartmentEntity> departments = new HashSet<>();
    private String deptUid;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

     // 添加、移除部门的方法
    // public void addDepartment(DepartmentEntity department) {
    //     departments.add(department);
    //     // department.getMembers().add(this); // 假设Department类中有getMembers()方法返回成员列表
    // }

    // public void removeDepartment(DepartmentEntity department) {
    //     departments.remove(department);
    //     // department.getMembers().remove(this); // 假设Department类中有getMembers()方法返回成员列表
    // }

}
