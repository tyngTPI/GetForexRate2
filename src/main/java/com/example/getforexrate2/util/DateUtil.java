package com.example.getforexrate2.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    //基本上以DB的日期格式處理

    // 格式化日期為 yyyyMMdd -> yyyy-MM-dd HH:mm:ss
    public static String apiToDbDateFormat(String strDate) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = inputFormat.parse(strDate);
            return outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 驗證Request日期格式是否正確
    public static boolean isValidReqDate(String strDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            sdf.parse(strDate);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 驗證日期區間是否合法
    public static boolean isDateRangeValid(String startDate, String endDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date start = sdf.parse(startDate);
            Date end = sdf.parse(endDate);

            // 計算一年前的日期
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.YEAR, -1);
            Date oneYearAgo = calendar.getTime();

            // 計算昨天的日期
            calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            Date yesterday = calendar.getTime();

            // 檢查日期區間是否在一年前到昨天之間
            return !start.before(oneYearAgo) && !end.after(yesterday) && !start.after(end);
        } catch (Exception e) {
            return false;
        }
    }

    // 格式化日期為 yyyy/MM/dd -> yyyy-MM-dd HH:mm:ss
    public static String reqToDbDateFormat(String strDate) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy/MM/dd");
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return outputFormat.format(inputFormat.parse(strDate));
        } catch (Exception e) {
            return null;
        }
    }

    // 格式化日期為 yyyy-MM-dd HH:mm:ss -> yyyyMMdd
    public static String dbToRespDateFormat(String strDate) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyyMMdd");
            return outputFormat.format(inputFormat.parse(strDate));
        } catch (Exception e) {
            return null;
        }
    }

    //取得下一天
    public static String getNextDay(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(sdf.parse(date));
            calendar.add(Calendar.DAY_OF_MONTH, 1); // 加一天
            return sdf.format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
