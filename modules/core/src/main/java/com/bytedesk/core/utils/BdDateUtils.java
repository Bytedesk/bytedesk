package com.bytedesk.core.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.StringUtils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * 时间常用函数
 */
@Slf4j
@UtilityClass
public class BdDateUtils {

    private static final String datetimeFormat = "yyyy-MM-dd HH:mm:ss";

    private static final String datetimeUidFormat = "yyyyMMddHHmmss";

    private static final String dateFormat = "yyyy-MM-dd";

    private static final String dateFormatSlash = "yyyy/MM/dd";

    private static final String dateFormatSlashNoZero = "yyyy/M/d";

    private static final String timeFormat = "HH:mm:ss";

    private static String displayZoneId = "Asia/Shanghai";

    public static void setDisplayZoneId(String zoneId) {
        displayZoneId = zoneId;
    }

    public static ZoneId getDisplayZoneId() {
        return ZoneId.of(displayZoneId != null ? displayZoneId : "Asia/Shanghai");
    }

    public static String formatDatetimeNow() {
        return new SimpleDateFormat(datetimeFormat).format(new Date());
    }

    public static String formatDatetimeToString(Date date) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat(datetimeFormat).format(date);
    }

    /**
     * 将 ZonedDateTime 转换为格式化的日期时间字符串
     * 使用中国时区进行转换，与toTimestamp方法保持一致
     * @param zonedDateTime ZonedDateTime对象
     * @return 格式化的日期时间字符串 (yyyy-MM-dd HH:mm:ss)
     */
    public static String formatDatetimeToString(ZonedDateTime zonedDateTime) {
        if (zonedDateTime == null) {
            return null;
        }
        // 如果已经是 Asia/Shanghai 时区，直接格式化
        if (zonedDateTime.getZone().equals(getDisplayZoneId())) {
            return DateTimeFormatter.ofPattern(datetimeFormat).format(zonedDateTime);
        }
        // 否则转换为 Asia/Shanghai 时区
        ZonedDateTime chinaTime = zonedDateTime.toInstant().atZone(getDisplayZoneId());
        return DateTimeFormatter.ofPattern(datetimeFormat).format(chinaTime);
    }

    public static String formatDateToString(ZonedDateTime zonedDateTime) {
        if (zonedDateTime == null) {
            return null;
        }
        // 如果已经是 Asia/Shanghai 时区，直接格式化
        if (zonedDateTime.getZone().equals(getDisplayZoneId())) {
            return DateTimeFormatter.ofPattern(dateFormat).format(zonedDateTime);
        }
        // 否则转换为 Asia/Shanghai 时区
        ZonedDateTime chinaTime = zonedDateTime.toInstant().atZone(getDisplayZoneId());
        return DateTimeFormatter.ofPattern(dateFormat).format(chinaTime);
    }

    public static Date formatStringToDateTime(String date) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(datetimeFormat);
        try {
            return dateFormatter.parse(date);
        } catch (ParseException e) {
            log.error("解析日期时间失败: {}", date, e);
        }
        return null;
    }

    public static String formatDatetimeUid() {
        return new SimpleDateFormat(datetimeUidFormat).format(new Date());
    }

    public static String formatToday() {
        return new SimpleDateFormat(dateFormat).format(new Date());
    }

    public static Date formatStringToTime(String time) {
        if (!StringUtils.hasText(time)) {
            return null;
        }
        SimpleDateFormat formatter = new SimpleDateFormat(timeFormat);
        try {
            return formatter.parse(time);
        } catch (ParseException e) {
            log.error("解析时间失败: {}", time, e);
        }
        return null;
    }

    public static String formatTimeToString(Date date) {
        return new SimpleDateFormat(timeFormat).format(date);
    }

    public static String formatDateSlashNow() {
        return new SimpleDateFormat(dateFormatSlash).format(new Date());
    }

    public static String formatDateSlashNowNoZero() {
        return new SimpleDateFormat(dateFormatSlashNoZero).format(new Date());
    }

    public static String formatDateToString(Date date) {
        return new SimpleDateFormat(dateFormat).format(date);
    }

    public static Date formatStringToDate(String date) {
        if (!StringUtils.hasText(date)) {
            return null;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        try {
            return simpleDateFormat.parse(date);
        } catch (ParseException e) {
            log.error("解析日期失败: {}", date, e);
        }
        return null;
    }

    // 将英文日期格式 转未 中文日期格式
    // 29 January, 2017
    // enDate:29 January,2017, cnDate 2017-01-29
    public static String transformEnDateToCnDate(String enDate) {
        if (!StringUtils.hasText(enDate)) {
            return "";
        }
        SimpleDateFormat fromFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH);
        if (enDate.contains(",")) {
            fromFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH);
        } else if (enDate.contains("-")) {
            // 无需转换
            return enDate;
        } else {
            fromFormat = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
        }
        //
        SimpleDateFormat cnFormat = new SimpleDateFormat(dateFormat);
        Date parse = null;
        try {
            parse = fromFormat.parse(enDate);
        } catch (ParseException e) {
            log.error("解析英文日期失败: {}", enDate, e);
        }
        return cnFormat.format(parse);
    }

    // 日期+1
    public static String dateStringAddOneDayToString(String dateString) {
        if (!StringUtils.hasText(dateString)) {
            return "";
        }
        Date date = formatStringToDate(dateString);
        date = dateAddOneDay(date);
        return formatDateToString(date);
    }

    // 日期加+1天
    public static Date dateAddOneDay(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1); // 把日期往后增加一天,整数 往后推,负数往前移动
        return calendar.getTime(); // 这个时间就是日期往后推一天的结果
    }

    public static int getHourOfDay() {
        Calendar cal = Calendar.getInstance();// 使用日历类
        cal.setTime(new Date());
        return cal.get(Calendar.HOUR_OF_DAY);
    }

    public static DateTimeFormatter getDateTimeFormatter() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }

    public static DateTimeFormatter getDateFormatter() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd");
    }
    
    public static ZonedDateTime parseZonedDateTime(String dateTime) {
        if (!StringUtils.hasText(dateTime)) {
            return null;
        }
        try {
            // 尝试直接解析为ZonedDateTime（如果包含时区信息）
            return ZonedDateTime.parse(dateTime, BdDateUtils.getDateTimeFormatter().withZone(getDisplayZoneId()));
        } catch (Exception e) {
            // 如果失败，尝试解析为LocalDateTime然后转换为ZonedDateTime
            try {
                return LocalDateTime.parse(dateTime, BdDateUtils.getDateTimeFormatter()).atZone(getDisplayZoneId());
            } catch (Exception e2) {
                // 如果还是失败，尝试解析为LocalDate然后转换为ZonedDateTime
                try {
                    return LocalDate.parse(dateTime, BdDateUtils.getDateFormatter()).atStartOfDay(getDisplayZoneId());
                } catch (Exception e3) {
                    throw new RuntimeException("Unable to parse date time: " + dateTime, e3);
                }
            }
        }
    }

    public static ZonedDateTime parseZonedDateTime(String dateTime, DateTimeFormatter formatter) {
        if (!StringUtils.hasText(dateTime)) {
            return null;
        }
        try {
            // 尝试直接解析为ZonedDateTime（如果包含时区信息）
            return ZonedDateTime.parse(dateTime, formatter.withZone(getDisplayZoneId()));
        } catch (Exception e) {
            // 如果失败，尝试解析为LocalDateTime然后转换为ZonedDateTime
            try {
                return LocalDateTime.parse(dateTime, formatter).atZone(getDisplayZoneId());
            } catch (Exception e2) {
                throw new RuntimeException("Unable to parse date time: " + dateTime, e2);
            }
        }
    }
    
    public static String getCurrentZonedDateTime() {
        return ZonedDateTime.now(LocaleContextHolder.getTimeZone().toZoneId()).toString();
    }

    public static ZoneId getCurrentZoneId() {
        return LocaleContextHolder.getTimeZone().toZoneId();
    }
    
    /**
     * 将ZonedDateTime转换为时间戳（毫秒）
     * 使用中国时区(Asia/Shanghai)进行转换
     * @param zonedDateTime ZonedDateTime对象
     * @return 时间戳（毫秒）
     */
    public static Long toTimestamp(ZonedDateTime zonedDateTime) {
        if (zonedDateTime == null) {
            return null;
        }
        // 使用中国时区进行转换，确保时间戳基于Asia/Shanghai时区
        return zonedDateTime.toInstant().atZone(getDisplayZoneId()).toInstant().toEpochMilli();
    }

    /**
     * 获取当前时间，使用Asia/Shanghai时区
     * @return 当前时间的ZonedDateTime对象
     */
    public static ZonedDateTime now() {
        return ZonedDateTime.now(getDisplayZoneId());
    }

    /**
     * 解析结束日期，如果是日期格式（不包含时间），则设置为当天的结束时间（23:59:59.999999999）
     * 这个方法专门用于处理查询条件中的结束日期
     * @param endDate 结束日期字符串，支持 "yyyy-MM-dd" 或 "yyyy-MM-dd HH:mm:ss" 格式
     * @return 解析后的ZonedDateTime对象，如果是日期格式则设置为当天结束时间
     */
    public static ZonedDateTime parseEndDate(String endDate) {
        if (!StringUtils.hasText(endDate)) {
            return null;
        }
        
        ZonedDateTime parsedDate = parseZonedDateTime(endDate);
        if (parsedDate == null) {
            return null;
        }
        
        // 如果是日期格式（不包含时间），则设置为当天的结束时间（23:59:59.999999999）
        if (endDate.length() == 10) { // "yyyy-MM-dd" 格式
            return parsedDate.withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        }
        
        return parsedDate;
    }

    /**
     * 解析开始日期，如果是日期格式（不包含时间），则设置为当天的开始时间（00:00:00.000000000）
     * 这个方法专门用于处理查询条件中的开始日期
     * @param startDate 开始日期字符串，支持 "yyyy-MM-dd" 或 "yyyy-MM-dd HH:mm:ss" 格式
     * @return 解析后的ZonedDateTime对象，如果是日期格式则设置为当天开始时间
     */
    public static ZonedDateTime parseStartDate(String startDate) {
        if (!StringUtils.hasText(startDate)) {
            return null;
        }
        
        ZonedDateTime parsedDate = parseZonedDateTime(startDate);
        if (parsedDate == null) {
            return null;
        }
        
        // 如果是日期格式（不包含时间），则设置为当天的开始时间（00:00:00.000000000）
        if (startDate.length() == 10) { // "yyyy-MM-dd" 格式
            return parsedDate.withHour(0).withMinute(0).withSecond(0).withNano(0);
        }
        
        return parsedDate;
    }

    // 获取当天日期
    public static String getCurrentDay() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

}
