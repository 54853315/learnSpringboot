/*
 * @Author: konakona konakona@crazyphper.com
 * @Date: 2022-05-17 11:39:39
 * @LastEditors: konakona konakona@crazyphper.com
 * @LastEditTime: 2022-05-18 15:42:19
 * @Description: 
 * 
 * Copyright (c) 2022 by konakona konakona@crazyphper.com, All Rights Reserved. 
 */
package pers.learn.blog.mapper.vo;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import pers.learn.blog.entity.Article;

@Data
/**
 * 文章评论视觉层
 */
public class ArticleCommentVo implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String name;
    private Long articleId = 0L;
    private String content;
    // 文章信息字段，如果为null则不显示此字段
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String,Object> article;
}
