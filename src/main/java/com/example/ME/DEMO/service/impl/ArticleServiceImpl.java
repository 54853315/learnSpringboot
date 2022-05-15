/*
 * @Author: konakona konakona@crazyphper.com
 * @Date: 2022-05-06 15:35:53
 * @LastEditors: konakona konakona@crazyphper.com
 * @LastEditTime: 2022-05-15 17:07:40
 * @Description: 
 * 
 * Copyright (c) 2022 by konakona konakona@crazyphper.com, All Rights Reserved. 
 */
package com.example.ME.DEMO.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityExistsException;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ME.DEMO.controller.vo.ArticleBriefVO;
import com.example.ME.DEMO.entity.Article;
import com.example.ME.DEMO.exception.ApiException;
import com.example.ME.DEMO.mapper.ArticleMapper;
import com.example.ME.DEMO.service.ArticleService;
import com.example.ME.constant.Query;
import com.example.ME.request.ArticleBodyDto;
import com.example.ME.util.DateAdopter;

import org.apache.ibatis.javassist.NotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import lombok.NonNull;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * 返回所有文章的概要信息（包含：ID、标题、概要信息、作者）
     * 
     * @return List<ArticleBriefVO>
     */
    @Cacheable(value = "article:allBrief")
    public List<ArticleBriefVO> allBrief() {
        LambdaQueryWrapper<Article> articleWrapper = generateCommonBusinessLogicWrapper();
        articleWrapper.select(Article::getId, Article::getTitle, Article::getBrief, Article::getAuthor);
        // select中排除content和release_time2个字段，其他都返回
        // articleWrapper.select(
        // Article.class,
        // info -> !info.getColumn().equals("content")
        // &&
        // !info.getColumn().equals("release_time"));
        List<ArticleBriefVO> articleBrief = new ArrayList<ArticleBriefVO>();
        list(articleWrapper).forEach(item -> {
            // 类型转换老写法
            // ArticleBriefVO article = new ArticleBriefVO();
            // article.setId(item.getId());
            // article.setTitle(item.getTitle());
            // article.setBrief(item.getBrief());
            // article.setAuthor(item.getAuthor());
            // articleBrief.add(article);

            // 使用org.modelmapper做类型转换
            articleBrief.add(modelMapper.map(item, ArticleBriefVO.class));
        });
        return articleBrief;
    }

    /**
     * 当没有传递分页参数时，使用重载方法的方式传入默认值，每页显示20条
     * 如果需要用MP默认的10条，则注释掉整个方法即可
     * 
     * @return
     */
    public IPage<Article> listWithPage() {
        return listWithPage(Query.getDefaultPage2Int(), Query.getDefaultPageSize2Int());
    }

    /**
     * 获取查询列表数据使用的查询构造器
     * 
     * @return LambdaQueryWrapper<Article>
     */
    private LambdaQueryWrapper<Article> generateCommonBusinessLogicWrapper() {
        LambdaQueryWrapper<Article> articleWrapper = new LambdaQueryWrapper<Article>();
        articleWrapper.isNotNull(Article::getReleaseTime);
        return articleWrapper;
    }

    /**
     * 查询数据并返回列表与分页
     * 
     * @return IPage<Article>
     */
    @Cacheable(value = "article:list")
    public IPage<Article> listWithPage(@NonNull Integer currentPage, @NonNull Integer pageSize) {
        LambdaQueryWrapper<Article> articleWrapper = generateCommonBusinessLogicWrapper();
        Page<Article> page = new Page<Article>(currentPage, pageSize);
        IPage<Article> selectPage = articleMapper.selectPage(page, articleWrapper);
        // NOTE 这里是使用自定义的分页接口
        // IPage<Article> selectPage =
        // articleMapper.selectWithPage(page,articleWrapper);
        return selectPage;
    }

    /**
     * 获取一个新闻并增加它的查看数
     * 
     * @return Article
     */
    @Override
    @Cacheable(value = "article", key = "#id", condition = "#id != null")
    public Article increaseViewCountAndGet(Long id) throws NotFoundException {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new NotFoundException("没有这篇文章");
        }
        article.setViewCount(article.getViewCount() + 1);
        articleMapper.updateById(article);
        return article;
    }

    /**
     * 更新文章
     * 
     * @param id
     * @param requestBody
     * @return boolean
     * @throws Article
     */
    @CachePut(value = "article", key = "#id", condition = "#result.id !=null")
    public Article updateArticle(Long id, ArticleBodyDto requestBody) throws Exception {
        Article article = getById(id);
        if (article == null) {
            throw new NotFoundException("找不到文章啦");
        }
        // 标题唯一性检测 TODO 希望能够复用，封装为一个内部方法在java里不算真正的可复用，我感觉得写个自定义注解给到Entity使用
        checkTitleRepeat(requestBody.getTitle(), id);
        article.setTitle(requestBody.getTitle());
        article.setContent(requestBody.getContent());
        article.setReleaseTime(DateAdopter.str2LoclaDateTime(requestBody.getReleaseTime()));
        article.setBrief(requestBody.getBrief());
        article.setAuthor(requestBody.getAuthor());
        if (updateById(article) == false) {
            throw new ApiException("更新文章失败");
        }
        return article;
    }

    /**
     * 新增时，检查标题唯一性
     * 
     * @param newTitle
     * @throws EntityExistsException
     */
    private void checkTitleRepeat(String newTitle) throws EntityExistsException {
        checkTitleRepeat(newTitle, 0L);
    }

    /**
     * 更新时，检查标题是否唯一
     * 
     * @param newTitle
     * @param id
     * @throws EntityExistsException
     */
    private void checkTitleRepeat(String newTitle, Long id) throws EntityExistsException {
        LambdaQueryWrapper<Article> query = new LambdaQueryWrapper<Article>();
        query.eq(Article::getTitle, newTitle);
        if (id > 0L) {
            query.ne(Article::getId, id);
        }
        Article sameTitleArticle = getOne(query);
        if (sameTitleArticle != null) {
            throw new EntityExistsException("标题已存在");
        }
    }

    /**
     * 新增文章
     * 
     * @param requestBody
     * @return Article
     * @throws EntityExistsException
     */
    public Article insertArticle(ArticleBodyDto requestBody) throws EntityExistsException {
        checkTitleRepeat(requestBody.getTitle());
        Article article = new Article();
        article.setTitle(requestBody.getTitle());
        article.setContent(requestBody.getContent());
        article.setReleaseTime(DateAdopter.str2LoclaDateTime(requestBody.getReleaseTime()));
        article.setBrief(requestBody.getBrief());
        article.setAuthor(requestBody.getAuthor());
        if (save(article) == false) {
            throw new ApiException("新增文章失败");
        }
        return article;
    }

}
