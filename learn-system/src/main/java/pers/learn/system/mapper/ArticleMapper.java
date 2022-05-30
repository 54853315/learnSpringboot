/*
 * @Author: konakona konakona@crazyphper.com
 * @Date: 2022-05-06 15:30:49
 * @LastEditors: konakona konakona@crazyphper.com
 * @LastEditTime: 2022-05-17 22:04:05
 * @Description: 
 * 
 * Copyright (c) 2022 by konakona konakona@crazyphper.com, All Rights Reserved. 
 */
package pers.learn.system.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import pers.learn.system.entity.Article;
import pers.learn.system.entity.ArticleComment;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface ArticleMapper extends BaseMapper<Article> {
    // List<Article> selectAll(@Param(Constants.WRAPPER) Wrapper<Article> wrapper);

    // NOTE 这个接口方法定义完成后，是需要去写xml里的sql，这里用注解完成
    // !
    // 这个接口方法与BaseMapper.selectPage()方法一样，外部调用方式也一样。但区别在于可以用自己定义的sql，在一些有条件约束的业务中有用
    @Select("select * from article ${ew.customSqlSegment} and release_time IS NOT NULL and deleted = 0")
    IPage<Article> selectWithPage(Page<Article> page, @Param(Constants.WRAPPER) Wrapper<Article> wrapper);

    @Select("select * from article_comment where article_id=#{id}")
    IPage<ArticleComment> allComments(Long articleId);

    @Select("delete from article where id =#{id}")
    boolean forceDeleteById(@Param("id") Long id);

    Article getArticle();
}
