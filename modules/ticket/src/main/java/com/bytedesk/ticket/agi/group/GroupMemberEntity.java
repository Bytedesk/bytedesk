package com.bytedesk.ticket.agi.group;

import com.bytedesk.core.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "bytedesk_ticket_group_member", 
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"group_id", "user_id"})
    })
@EqualsAndHashCode(callSuper = true)
public class GroupMemberEntity extends BaseEntity {

    @Column(name = "group_id", nullable = false)
    private Long groupId;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    private Integer role = 0;  // 0: 普通成员, 1: 管理员, 2: 组长
    
    @Column(name = "max_tickets")
    private Integer maxTickets = 10;  // 最大工单数
    
    @Column(name = "max_priority")
    private String maxPriority;  // 最高可处理优先级
    
    @Column(name = "categories")
    private String categories;  // 可处理的分类，逗号分隔
    
    @Column(name = "skills")
    private String skills;  // 技能标签，逗号分隔
    
    @Column(name = "skill_levels")
    private String skillLevels;  // 技能等级，格式：skill1:level1,skill2:level2
    
    @Column(name = "working_hours")
    private String workingHours;  // 工作时间，格式：1:0900-1800,2:0900-1800...
    
    @Column(name = "last_assigned_at")
    private LocalDateTime lastAssignedAt;  // 最后分配时间
    
    @Column(name = "last_active_at")
    private LocalDateTime lastActiveAt;  // 最后活跃时间
    
    @Column(name = "status")
    private String status = "available";  // available, busy, away, offline
    
    private Boolean enabled = true;
    
    // 统计字段
    @Column(name = "total_assigned")
    private Integer totalAssigned = 0;  // 总分配工单数
    
    @Column(name = "total_resolved")
    private Integer totalResolved = 0;  // 总解决工单数
    
    @Column(name = "avg_response_time")
    private Double avgResponseTime;  // 平均响应时间(分钟)
    
    @Column(name = "avg_resolution_time")
    private Double avgResolutionTime;  // 平均解决时间(分钟)
    
    @Column(name = "avg_satisfaction")
    private Double avgSatisfaction;  // 平均满意度
    
    // 辅助方法
    public boolean canHandleTicket(String priority, Long categoryId) {
        // 检查优先级
        if (maxPriority != null && !canHandlePriority(priority)) {
            return false;
        }
        
        // 检查分类
        if (categories != null && !canHandleCategory(categoryId)) {
            return false;
        }
        
        // 检查工作时间
        if (workingHours != null && !isWorkingHours()) {
            return false;
        }
        
        // 检查状态
        if (!"available".equals(status)) {
            return false;
        }
        
        return true;
    }
    
    private boolean canHandlePriority(String priority) {
        if (priority == null || maxPriority == null) {
            return true;
        }
        return getPriorityLevel(priority) <= getPriorityLevel(maxPriority);
    }
    
    private int getPriorityLevel(String priority) {
        return switch (priority.toLowerCase()) {
            case "urgent" -> 0;
            case "high" -> 1;
            case "normal" -> 2;
            case "low" -> 3;
            default -> 4;
        };
    }
    
    private boolean canHandleCategory(Long categoryId) {
        if (categoryId == null || categories == null || categories.isEmpty()) {
            return true;
        }
        return categories.contains(categoryId.toString());
    }
    
    private boolean isWorkingHours() {
        if (workingHours == null || workingHours.isEmpty()) {
            return true;
        }
        
        LocalDateTime now = LocalDateTime.now();
        int dayOfWeek = now.getDayOfWeek().getValue();
        String today = String.valueOf(dayOfWeek);
        
        // 解析工作时间
        for (String schedule : workingHours.split(",")) {
            String[] parts = schedule.split(":");
            if (parts[0].equals(today)) {
                String[] hours = parts[1].split("-");
                int start = Integer.parseInt(hours[0]);
                int end = Integer.parseInt(hours[1]);
                int current = now.getHour() * 100 + now.getMinute();
                return current >= start && current <= end;
            }
        }
        
        return false;
    }
    
    public int getSkillLevel(String skill) {
        if (skillLevels == null || skillLevels.isEmpty()) {
            return 0;
        }
        
        for (String skillLevel : skillLevels.split(",")) {
            String[] parts = skillLevel.split(":");
            if (parts[0].equals(skill)) {
                return Integer.parseInt(parts[1]);
            }
        }
        
        return 0;
    }
} 