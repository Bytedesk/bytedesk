package com.bytedesk.core.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.StringUtils;

import lombok.experimental.UtilityClass;

/**
 * 时间常用函数
 */
@UtilityClass
public class BdDateUtils {

    private static final String datetimeFormat = "yyyy-MM-dd HH:mm:ss";

    private static final String datetimeUidFormat = "yyyyMMddHHmmss";

    private static final String dateFormat = "yyyy-MM-dd";

    private static final String dateFormatSlash = "yyyy/MM/dd";

    private static final String dateFormatSlashNoZero = "yyyy/M/d";

    private static final String timeFormat = "HH:mm:ss";

    public static String formatDatetimeNow() {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(datetimeFormat);
        return dateFormatter.format(new Date());
    }

    public static String formatDatetimeToString(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(datetimeFormat);
        return formatter.format(date);
    }

    /**
     * 将 LocalDateTime 转换为格式化的日期时间字符串
     * @param localDateTime LocalDateTime对象
     * @return 格式化的日期时间字符串 (yyyy-MM-dd HH:mm:ss)
     */
    public static String formatDatetimeToString(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        // 使用 DateTimeFormatter 直接格式化 LocalDateTime
        // return localDateTime.format(DateTimeFormatter.ofPattern(datetimeFormat));
        
        // 使用应用配置的时区
        // ZonedDateTime zonedDateTime = localDateTime.atZone(LocaleContextHolder.getTimeZone().toZoneId());
        // 或者固定使用中国时区
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of("Asia/Shanghai"));
        return DateTimeFormatter.ofPattern(datetimeFormat).format(zonedDateTime);
        
        // 或者如果一定要使用 SimpleDateFormat，可以这样转换：
        // try {
        //     Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        //     SimpleDateFormat formatter = new SimpleDateFormat(datetimeFormat);
        //     return formatter.format(date);
        // } catch (Exception e) {
        //     return null;
        // }
    }

    public static Date formatStringToDateTime(String date) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(datetimeFormat);
        try {
            return dateFormatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String formatDatetimeUid() {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(datetimeUidFormat);
        return dateFormatter.format(new Date());
    }

    public static String formatToday() {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat);
        return dateFormatter.format(new Date());
    }

    public static Date formatStringToTime(String time) {
        if (!StringUtils.hasText(time)) {
            return null;
        }
        SimpleDateFormat formatter = new SimpleDateFormat(timeFormat);
        try {
            return formatter.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String formatTimeToString(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(timeFormat);
        return formatter.format(date);
    }

    public static String formatDateSlashNow() {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormatSlash);
        return dateFormatter.format(new Date());
    }

    public static String formatDateSlashNowNoZero() {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormatSlashNoZero);
        return dateFormatter.format(new Date());
    }

    public static String formatDateToString(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        return simpleDateFormat.format(date);
    }

    public static Date formatStringToDate(String date) {
        if (!StringUtils.hasText(date)) {
            return null;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        try {
            return simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
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
        // String str = "Wed Apr 22 05:12:10 CST 2020";
        // SimpleDateFormat sdf1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy",
        // Locale.ENGLISH);

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
            e.printStackTrace();
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

    // 将英文日期格式 转未 中文日期格式
    // 日期 + 1
    public static String transformDateForVoa(String enDate) {
        String cnDate = transformEnDateToCnDate(enDate);
        return dateStringAddOneDayToString(cnDate);
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
    
    public static LocalDateTime parseLocalDateTime(String dateTime) {
        return LocalDateTime.parse(dateTime, BdDateUtils.getDateTimeFormatter());
    }

    public static LocalDateTime parseLocalDateTime(String dateTime, DateTimeFormatter formatter) {
        return LocalDateTime.parse(dateTime, formatter);
    }

    public static String getCurrentLocalDateTime() {
        return LocalDateTime.now().atZone(LocaleContextHolder.getTimeZone().toZoneId()).toString();
    }

    public static ZonedDateTime getCurrentZonedDateTime() {
        return LocalDateTime.now().atZone(LocaleContextHolder.getTimeZone().toZoneId());
    }

    public static ZoneId getCurrentZoneId() {
        return LocaleContextHolder.getTimeZone().toZoneId();
    }
    

}
