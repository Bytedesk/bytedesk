/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-20 12:03:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-03 12:17:02
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.utils.tools;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.context.i18n.LocaleContextHolder;

import lombok.extern.slf4j.Slf4j;

/**
 * https://docs.spring.io/spring-ai/reference/api/tools.html
 */
@Slf4j
public class DateTimeTools {

    @Tool(description = "Get the current date and time in the user's timezone")
    public String getCurrentDateTime() {
        // 获取当前时区
        TimeZone timeZone = LocaleContextHolder.getTimeZone();
        log.info("timeZone: {}", timeZone);
        // Convert the current time to the date and time of the specified time zone
        return ZonedDateTime.now(ZoneId.of(timeZone.getID())).toString();
    }

    @Tool(description = "Set a user alarm for the given time")
    void setAlarm(@ToolParam(description = "Time in ISO-8601 format") String time) {
        ZonedDateTime alarmTime = ZonedDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME);
        log.info("Alarm set for " + alarmTime);
    }

    // MethodToolCallback
    String getCurrentDateTimeMethodToolCallback() {
        return ZonedDateTime.now().withZoneSameInstant(LocaleContextHolder.getTimeZone().toZoneId()).toString();
    }

}
