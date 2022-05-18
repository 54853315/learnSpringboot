/*
 * @Author: konakona konakona@crazyphper.com
 * @Date: 2022-05-12 17:38:32
 * @LastEditors: konakona konakona@crazyphper.com
 * @LastEditTime: 2022-05-17 19:03:27
 * @Description: 
 * 
 * Copyright (c) 2022 by konakona konakona@crazyphper.com, All Rights Reserved. 
 */
package pers.learn.blog.util;

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
