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
package com.bytedesk.service.worktime_settings;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BytedeskConsts;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
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

    @Builder.Default
    private Boolean enabled = true;

    @Builder.Default
    @Convert(converter = WorktimeSlotListConverter.class)
    @Column(name = "regular_worktimes", columnDefinition = "text")
    private List<WorktimeSlotValue> regularWorktimes = new ArrayList<>();

    @Builder.Default
    @Convert(converter = WorktimeSlotListConverter.class)
    @Column(name = "special_worktimes", columnDefinition = "text")
    private List<WorktimeSlotValue> specialWorktimes = new ArrayList<>();

    @Builder.Default
    @Column(length = BytedeskConsts.COLUMN_EXTRA_LENGTH)
    private String holidays = BytedeskConsts.EMPTY_JSON_STRING;

    /**
     * 检查当前时间是否在工作时间内
     * 
     * @return true 如果当前时间在工作时间内，false 如果不在
     */
    public Boolean isInWorktime() {
        if (Boolean.FALSE.equals(enabled)) {
            return true;
        }

        LocalDate today = LocalDate.now();

        if (holidays != null && !"{}".equals(holidays) && holidays.contains(today.toString())) {
            return isInSpecialWorktime(today);
        }

        if (isInSpecialWorktime(today)) {
            return true;
        }

        return isInRegularWorktime(today);
    }

    private Boolean isInSpecialWorktime(LocalDate date) {
        if (specialWorktimes == null || specialWorktimes.isEmpty()) {
            return true;
        }
        LocalTime now = LocalTime.now();
        return specialWorktimes.stream().anyMatch(slot -> slot.isActive(date, now));
    }

    private Boolean isInRegularWorktime(LocalDate date) {
        if (regularWorktimes == null || regularWorktimes.isEmpty()) {
            return true;
        }
        LocalTime now = LocalTime.now();
        return regularWorktimes.stream().anyMatch(slot -> slot.isActive(date, now));
    }

    public static WorktimeSettingEntity fromRequest(WorktimeSettingRequest request, ModelMapper modelMapper) {
        if (modelMapper == null || request == null) {
            return WorktimeSettingEntity.builder().build();
        }
        return modelMapper.map(request, WorktimeSettingEntity.class);
    }
}
