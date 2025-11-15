/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-14 09:40:35
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-05-14 14:48:53
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.quartz;

import com.bytedesk.core.base.BaseRequest;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class QuartzRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    /** 任务名称 */
    @NotBlank(message = "jobName is required")
    private String jobName;

    /** 任务组名 */
    @NotBlank(message = "jobGroup is required")
    private String jobGroup;

    // 
    private String description;

    /** 调用方法 */
    @NotBlank(message = "jobClassName is required")
    private String jobClassName;

    @NotBlank(message = "jobMethodName is required")
    private String jobMethodName;

    /** cron执行表达式 */
    @NotBlank(message = "cronExpression is required")
    private String cronExpression;

    //
    private Boolean durable;

    private Boolean nonconcurrent;

    private Boolean updateData;


    // triggers
    @NotBlank(message = "triggerName is required")
    private String triggerName;// 执行时间

    @NotBlank(message = "triggerGroup is required")
    private String triggerGroup;//

    private String triggerType;//

    private String triggerState;// 任务状态

    /** cron计划策略 */
    @Builder.Default
    private String misfirePolicy = QuartzConsts.MISFIRE_DEFAULT;

    /** belong to org */
    // @NotBlank(message = "orgUid is required")
    // private String orgUid;
}
