/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:14:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-06 12:07:33
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.worktime.settings;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.service.worktime.WorktimeEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
// @EntityListeners({WorktimeSettingEntityListener.class})
@Table(name = "bytedesk_service_worktime_setting")
public class WorktimeSettingEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;


    private String name;

    @Builder.Default
    private String description = I18Consts.I18N_DESCRIPTION;

    // 工作时间设置是否启用
    @Builder.Default
    private Boolean enabled = true;

    // 默认工作时间列表（周一至周五 9:00-18:00）
    @Builder.Default
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorktimeEntity> regularWorktimes = new ArrayList<>();

    // 特殊工作时间列表（如节假日前后、促销活动期间等特殊工作时间）
    @Builder.Default
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorktimeEntity> specialWorktimes = new ArrayList<>();

    // 节假日设置（key=日期，value=节假日名称）
    @Builder.Default
    @Column(columnDefinition = "text")
    private String holidays = "{}";

    // 非工作时间是否启用机器人
    @Builder.Default
    private Boolean nonWorktimeRobotEnabled = true;

    // 非工作时间是否启用留言功能
    @Builder.Default
    private Boolean nonWorktimeLeaveMessageEnabled = true;

    // 工作时间通知设置
    @Builder.Default
    private Boolean worktimeNotificationEnabled = false;

    // 工作时间通知方式（邮件、短信、企业微信等）
    @Builder.Default
    private String worktimeNotificationType = "EMAIL";

    // 工作时间通知接收人
    @Builder.Default
    private String worktimeNotificationRecipients = BytedeskConsts.EMPTY_STRING;

    /**
     * 检查当前时间是否在工作时间内
     * 
     * @return true 如果当前时间在工作时间内，false 如果不在
     */
    public Boolean isInWorktime() {
        LocalDate today = LocalDate.now();
        
        // 1. 检查是否是节假日
        if (!holidays.equals("{}") && holidays.contains(today.toString())) {
            // 如果是节假日，检查是否有特殊工作时间安排
            return isInSpecialWorktime();
        }
        
        // 2. 检查是否在特殊工作时间内
        if (isInSpecialWorktime()) {
            return true;
        }
        
        // 3. 检查是否在正常工作时间内
        return isInRegularWorktime();
    }
    
    /**
     * 检查当前时间是否在特殊工作时间内
     */
    private Boolean isInSpecialWorktime() {
        if (specialWorktimes == null || specialWorktimes.isEmpty()) {
            return false;
        }
        return specialWorktimes.stream()
            .anyMatch(WorktimeEntity::isWorkTime);
    }
    
    /**
     * 检查当前时间是否在正常工作时间内
     */
    private Boolean isInRegularWorktime() {
        if (regularWorktimes == null || regularWorktimes.isEmpty()) {
            return false;
        }
        return regularWorktimes.stream()
            .anyMatch(WorktimeEntity::isWorkTime);
    }
}
