/*
 * @Author: konakona konakona@crazyphper.com
 * @Date: 2022-05-17 11:36:19
 * @LastEditors: konakona konakona@crazyphper.com
 * @LastEditTime: 2022-05-18 15:47:24
 * @Description: 
 * 
 * Copyright (c) 2022 by konakona konakona@crazyphper.com, All Rights Reserved. 
 */
package pers.learn.system.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import pers.learn.system.entity.ArticleComment;
import pers.learn.system.mapper.vo.ArticleCommentVo;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ArticleCommentMapper extends BaseMapper<ArticleComment> {
    // 获取文章的评论分页数据，此处使用@ReslutMap无法满足collection的目的，请前往mapper.xml看逻辑
    IPage<ArticleCommentVo> selectPageWhitCondition(Page<ArticleComment> page,
            @Param(Constants.WRAPPER) Wrapper<ArticleComment> wrapper);

    // 所有文章的评论数据
    // 未使用
    @Select("select a.title,c.* from article AS a RIGHT JOIN article_comment AS c ON a.id = c.article_id where c.article_id=#{id} and a.release_time IS NOT NULL and a.deleted = 0")
    List<ArticleCommentVo> allFromArticle(Long articleId);
}
