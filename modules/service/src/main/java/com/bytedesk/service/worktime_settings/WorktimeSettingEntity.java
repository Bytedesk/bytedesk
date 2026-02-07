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
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;

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
     * 非工作时间提示（用于引导访客留言/等待）
     */
    @Builder.Default
    @Column(length = BytedeskConsts.COLUMN_EXTRA_LENGTH)
    private String nonWorktimeTip = I18Consts.I18N_DEFAULT_OFFLINE_MESSAGE;

    /**
     * 检查当前时间是否在工作时间内
     * 
     * @return true 如果当前时间在工作时间内，false 如果不在
     */
    public Boolean isInWorktime() {
        return isInWorktime(LocalDate.now(), LocalTime.now());
    }

    /**
     * 指定日期/时间判断是否处于工作时间内。
     * 语义：
     * - enabled=false：不限制，恒为 true
     * - holidays 命中：仅以 specialWorktimes 判定（可用于“节假日上班时段”）
     * - holidays 未命中：以 regularWorktimes 判定；regular 为空则视为不限制（true）
     */
    public Boolean isInWorktime(LocalDate date, LocalTime time) {
        if (Boolean.FALSE.equals(enabled)) {
            return true;
        }
        if (date == null || time == null) {
            return true;
        }

        if (isHoliday(date)) {
            return isInSpecialWorktime(date, time);
        }
        return isInRegularWorktime(date, time);
    }

    public boolean isHoliday(LocalDate date) {
        if (date == null) {
            return false;
        }
        if (!StringUtils.hasText(holidays) || BytedeskConsts.EMPTY_JSON_STRING.equals(holidays)) {
            return false;
        }
        try {
            JSONObject obj = JSON.parseObject(holidays);
            return obj != null && obj.containsKey(date.toString());
        } catch (Exception ignored) {
            // best-effort：保留旧格式（字符串包含）兼容
            return holidays.contains(date.toString());
        }
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
}
