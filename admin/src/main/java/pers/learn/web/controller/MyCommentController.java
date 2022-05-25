/*
 * @Author: konakona konakona@crazyphper.com
 * @Date: 2022-05-16 22:35:44
 * @LastEditors: konakona konakona@crazyphper.com
 * @LastEditTime: 2022-05-18 15:47:13
 * @Description: 查看我所有的评论，无论是给文章还是给谁的...
 * 
 * Copyright (c) 2022 by konakona konakona@crazyphper.com, All Rights Reserved. 
 */
package pers.learn.web.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import pers.learn.constant.Query;
import pers.learn.mapper.vo.ArticleCommentVo;
import pers.learn.response.CommonResponse;
import pers.learn.service.impl.ArticleCommentServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyCommentController {

    @Autowired
    private ArticleCommentServiceImpl articleCommentServiceImpl;

    @GetMapping(value = "/comment/article")
    /**
     * 我的文章评论列表
     * 每条评论会含有文章的概要信息
     * 
     * @example:
     *           {
     *           "records": [
     *           {
     *           "id": 1,
     *           "name": "a",
     *           "articleId": 11,
     *           "content": "b",
     *           "article": {
     *           "brief": "22",
     *           "author": "11",
     *           "id": 1,
     *           "viewCount": 2,
     *           "title": "abc11"
     *           }
     *           }
     *           ]
     *           }
     * 
     * @param page
     * @param perPage
     * @return CommonResponse<IPage<ArticleCommentVo>>
     */
    public CommonResponse<IPage<ArticleCommentVo>> listOfArticle(
            @RequestParam(value = "page", required = false, defaultValue = Query.DEFAULT_PAGE) Integer page,
            @RequestParam(value = "per_page", required = false, defaultValue = Query.DEFAULT_PAGE_SIZE) Integer perPage) {

        IPage<ArticleCommentVo> result = articleCommentServiceImpl.listWithPage(page, perPage);

        return CommonResponse.returnResult(result);
    }
}
