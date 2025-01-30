package com.bytedesk.ticket.ticket.service;

import java.util.List;

import org.flowable.idm.api.Group;
import org.flowable.idm.api.IdmIdentityService;
import org.flowable.idm.api.User;
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
 * 组管理：
 * 同步业务系统组织架构
 * 管理工作组和角色
 * 
 * 权限分配
 * 关系管理：
 * 维护用户和组的关系
 * 支持多组织架构
 * 灵活的权限控制   
 * 这样就能将业务系统的用户和组织架构与 Flowable 的工作流权限体系完美对接。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TicketIdentityService {

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
    public boolean isUserInGroup(String userId, String groupId) {
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
} 