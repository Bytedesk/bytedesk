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

@Data
@Entity
@Builder
@EqualsAndHashCode(callSuper=false)
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_core_quartz")
public class QuartzEntity extends BaseEntity {

    // job details
    /** 任务名称 */
    @Column(nullable = false)
    private String jobName;

    /** 任务组名 */
    @Column(nullable = false)
    private String jobGroup;

    // 
    private String description;

    /** 调用目标字符串 */
    @Column(nullable = false)
    private String jobClassName;
    @Column(nullable = false)
    private String jobMethodName;

    /** cron执行表达式 */
    private String cronExpression;

    //
    @Builder.Default
    @Column(name = "is_durable")
    private Boolean durable = true;

    @Builder.Default
    @Column(name = "is_nonconcurrent")
    private Boolean nonconcurrent = true;

    @Builder.Default
    @Column(name = "is_update_data")
    private Boolean updateData = false;

    // triggers
    @Column(nullable = false)
    private String triggerName;// 执行时间

    @Column(nullable = false)
    private String triggerGroup;//

    // simple/cron
    private String triggerType;//

    // waiting/paused/aquired
    private String triggerState;// 任务状态

    /** cron计划策略 */
    @Builder.Default
    private String misfirePolicy = QuartzConsts.MISFIRE_DEFAULT;

    /** belong to org */
    // @Column(nullable = false)
    // private String orgUid;
    
}
