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
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.constant.TypeConsts;

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
    @Column(name = "is_enabled")
    private Boolean enabled = true;

    @Builder.Default
    @Convert(converter = WorktimeSlotListConverter.class)
    @Column(name = "regular_worktimes", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<WorktimeSlotValue> regularWorktimes = defaultRegularWorktimes();

    @Builder.Default
    @Convert(converter = WorktimeSlotListConverter.class)
    @Column(name = "special_worktimes", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<WorktimeSlotValue> specialWorktimes = new ArrayList<>();

    // === 节假日规则 ===
    @Builder.Default
    @Column(name = "holiday_settings_enabled")
    private Boolean holidaySettingsEnabled = false;

    // 暂时不启用，统一使用默认值：国家=CN
    // @Builder.Default
    // @Column(name = "holiday_country_code", length = 8)
    // private String holidayCountryCode = "CN";

    // 暂时不启用 holidayScopeType，统一使用 ORG_ONLY（默认）
    // @Builder.Default
    // @Column(name = "holiday_scope_type", length = 32)
    // private String holidayScopeType = WorktimeHolidayScopeEnum.ORG_ONLY.name();

    // 暂时不启用，统一使用默认值：Asia/Shanghai
    // @Builder.Default
    // @Column(name = "timezone", length = 64)
    // private String timezone = "Asia/Shanghai";

    /**
     * 非工作时间提示（用于引导访客留言/等待）
     */
    @Builder.Default
    @Column(length = BytedeskConsts.COLUMN_EXTRA_LENGTH)
    private String nonWorktimeTip = I18Consts.I18N_DEFAULT_OFFLINE_MESSAGE;

    /**
     * 检查当前时间是否在工作时间内（不使用节假日信息，仅用于兼容旧调用）。
     * 新代码应通过 {@link WorktimeService#isInServiceTime} 获取统一判定。
     */
    public Boolean isInWorktime() {
        return isInWorktime(LocalDate.now(), LocalTime.now(), false);
    }

    /**
     * 仅负责时间段基础判断：
     * 1. enabled=false → true
     * 2. holidaySettingsEnabled=true 且 holiday=true → 使用 specialWorktimes
     * 3. 否则（holidaySettingsEnabled=false 或非节假日）→ 使用 regularWorktimes
     */
    public Boolean isInWorktime(LocalDate date, LocalTime time, boolean holiday) {
        if (Boolean.FALSE.equals(enabled)) {
            return true;
        }
        if (date == null || time == null) {
            return true;
        }
        if (Boolean.TRUE.equals(holidaySettingsEnabled) && holiday) {
            return isInSpecialWorktime(date, time);
        }
        return isInRegularWorktime(date, time);
    }

    private Boolean isInSpecialWorktime(LocalDate date, LocalTime now) {
        if (specialWorktimes == null || specialWorktimes.isEmpty()) {
            // specialWorktimes 表示“节假日上班时段”：为空时应视为不开放
            return false;
        }
        return specialWorktimes.stream().anyMatch(slot -> slot.isActive(date, now));
    }

    private Boolean isInRegularWorktime(LocalDate date, LocalTime now) {
        if (regularWorktimes == null || regularWorktimes.isEmpty()) {
            // regularWorktimes 为空表示不限制
            return true;
        }
        return regularWorktimes.stream().anyMatch(slot -> slot.isActive(date, now));
    }

    public static WorktimeSettingEntity fromRequest(WorktimeSettingRequest request, ModelMapper modelMapper) {
        if (modelMapper == null || request == null) {
            return WorktimeSettingEntity.builder().build();
        }
        return modelMapper.map(request, WorktimeSettingEntity.class);
    }

    /**
     * Default regular worktime: 09:00-18:00, Monday-Friday.
     * Used as builder default so newly created entities get a sensible initial value.
     */
    private static List<WorktimeSlotValue> defaultRegularWorktimes() {
        List<WorktimeSlotValue> list = new ArrayList<>();
        list.add(WorktimeSlotValue.builder()
                .startTime("09:00")
                .endTime("18:00")
                .workDays("1,2,3,4,5")
                .build());
        return list;
    }
}
