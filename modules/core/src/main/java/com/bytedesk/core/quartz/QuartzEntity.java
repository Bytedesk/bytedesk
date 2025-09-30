/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-13 20:43:01
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-23 11:11:46
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.quartz;

import com.bytedesk.core.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Quartz job entity for scheduled task management
 * Manages cron jobs, scheduled tasks, and job execution configurations
 * 
 * Database Table: bytedesk_core_quartz
 * Purpose: Stores job definitions, trigger configurations, and execution schedules
 */
@Data
@Entity
@Builder
@EqualsAndHashCode(callSuper=false)
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_core_quartz")
public class QuartzEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;


    /**
     * Name of the scheduled job
     */
    @Column(nullable = false)
    private String jobName;

    /**
     * Group name for organizing related jobs
     */
    @Column(nullable = false)
    private String jobGroup;

    /**
     * Description of the job's purpose
     */
    private String description;

    /**
     * Fully qualified class name of the job implementation
     */
    @Column(nullable = false)
    private String jobClassName;
    
    /**
     * Method name to be executed in the job class
     */
    @Column(nullable = false)
    private String jobMethodName;

    /**
     * Cron expression defining the job execution schedule
     */
    private String cronExpression;

    /**
     * Whether the job should persist after completion
     */
    @Builder.Default
    @Column(name = "is_durable")
    private Boolean durable = true;

    /**
     * Whether the job should not run concurrently with itself
     */
    @Builder.Default
    @Column(name = "is_nonconcurrent")
    private Boolean nonconcurrent = true;

    /**
     * Whether the job should update data during execution
     */
    @Builder.Default
    @Column(name = "is_update_data")
    private Boolean updateData = false;

    /**
     * Name of the trigger for this job
     */
    @Column(nullable = false)
    private String triggerName;

    /**
     * Group name for organizing related triggers
     */
    @Column(nullable = false)
    private String triggerGroup;

    /**
     * Type of trigger (SIMPLE, CRON)
     */
    private String triggerType;

    /**
     * Current state of the trigger (WAITING, PAUSED, ACQUIRED)
     */
    private String triggerState;

    /**
     * Policy for handling misfired triggers
     */
    @Builder.Default
    private String misfirePolicy = QuartzConsts.MISFIRE_DEFAULT;

    /** belong to org */
    // @Column(nullable = false)
    // private String orgUid;
    
}
