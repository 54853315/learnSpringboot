/*
 * @Author: konakona konakona@crazyphper.com
 * @Date: 2022-05-12 16:03:46
 * @LastEditors: konakona konakona@crazyphper.com
 * @LastEditTime: 2022-05-25 14:58:10
 * @Description: 通用型查询常量
 * 
 * Copyright (c) 2022 by konakona konakona@crazyphper.com, All Rights Reserved. 
 */
package pers.learn.common.constant;

import lombok.Data;

@Data
public class Query {
    /**
     * 分页默认值
     * ! 使用String是因为@RequestParam的defaultValue只接收字符串型常量
     * 
     * @example : @RequestParam(value = "page", required = false, defaultValue =
     *          Query.DEFAULT_PAGE) Integer page
     */
    public final static String DEFAULT_PAGE = "1";
    public final static String DEFAULT_PAGE_SIZE = "15";

    /**
     * 获取默认页的整型
     * 
     * @example : listWithPage(Query.getDefaultPage2Int(),
     *          Query.getDefaultPageSize2Int())
     * @return Integer
     */
    public static Integer getDefaultPage2Int() {
        return Integer.parseInt(DEFAULT_PAGE);
    }

    /**
     * 获取默认页数的整型
     * 
     * @example : listWithPage(Query.getDefaultPage2Int(),
     *          Query.getDefaultPageSize2Int())
     * @return Integer
     */
    public static Integer getDefaultPageSize2Int() {
        return Integer.parseInt(DEFAULT_PAGE_SIZE);
    }
}
