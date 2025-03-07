package com.example.getforexrate2.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {
    //基本上以DB的日期格式處理
    private static final SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyyMMdd");
    private static final SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat reqDateFormat = new SimpleDateFormat("yyyy/MM/dd");
    private static final SimpleDateFormat respDateFormat = new SimpleDateFormat("yyyyMMdd");

    static {
        // 設置時區為 UTC
        apiDateFormat.setTimeZone(TimeZone.getTimeZone("UTC+8"));
        dbDateFormat.setTimeZone(TimeZone.getTimeZone("UTC+8"));
        reqDateFormat.setTimeZone(TimeZone.getTimeZone("UTC+8"));
        respDateFormat.setTimeZone(TimeZone.getTimeZone("UTC+8"));
    }

    // 格式化日期為 yyyyMMdd -> yyyy-MM-dd HH:mm:ss
    public static Date apiToDbDateFormat(String strDate) {
        try {
            Date date = apiDateFormat.parse(strDate);
            String formattedDate = dbDateFormat.format(date);
            return dbDateFormat.parse(formattedDate);
        } catch (Exception e) {
            return null;
        }
    }

    // 驗證Request日期格式是否正確
    public static boolean isValidReqDate(String strDate) {
        try {
            reqDateFormat.parse(strDate);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    // 驗證日期區間是否符合1年前~當下日期-1天
    public static boolean isDateRangeValid(Date startDate, Date endDate) {
        try {

            // 計算一年前的日期
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.YEAR, -1);
            Date oneYearAgo = calendar.getTime();

            // 計算昨天的日期
            calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            Date yesterday = calendar.getTime();

            // 檢查日期區間是否在一年前到昨天之間
            return !startDate.before(oneYearAgo) && !endDate.after(yesterday) && !startDate.after(endDate);
        } catch (Exception e) {
            return false;
        }
    }

    // 格式化日期為 yyyy/MM/dd -> yyyy-MM-dd HH:mm:ss
    public static Date reqToDbDateFormat(String strDate) {
        try {
            return reqDateFormat.parse(strDate);
        } catch (Exception e) {
            return null;
        }
    }

    // 格式化日期為 yyyy-MM-dd HH:mm:ss -> yyyyMMdd
    public static String dbToRespDateFormat(Date dDate) {
        try {
            return respDateFormat.format(dDate);
        } catch (Exception e) {
            return null;
        }
    }

    // 日期為 yyyy-MM-dd HH:mm:ss 輸出為字串
    public static String dateToStr(Date dDate) {
        try {
            return dbDateFormat.format(dDate);
        } catch (Exception e) {
            return null;
        }
    }

}
