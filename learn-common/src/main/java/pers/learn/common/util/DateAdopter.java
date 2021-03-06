/*
 * @Author: konakona konakona@crazyphper.com
 * @Date: 2022-05-12 17:38:32
 * @LastEditors: konakona konakona@crazyphper.com
 * @LastEditTime: 2022-05-17 19:03:27
 * @Description: 
 * 
 * Copyright (c) 2022 by konakona konakona@crazyphper.com, All Rights Reserved. 
 */
package pers.learn.common.util;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;


public class DateAdopter {

    public static final String PATTERN_DATETIME = "yyyy-MM-dd HH-mm-ss";

    /**
     * 将String时间格式化成LocalDateTime
     * 
     * @param {String} datetime
     * @return LocalDateTime
     */
    public static LocalDateTime str2LoclaDateTime(String dateStr) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(PATTERN_DATETIME);
        LocalDateTime parsedDate = LocalDateTime.parse(dateStr, df);
        return parsedDate;
    }

    /**
     * 当前时间格式化后返回
     * 
     * @return String
     */
    public static String long2TimeStr() {
        SimpleDateFormat df = new SimpleDateFormat(PATTERN_DATETIME);
        return df.format(new Date());
    }

    public static final String parseDateToStr(final Date date)
    {
        return new SimpleDateFormat(PATTERN_DATETIME).format(date);
    }

    public static final String parseDateToStr(final String format, final Date date)
    {
        return new SimpleDateFormat(format).format(date);
    }
}
