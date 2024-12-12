/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-18 14:43:29
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-04 09:50:37
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.worktime;

import java.time.LocalDate;
import java.time.LocalTime;

import com.bytedesk.core.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@Entity
@Table(name = "bytedesk_worktime")
@EqualsAndHashCode(callSuper = true)
public class WorktimeEntity extends BaseEntity {

    @Builder.Default
    @Column(name = "start_time", nullable = false)
    private String startTime = "09:00";  // 开始时间

    @Builder.Default
    @Column(name = "end_time", nullable = false)
    private String endTime = "18:00";    // 结束时间

    @Builder.Default
    @Column(name = "work_days", nullable = false)
    private String workDays = "1,2,3,4,5";  // 工作日(1-7代表周一到周日)

    /**
     * 检查当前时间是否在工作时间内
     */
    public boolean isWorkTime() {
        // 1. 检查是否是工作日
        int dayOfWeek = LocalDate.now().getDayOfWeek().getValue();
        if (!workDays.contains(String.valueOf(dayOfWeek))) {
            return false;
        }

        // 2. 检查是否在工作时间段内
        LocalTime now = LocalTime.now();
        LocalTime start = LocalTime.parse(startTime);
        LocalTime end = LocalTime.parse(endTime);

        if (end.isAfter(start)) {
            // 普通情况: 09:00-18:00
            return now.isAfter(start) && now.isBefore(end);
        } else {
            // 跨天情况: 22:00-06:00
            return now.isAfter(start) || now.isBefore(end);
        }
    }
}
