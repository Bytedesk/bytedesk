/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-18 14:43:29
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-11 18:22:02
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.bytedesk.core.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;

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

/**
 * 
 */
@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "service_worktime")
public class Worktime extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "start_time")
    @JsonFormat(pattern = "HH:mm:ss")
    @Temporal(TemporalType.TIME)
    private Date startTime;

    @Column(name = "end_time")
    @JsonFormat(pattern = "HH:mm:ss")
    @Temporal(TemporalType.TIME)
    private Date endTime;
    
    /**
     * 是否工作时间
     */
    public boolean isWorkTime() {

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String timeString = formatter.format(new Date());

        Date now = null;
        try {
            now = formatter.parse(timeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calendarNow = Calendar.getInstance();
        calendarNow.setTime(now);
        calendarNow.set(0, 0, 0);

        Calendar calendarStart = Calendar.getInstance();
        calendarStart.setTime(startTime);
        calendarStart.set(0, 0, 0);

        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.setTime(endTime);
        calendarEnd.set(0, 0, 0);

        boolean result1 = calendarNow.getTime().after(calendarStart.getTime());
        boolean result2 = calendarNow.getTime().before(calendarEnd.getTime());

        // logger.info("startTime {}, nowTime {}, endTime {}, result1 {}, result2 {}",
        // calendarStart.getTime().toString(), calendarNow.getTime().toString(),
        // calendarEnd.getTime().toString(),
        // result1 ? "before true" : "before false",
        // result2 ? "end true" : "end false");

        return result1 && result2;
    }

}
