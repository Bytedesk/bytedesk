package com.bytedesk.ticket.agi.assign;

public enum AssignmentStrategy {
    ROUND_ROBIN,      // 轮询分配
    LEAST_ACTIVE,     // 分配给最少活跃工单的客服
    LOAD_BALANCED,    // 根据工作负载分配
    SKILL_BASED,      // 根据技能分配
    CATEGORY_BASED    // 根据分类分配
} 