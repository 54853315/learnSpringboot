/*
 * @Author: konakona konakona@crazyphper.com
 * @Date: 2022-05-12 14:37:10
 * @LastEditors: konakona konakona@crazyphper.com
 * @LastEditTime: 2022-05-12 15:32:55
 * @Description: 文章概要-视觉层
 * 
 * Copyright (c) 2022 by konakona konakona@crazyphper.com, All Rights Reserved. 
 */
package pers.learn.web.controller.vo;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ArticleBriefVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String title;
    private String author;
    private String brief;
    public ArticleBriefVO(Long id, String title, String author, String brief) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.brief = brief;
    }
}
