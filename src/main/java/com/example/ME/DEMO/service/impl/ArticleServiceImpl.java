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
import com.example.ME.DEMO.mapper.ArticleMapper;
import com.example.ME.DEMO.service.ArticleService;
import com.example.ME.constant.Query;

import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.NonNull;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    /**
     * 返回所有文章的概要信息（包含：ID、标题、概要信息、作者）
     * 
     * @return List<ArticleBriefVO>
     */
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
            ArticleBriefVO article = new ArticleBriefVO();
            article.setId(item.getId());
            article.setTitle(item.getTitle());
            article.setBrief(item.getBrief());
            article.setAuthor(item.getAuthor());
            articleBrief.add(article);
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
    public IPage<Article> listWithPage(@NonNull Integer currentPage, @NonNull Integer pageSize) {
        LambdaQueryWrapper<Article> articleWrapper = generateCommonBusinessLogicWrapper();
        Page<Article> page = new Page<Article>(currentPage, pageSize);
        IPage<Article> selectPage = articleMapper.selectPage(page, articleWrapper);
        // IPage<Article> selectPage = articleMapper.selectWithPage(page,
        // articleWrapper); // NOTE 这里是使用自定义的分页接口
        return selectPage;
    }

    /**
     * 获取一个新闻并增加它的查看数
     * 而且使用了乐观锁 0.0 详见Article实体的@Version实体属性
     * 
     * @return Article
     */
    @Override
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
     * @throws Exception
     */
    public boolean updateArticle(Long id, Article requestBody) throws Exception {
        Article article = getById(id);
        if (article == null) {
            throw new NotFoundException("找不到文章啦");
        }
        System.out.println("文章内容：" + article);
        // 标题唯一性检测 TODO 希望能够复用，封装为一个内部方法在java里不算真正的可复用，我感觉得写个自定义注解给到Entity使用
        checkTitleRepeat(requestBody.getTitle(), id);
        article.setTitle(requestBody.getTitle());
        article.setContent(requestBody.getContent());
        article.setReleaseTime(requestBody.getReleaseTime());
        article.setBrief(requestBody.getBrief());
        article.setAuthor(requestBody.getAuthor());
        return updateById(article);
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
     * @return boolean
     * @throws EntityExistsException
     */
    public boolean insertArticle(Article requestBody) throws EntityExistsException {
        checkTitleRepeat(requestBody.getTitle());
        Article article = new Article();
        article.setTitle(requestBody.getTitle());
        article.setContent(requestBody.getContent());
        article.setReleaseTime(requestBody.getReleaseTime());
        article.setBrief(requestBody.getBrief());
        article.setAuthor(requestBody.getAuthor());
        return save(article);
    }
}
