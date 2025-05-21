package com.bytedesk.ticket.identity;

import java.util.List;
import java.util.ArrayList;

import org.flowable.idm.api.Group;
import org.flowable.idm.api.IdmIdentityService;
import org.flowable.idm.api.User;
import org.flowable.idm.api.Privilege;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.bytedesk.service.agent.AgentEntity;
// import com.bytedesk.service.agent.AgentRestService;
import com.bytedesk.service.workgroup.WorkgroupEntity;
// import com.bytedesk.service.workgroup.WorkgroupRestService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 工单用户和组管理
 * 使用场景：
 * 
 * 用户管理：
 * 同步业务系统用户到 Flowable
 * 管理用户信息和状态
 * 用户认证和授权
 * 
 * 组管理：
 * 同步业务系统组织架构
 * 管理工作组和角色
 * 权限分配
 * 
 * 关系管理：
 * 维护用户和组的关系
 * 支持多组织架构
 * 灵活的权限控制
 * 
 * 这样就能将业务系统的用户和组织架构与 Flowable 的工作流权限体系完美对接。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TicketIdentityService {

    // private final LDAPIdentityServiceImpl ldapIdentityService;
    private final IdmIdentityService identityService;
    // private final AgentRestService agentService;
    // private final WorkgroupRestService workgroupService;

    /**
     * 同步用户到Flowable
     */
    public void syncUser(AgentEntity agent) {
        // 检查用户是否存在
        User flowableUser = identityService.createUserQuery()
            .userId(agent.getUid())
            .singleResult();
            
        if (flowableUser == null) {
            // 创建新用户
            flowableUser = identityService.newUser(agent.getUid());
        }
        
        // 更新用户信息
        flowableUser.setFirstName(agent.getNickname());
        flowableUser.setEmail(agent.getEmail());
        identityService.saveUser(flowableUser);
        
        log.info("Synced user: {}", agent.getUid());
    }

    /**
     * 同步工作组到Flowable
     */
    public void syncWorkgroup(WorkgroupEntity workgroup) {
        // 检查组是否存在
        Group flowableGroup = identityService.createGroupQuery()
            .groupId(workgroup.getUid())
            .singleResult();
            
        if (flowableGroup == null) {
            // 创建新组
            flowableGroup = identityService.newGroup(workgroup.getUid());
        }
        
        // 更新组信息
        flowableGroup.setName(workgroup.getNickname());
        flowableGroup.setType("assignment");
        identityService.saveGroup(flowableGroup);
        
        log.info("Synced group: {}", workgroup.getUid());
    }

    /**
     * 同步用户和工作组的关系
     */
    public void syncMembership(String userId, String groupId) {
        // 添加用户到组
        identityService.createMembership(userId, groupId);
        log.info("Synced membership: {} -> {}", userId, groupId);
    }

    /**
     * 获取用户
     */
    public User getUser(String userId) {
        return identityService.createUserQuery()
            .userId(userId)
            .singleResult();
    }
    
    /**
     * 获取组
     */
    public Group getGroup(String groupId) {
        return identityService.createGroupQuery()
            .groupId(groupId)
            .singleResult();
    }

    /**
     * 查询用户所在的所有组
     */
    public List<Group> getUserGroups(String userId) {
        return identityService.createGroupQuery()
            .groupMember(userId)
            .list();
    }

    /**
     * 查询组内的所有用户
     */
    public List<User> getGroupUsers(String groupId) {
        return identityService.createUserQuery()
            .memberOfGroup(groupId)
            .list();
    }

    /**
     * 检查用户是否在指定组中
     */
    public Boolean isUserInGroup(String userId, String groupId) {
        return identityService.createGroupQuery()
            .groupId(groupId)
            .groupMember(userId)
            .count() > 0;
    }

    /**
     * 删除用户
     */
    public void deleteUser(String userId) {
        identityService.deleteUser(userId);
        log.info("Deleted user: {}", userId);
    }

    /**
     * 删除工作组
     */
    public void deleteGroup(String groupId) {
        identityService.deleteGroup(groupId);
        log.info("Deleted group: {}", groupId);
    }

    /**
     * 删除用户和组的关系
     */
    public void deleteMembership(String userId, String groupId) {
        identityService.deleteMembership(userId, groupId);
        log.info("Deleted membership: {} -> {}", userId, groupId);
    }

    /**
     * 检查用户认证状态
     */
    public Boolean checkAuthentication(String userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }
        return auth.getName().equals(userId);
    }

    /**
     * 为用户添加权限
     */
    public void addUserPrivilege(String userId, String privilegeName) {
        // 创建权限(如果不存在)
        Privilege privilege = identityService.createPrivilegeQuery()
            .privilegeName(privilegeName)
            .singleResult();
            
        if (privilege == null) {
            privilege = identityService.createPrivilege(privilegeName);
        }
        
        // 为用户添加权限
        identityService.addUserPrivilegeMapping(privilege.getId(), userId);
        log.info("Added privilege {} to user {}", privilegeName, userId);
    }

    /**
     * 为组添加权限
     */
    public void addGroupPrivilege(String groupId, String privilegeName) {
        Privilege privilege = identityService.createPrivilegeQuery()
            .privilegeName(privilegeName)
            .singleResult();
            
        if (privilege == null) {
            privilege = identityService.createPrivilege(privilegeName);
        }
        
        identityService.addGroupPrivilegeMapping(privilege.getId(), groupId);
        log.info("Added privilege {} to group {}", privilegeName, groupId);
    }

    /**
     * 获取用户的所有权限
     */
    public List<Privilege> getUserPrivileges(String userId) {
        return identityService.createPrivilegeQuery()
            .userId(userId)
            .list();
    }

    /**
     * 检查用户是否有指定权限
     */
    public Boolean hasPrivilege(String userId, String privilegeName) {
        return identityService.createPrivilegeQuery()
            .userId(userId)
            .privilegeName(privilegeName)
            .count() > 0;
    }

    /**
     * 删除用户权限
     */
    public void removeUserPrivilege(String userId, String privilegeName) {
        Privilege privilege = identityService.createPrivilegeQuery()
            .privilegeName(privilegeName)
            .singleResult();
            
        if (privilege != null) {
            identityService.deleteUserPrivilegeMapping(privilege.getId(), userId);
            log.info("Removed privilege {} from user {}", privilegeName, userId);
        }
    }

    /**
     * 删除组权限
     */
    public void removeGroupPrivilege(String groupId, String privilegeName) {
        Privilege privilege = identityService.createPrivilegeQuery()
            .privilegeName(privilegeName)
            .singleResult();
            
        if (privilege != null) {
            identityService.deleteGroupPrivilegeMapping(privilege.getId(), groupId);
            log.info("Removed privilege {} from group {}", privilegeName, groupId);
        }
    }

    /**
     * 获取用户的Spring Security权限
     */
    public List<SimpleGrantedAuthority> getUserAuthorities(String userId) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        
        // 添加用户直接权限
        List<Privilege> userPrivileges = getUserPrivileges(userId);
        for (Privilege privilege : userPrivileges) {
            authorities.add(new SimpleGrantedAuthority(privilege.getName()));
        }
        
        // 添加用户组权限
        List<Group> userGroups = getUserGroups(userId);
        for (Group group : userGroups) {
            authorities.add(new SimpleGrantedAuthority("GROUP_" + group.getId()));
        }
        
        return authorities;
    }

    /**
     * 从LDAP同步用户
     */
    public void syncUserFromLDAP(String userId) {
        // User ldapUser = ldapIdentityService.findUserById(userId);
        // if (ldapUser != null) {
        //     User flowableUser = identityService.newUser(ldapUser.getId());
        //     flowableUser.setFirstName(ldapUser.getFirstName());
        //     flowableUser.setLastName(ldapUser.getLastName());
        //     flowableUser.setEmail(ldapUser.getEmail());
        //     identityService.saveUser(flowableUser);
            
        //     // 同步用户组
        //     List<Group> ldapGroups = ldapIdentityService.findGroupsByUser(userId);
        //     for (Group ldapGroup : ldapGroups) {
        //         syncGroupFromLDAP(ldapGroup.getId());
        //         identityService.createMembership(userId, ldapGroup.getId());
        //     }
        // }
    }

    /**
     * 从LDAP同步组
     */
    public void syncGroupFromLDAP(String groupId) {
        // Group ldapGroup = ldapIdentityService.findGroupById(groupId);
        // if (ldapGroup != null) {
        //     Group flowableGroup = identityService.newGroup(ldapGroup.getId());
        //     flowableGroup.setName(ldapGroup.getName());
        //     flowableGroup.setType("assignment");
        //     identityService.saveGroup(flowableGroup);
        // }
    }

    /**
     * LDAP认证
     */
    public Boolean authenticate(String userId, String password) {
        // return ldapIdentityService.checkPassword(userId, password);
        return true;
    }
} 