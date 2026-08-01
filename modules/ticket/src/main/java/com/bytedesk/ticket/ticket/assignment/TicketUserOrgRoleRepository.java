/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-07-28
 * @Description: Repository for querying user-organization-role associations.
 * 
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.ticket.ticket.assignment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bytedesk.core.rbac.user.UserOrganizationRoleEntity;

/**
 * Lightweight repository to resolve role-based candidate pools for ticket assignment.
 */
public interface TicketUserOrgRoleRepository extends JpaRepository<UserOrganizationRoleEntity, Long> {

    @Query("""
            select distinct m.uid from com.bytedesk.core.rbac.user.UserOrganizationRoleEntity uor
            join uor.roles r
            join com.bytedesk.core.member.MemberEntity m on m.user.uid = uor.user.uid
            where uor.organization.uid = :orgUid
              and m.deleted = false
              and r.uid = :roleUid
            """)
    List<String> findMemberUidsByOrgUidAndRoleUid(@Param("orgUid") String orgUid, @Param("roleUid") String roleUid);
}
