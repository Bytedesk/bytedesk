package com.bytedesk.ticket.agi.group;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMemberEntity, Long> {
    
    // 基本查询
    List<GroupMemberEntity> findByGroupIdAndEnabledTrue(Long groupId);
    Optional<GroupMemberEntity> findByGroupIdAndUserId(Long groupId, Long userId);
    
    // 可用性查询
    @Query("SELECT m FROM GroupMemberEntity m WHERE m.groupId = ?1 AND m.enabled = true " +
           "AND m.status = 'available' AND m.totalAssigned < m.maxTickets " +
           "ORDER BY m.lastAssignedAt ASC NULLS FIRST")
    List<GroupMemberEntity> findAvailableMembers(Long groupId);
    
    // 技能匹配查询
    @Query("SELECT m FROM GroupMemberEntity m WHERE m.groupId = ?1 AND m.enabled = true " +
           "AND m.status = 'available' AND m.skills LIKE %?2% " +
           "ORDER BY m.skillLevels DESC")
    List<GroupMemberEntity> findMembersBySkill(Long groupId, String skill);
    
    // 分类匹配查询
    @Query("SELECT m FROM GroupMemberEntity m WHERE m.groupId = ?1 AND m.enabled = true " +
           "AND m.status = 'available' AND m.categories LIKE %?2% " +
           "ORDER BY m.lastAssignedAt ASC")
    List<GroupMemberEntity> findMembersByCategory(Long groupId, String categoryId);
    
    // 优先级匹配查询
    @Query("SELECT m FROM GroupMemberEntity m WHERE m.groupId = ?1 AND m.enabled = true " +
           "AND m.status = 'available' AND " +
           "(m.maxPriority IS NULL OR m.maxPriority >= ?2)")
    List<GroupMemberEntity> findMembersByPriority(Long groupId, String priority);
    
    // 工作负载查询
    @Query("SELECT m FROM GroupMemberEntity m WHERE m.groupId = ?1 AND m.enabled = true " +
           "AND m.status = 'available' " +
           "ORDER BY (m.totalAssigned - m.totalResolved) ASC")
    List<GroupMemberEntity> findMembersByWorkload(Long groupId);
    
    // 统计查询
    @Query("SELECT COUNT(t) FROM TicketEntity t WHERE t.assignedTo = ?1 AND " +
           "t.status NOT IN ('resolved', 'closed')")
    int countActiveTickets(Long userId);
    
    @Query("SELECT AVG(t.firstResponseTime) FROM TicketEntity t WHERE " +
           "t.assignedTo = ?1 AND t.firstResponseTime IS NOT NULL")
    Double getAverageResponseTime(Long userId);
    
    @Query("SELECT AVG(t.resolutionTime) FROM TicketEntity t WHERE " +
           "t.assignedTo = ?1 AND t.resolutionTime IS NOT NULL")
    Double getAverageResolutionTime(Long userId);
    
    // 性能统计
    @Query("SELECT m FROM GroupMemberEntity m WHERE m.groupId = ?1 " +
           "ORDER BY m.avgSatisfaction DESC")
    Page<GroupMemberEntity> findTopPerformers(Long groupId, Pageable pageable);
    
    @Query("SELECT m FROM GroupMemberEntity m WHERE m.groupId = ?1 " +
           "AND m.avgResponseTime IS NOT NULL " +
           "ORDER BY m.avgResponseTime ASC")
    Page<GroupMemberEntity> findFastestResponders(Long groupId, Pageable pageable);
    
    // 状态更新
    @Query("UPDATE GroupMemberEntity m SET m.status = ?3 " +
           "WHERE m.groupId = ?1 AND m.userId = ?2")
    void updateMemberStatus(Long groupId, Long userId, String status);
    
    // 工作时间查询
    @Query("SELECT m FROM GroupMemberEntity m WHERE m.groupId = ?1 " +
           "AND m.enabled = true AND m.status = 'available' " +
           "AND m.workingHours LIKE %?2%")
    List<GroupMemberEntity> findMembersInWorkingHours(Long groupId, String timeSlot);
} 