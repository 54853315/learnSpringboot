package com.example.ME.DEMO.service.impl;

import javax.persistence.EntityExistsException;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ME.DEMO.entity.Article;
import com.example.ME.DEMO.mapper.ArticleMapper;
import com.example.ME.DEMO.service.ArticleService;

import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    /**
     * 当没有传递分页参数时，使用重载方法的方式传入默认值，每页显示20条
     * 如果需要用MP默认的10条，则注释掉整个方法即可
     * 
     * @return
     */
    public IPage<Article> listWithPage() {
        return listWithPage(1, 15);
    }

    /**
     * 获取查询列表数据使用的查询构造器
     * 
     * @return LambdaQueryWrapper<Article>
     */
    private LambdaQueryWrapper<Article> generateListWrapper() {
        LambdaQueryWrapper<Article> articleWrapper = new LambdaQueryWrapper<Article>();
        articleWrapper.isNotNull(Article::getReleaseTime);
        return articleWrapper;
    }

    /**
     * 查询数据并返回列表与分页
     * 
     * @return IPage<Article>
     */
    public IPage<Article> listWithPage(Integer currentPage, Integer pageSize) {
        LambdaQueryWrapper<Article> articleWrapper = generateListWrapper();
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
