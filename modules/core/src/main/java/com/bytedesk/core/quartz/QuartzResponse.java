/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-14 09:40:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-10 14:28:45
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.quartz;

import com.bytedesk.core.base.BaseResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class QuartzResponse extends BaseResponse {
    private static final long serialVersionUID = 1L;

    
    /** 任务名称 */
    private String jobName;

    /** 任务组名 */
    private String jobGroup;

    // 
    private String description;

    /** 调用目标字符串 */
    private String jobClassName;
    private String jobMethodName;

    /** cron执行表达式 */
    private String cronExpression;

    //
    private Boolean durable;

    private Boolean nonconcurrent;

    private Boolean updateData;

    // triggers
    private String triggerName;// 执行时间

    private String triggerGroup;//

    private String triggerType;//

    private String triggerState;// 任务状态

    /** cron计划策略 */
    private String misfirePolicy;

    /** belong to org */
    // private String orgUid;

    // 
    // private ZonedDateTime updatedAt;
}

