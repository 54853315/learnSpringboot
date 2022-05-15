package com.example.ME.util;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;


public class DateAdopter {

    /**
     * 将String时间格式化成LocalDateTime
     * 
     * @param {String} datetime
     * @return LocalDateTime
     */
    public static LocalDateTime str2LoclaDateTime(String dateStr) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime parsedDate = LocalDateTime.parse(dateStr, df);
        return parsedDate;
    }

    /**
     * 当前时间格式化后返回
     * 
     * @return String
     */
    public static String long2TimeStr() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(new Date());
    }
}
