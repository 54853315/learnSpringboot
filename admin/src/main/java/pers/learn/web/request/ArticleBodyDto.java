/*
 * @Author: konakona konakona@crazyphper.com
 * @Date: 2022-05-12 17:14:36
 * @LastEditors: konakona konakona@crazyphper.com
 * @LastEditTime: 2022-05-16 17:14:01
 * @Description: 请求文章时的Body数据
 * 
 * Copyright (c) 2022 by konakona konakona@crazyphper.com, All Rights Reserved. 
 */
package pers.learn.web.request;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ArticleBodyDto {
    public String title;
    public String author;
    public String brief;
    public String content;
    public String releaseTime;
}
