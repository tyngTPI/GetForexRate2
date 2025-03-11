package com.example.getforexrate2.util;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;

/**
 * 處理日期格式轉換和驗證
 * 基本上以DB的日期格式處理，前端以字串呈現
 */
public class DateUtil {

    private static SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyyMMdd"); //外匯API用的日期格式
    private static SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //資料庫日期格式(以字串表示)
    private static SimpleDateFormat reqDateFormat = new SimpleDateFormat("yyyy/MM/dd"); //Request請求用的日期格式
    private static SimpleDateFormat respDateFormat = new SimpleDateFormat("yyyyMMdd"); //Response回應用的日期格式

    static {
        // 設置時區為 UTC+8 (台灣時區)
        apiDateFormat.setTimeZone(TimeZone.getTimeZone("UTC+8"));
        dbDateFormat.setTimeZone(TimeZone.getTimeZone("UTC+8"));
        reqDateFormat.setTimeZone(TimeZone.getTimeZone("UTC+8"));
        respDateFormat.setTimeZone(TimeZone.getTimeZone("UTC+8"));
    }

    /**
     * 將 外匯API 回應中的日期字串（yyyyMMdd）轉換為資料庫儲存的日期格式（yyyy-MM-dd HH:mm:ss）
     */
    public static Date apiToDbDateFormat(String strDate) {
        try {
            Date date = apiDateFormat.parse(strDate);
            String formattedDate = dbDateFormat.format(date);
            return dbDateFormat.parse(formattedDate);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 驗證Request參數中的日期字串是否符合指定的日期格式（yyyy/MM/dd）
     */
    public static boolean isValidReqDate(String strDate) {
        try {
            reqDateFormat.parse(strDate);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 驗證日期區間是否在當下日期一年前到昨天之間
     */
    public static boolean isDateRangeValid(Date startDate, Date endDate) {
        try {

            // 將 Date 轉換為 LocalDate
            LocalDate localStartDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate localEndDate = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate today = LocalDate.now();
            LocalDate oneYearAgo = today.minus(Period.ofYears(1));
            LocalDate yesterday = today.minus(Period.ofDays(1));

            // 檢查日期區間是否在一年前到昨天之間
            return !localStartDate.isBefore(oneYearAgo) && !localEndDate.isAfter(yesterday) && !localStartDate.isAfter(localEndDate);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 將Request參數中的日期字串（yyyy/MM/dd）轉換為資料庫儲存的日期格式（yyyy-MM-dd HH:mm:ss）
     */
    public static Date reqToDbDateFormat(String strDate) {
        try {
            return reqDateFormat.parse(strDate);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 將資料庫儲存的日期格式（yyyy-MM-dd HH:mm:ss）轉換為Response資料中的日期格式（yyyyMMdd）
     */
    public static String dbToRespDateFormat(Date dDate) {
        try {
            return respDateFormat.format(dDate);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 將日期格式化為字串 (yyyy-MM-dd HH:mm:ss)
     */
    public static String dateToStr(Date dDate) {
        try {
            return dbDateFormat.format(dDate);
        } catch (Exception e) {
            return null;
        }
    }

}
