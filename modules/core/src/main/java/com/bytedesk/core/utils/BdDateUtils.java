package com.bytedesk.core.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.springframework.util.StringUtils;

/**
 * 时间常用函数
 */
public class BdDateUtils {

    private BdDateUtils() {
    }

    private static final String datetimeFormat = "yyyy-MM-dd HH:mm:ss";

    private static final String dtFormat = "yyyy-MM-dd";

    private static final String dtFormatSlash = "yyyy/MM/dd";

    private static final String dtFormatSlashNoZero = "yyyy/M/d";

    public static String formatDatetimeNow() {
        SimpleDateFormat dateFormater = new SimpleDateFormat(datetimeFormat);
        return dateFormater.format(new Date());
    }

    public static String formatDatetimeToString(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(datetimeFormat);
        return formatter.format(date);
    }

    public static Date formatStringToDateTime(String date) {
        SimpleDateFormat dateFormater = new SimpleDateFormat(datetimeFormat);
        try {
            return dateFormater.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String formatDateNow() {
        SimpleDateFormat dateFormater = new SimpleDateFormat(dtFormat);
        return dateFormater.format(new Date());
    }

    public static String formatDateSlashNow() {
        SimpleDateFormat dateFormater = new SimpleDateFormat(dtFormatSlash);
        return dateFormater.format(new Date());
    }

    public static String formatDateSlashNowNoZero() {
        SimpleDateFormat dateFormater = new SimpleDateFormat(dtFormatSlashNoZero);
        return dateFormater.format(new Date());
    }

    public static String formatDateToString(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(dtFormat);
        return formatter.format(date);
    }

    public static Date formatStringToDate(String date) {
        if (!StringUtils.hasLength(date)) {
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(dtFormat);
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 将英文日期格式 转未 中文日期格式
    // 29 January, 2017
    // enDate:29 January,2017, cnDate 2017-01-29
    public static String transformEndateToCnDate(String enDate) {
        if (!StringUtils.hasLength(enDate)) {
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
        SimpleDateFormat cnFormat = new SimpleDateFormat(dtFormat);
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
        if (!StringUtils.hasLength(dateString)) {
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
        String cnDate = transformEndateToCnDate(enDate);
        return dateStringAddOneDayToString(cnDate);
    }

    public static int getHourOfDay() {
        Calendar cal = Calendar.getInstance();//使用日历类
        cal.setTime(new Date());
        return cal.get(Calendar.HOUR_OF_DAY);
    }

}
