/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-09-18 11:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-18 11:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.form_result;

/**
 * 表单结果状态枚举
 */
public enum FormResultStatusEnum {
    /**
     * 草稿状态 - 表单数据已保存但未正式提交
     */
    DRAFT,
    
    /**
     * 已提交状态 - 表单已正式提交，等待处理
     */
    SUBMITTED,
    
    /**
     * 处理中状态 - 表单结果正在被处理
     */
    PROCESSING,
    
    /**
     * 已处理状态 - 表单结果已完成处理
     */
    PROCESSED,
    
    /**
     * 需要补充状态 - 表单信息不完整，需要用户补充
     */
    PENDING_SUPPLEMENT,
    
    /**
     * 已关闭状态 - 表单处理流程已关闭
     */
    CLOSED,
    
    /**
     * 已拒绝状态 - 表单提交被拒绝
     */
    REJECTED;
    
    /**
     * 根据字符串查找对应的枚举常量
     * @param value 字符串值
     * @return 对应的枚举常量，找不到时返回SUBMITTED
     */
    public static FormResultStatusEnum fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return SUBMITTED;
        }
        
        for (FormResultStatusEnum status : FormResultStatusEnum.values()) {
            if (status.name().equalsIgnoreCase(value.trim())) {
                return status;
            }
        }
        return SUBMITTED; // 默认返回已提交状态
    }
    
    /**
     * 检查是否为最终状态（不可再变更）
     * @return true if it's a final status
     */
    public boolean isFinalStatus() {
        return this == PROCESSED || 
               this == CLOSED || 
               this == REJECTED;
    }
    
    /**
     * 检查是否为活跃状态（需要处理）
     * @return true if it's an active status
     */
    public boolean isActiveStatus() {
        return this == SUBMITTED || 
               this == PROCESSING || 
               this == PENDING_SUPPLEMENT;
    }
}