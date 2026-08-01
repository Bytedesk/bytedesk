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

import java.time.LocalDate;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.I18Consts;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

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

    private static final long serialVersionUID = 1L;

    private String name;

    @Builder.Default
    private String description = I18Consts.I18N_DESCRIPTION;

    @Builder.Default
    @Column(name = "holiday_type")
    private String type = HolidayTypeEnum.OFFICIAL.name();

    @Column(name = "holiday_date")
    private LocalDate holidayDate;

    @Builder.Default
    @Column(name = "holiday_year")
    private Integer holidayYear = LocalDate.now().getYear();

    @Builder.Default
    @Column(name = "country_code")
    private String countryCode = "CN";

    @Builder.Default
    @Column(name = "is_off_day")
    private Boolean offDay = false;

    @Builder.Default
    @Column(name = "is_official")
    private Boolean official = false;

    @Column(name = "source_url", length = 512)
    private String sourceUrl;

    @Column(name = "holiday_key", length = 128)
    private String holidayKey;
    
}
