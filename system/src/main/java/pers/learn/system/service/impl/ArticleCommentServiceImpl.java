/*
 * @Author: konakona konakona@crazyphper.com
 * @Date: 2022-05-17 12:24:46
 * @LastEditors: konakona konakona@crazyphper.com
 * @LastEditTime: 2022-05-18 15:47:26
 * @Description: 
 * 
 * Copyright (c) 2022 by konakona konakona@crazyphper.com, All Rights Reserved. 
 */
package pers.learn.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import pers.learn.entity.ArticleComment;
import pers.learn.mapper.ArticleCommentMapper;
import pers.learn.mapper.vo.ArticleCommentVo;
import pers.learn.service.ArticleCommentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.NonNull;

@Service
public class ArticleCommentServiceImpl extends ServiceImpl<ArticleCommentMapper, ArticleComment>
        implements ArticleCommentService {

    @Autowired
    private ArticleCommentMapper articleCommentMapper;

    /**
     * 获取查询列表数据使用的查询构造器
     * 
     * @return LambdaQueryWrapper<Article>
     */
    private LambdaQueryWrapper<ArticleComment> generateCommonBusinessLogicWrapper() {
        LambdaQueryWrapper<ArticleComment> articleWrapper = new LambdaQueryWrapper<ArticleComment>();
        // articleWrapper.isNotNull(ArticleComment::getReleaseTime);
        return articleWrapper;
    }

    /**
     * 查询数据并返回列表与分页
     * 
     * @return IPage<ArticleCommentVo>
     */
    public IPage<ArticleCommentVo> listWithPage(@NonNull Integer currentPage, @NonNull Integer pageSize) {
        LambdaQueryWrapper<ArticleComment> articleWrapper = generateCommonBusinessLogicWrapper();
        Page<ArticleComment> page = new Page<ArticleComment>(currentPage, pageSize);
        IPage<ArticleCommentVo> result = articleCommentMapper.selectPageWhitCondition(page, articleWrapper);
        return result;
    }

}
