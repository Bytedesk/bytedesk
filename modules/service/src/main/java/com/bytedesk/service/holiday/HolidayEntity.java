/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-02 16:57:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-02 17:13:24
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.holiday;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.I18Consts;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.util.Date;

/**
 * 节假日设置
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
// @EntityListeners({HolidayEntityListener.class})
@Table(name = "bytedesk_service_holiday")
public class HolidayEntity extends BaseEntity {

    private String name;

    @Builder.Default
    private String description = I18Consts.I18N_DESCRIPTION;

    @Builder.Default
    @Column(name = "holiday_type", nullable = false)
    private String type = HolidayTypeEnum.CUSTOMER.name();
    
    // 节假日开始日期
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_date", nullable = false)
    private Date startDate;
    
    // 节假日结束日期
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_date", nullable = false)
    private Date endDate;
    
    // 是否全天，如果不是全天，则需要指定开始和结束时间
    @Builder.Default
    @Column(name = "all_day", nullable = false)
    private boolean allDay = true;
    
    // 开始时间（小时:分钟） 格式如 "09:00"
    @Column(name = "start_time")
    private String startTime;
    
    // 结束时间（小时:分钟） 格式如 "18:00"
    @Column(name = "end_time")
    private String endTime;
    
    // 重复类型：无重复、每天、每周、每月、每年
    @Builder.Default
    @Column(name = "repeat_type", nullable = false)
    private String repeatType = HolidayRepeatEnum.NONE.name();
    
    // 重复结束日期（可为空表示无限重复）
    @Temporal(TemporalType.DATE)
    @Column(name = "repeat_end_date")
    private Date repeatEndDate;
    
    // 对于每周重复，记录是周几（1-7，代表周一至周日）
    @Column(name = "weekday")
    private Integer weekday;
    
    // 对于每月重复，记录是每月几号
    @Column(name = "day_of_month")
    private Integer dayOfMonth;
    
    // 是否法定节假日
    @Builder.Default
    @Column(name = "is_official", nullable = false)
    private boolean isOfficial = false;
    
    // 是否是周末
    @Builder.Default
    @Column(name = "is_weekend", nullable = false)
    private boolean isWeekend = false;
    
    // 节假日期间客户提示信息
    @Column(name = "customer_notice", length = 500)
    private String customerNotice;
    
    // 节假日期间是否暂停服务
    @Builder.Default
    @Column(name = "service_paused", nullable = false)
    private boolean servicePaused = false;
    
    // 节假日期间替代的技能组ID（如果需要转到特定技能组）
    @Column(name = "alternative_group_id")
    private String alternativeGroupId;
    
    // 节假日期间替代的欢迎语
    @Column(name = "alternative_welcome", length = 500)
    private String alternativeWelcome;
    
    // 节假日期间是否启用自动回复
    @Builder.Default
    @Column(name = "auto_reply_enabled", nullable = false)
    private boolean autoReplyEnabled = false;
    
    // 自动回复内容
    @Column(name = "auto_reply_content", length = 1000)
    private String autoReplyContent;
}
