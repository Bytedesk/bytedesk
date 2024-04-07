/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:20:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-02 16:36:48
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

import com.bytedesk.core.rbac.user.User;
import com.bytedesk.core.utils.AuditModel;
import com.bytedesk.team.department.Department;
import com.bytedesk.team.organization.Organization;
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
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "team_member")
public class Member extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * uuid
     */
    @Column(name = "mid", unique = true, nullable = false)
    private String mid;

    /**
     * job number
     * 工号
     */
    private String jobNo;

    /**
     * realname
     * 姓名
     */
    private String nickname;

    /**
     * seat no
     * 工位
     */
    private String seatNo;

    /**
     * telephone
     * 电话-分机号
     */
    private String telephone;

    /**
     * work email
     */
    @Email(message = "email format error")
    @Column(name = "email", unique = true)
    private String email;

    /**
     * department
     */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Department department;

    /**
     * login user info
     */
    // @JsonIgnore
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private User user;

    /**
     * belong to org
     */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Organization organization;

}
