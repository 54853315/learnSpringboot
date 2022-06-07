package pers.learn.web.controller;

import java.util.ArrayList;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.modelmapper.ModelMapper;
import pers.learn.common.constant.Query;
import pers.learn.common.exception.ApiException;
import pers.learn.system.entity.Article;
import pers.learn.system.entity.ArticleComment;
import pers.learn.web.controller.vo.ArticleBriefVO;
import pers.learn.system.dto.ArticleBodyDto;
import pers.learn.common.response.CommonResponse;
import pers.learn.system.service.impl.ArticleCommentServiceImpl;
import pers.learn.system.service.impl.ArticleServiceImpl;

import org.apache.ibatis.javassist.NotFoundException;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
// 整个Article功能都是基于springboot+mybatis plus + hibernate实现

import lombok.NonNull;

@RestController
public class ArticleController {
    @Autowired
    private ArticleServiceImpl articleServiceImpl;

    @Autowired
    // private ArticleCommentService articleCommentService;
    private ArticleCommentServiceImpl articleCommentServiceImpl;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * 抛出一个api异常，展示显示效果
     *
     * @example: {
     * "code": "422 UNPROCESSABLE_ENTITY",
     * "message": "haha",
     * "data": null,
     * "timestamp": "2022-05-12 12:38:32"
     * }
     */
    @GetMapping("/article/exception")
    public void throwExampleException() {
        throw new ApiException("haha");
    }

    /**
     * 返回所有文章的概要信息
     *
     * @return List<Article>
     * @example :
     * {
     * "code": "200 OK",
     * "message": "success",
     * "data": [{
     * "id": 2,
     * "title": "abc",
     * "author": "1",
     * "brief": "22"
     * }],
     * "timestamp": "2022-05-12 15:00:41"
     * }
     */
    @GetMapping("/admin/article/allBrief")
    @RequiresPermissions("article:all")
    public CommonResponse<List<ArticleBriefVO>> allBrief() {
        List<Article> allBriefResult = articleServiceImpl.allBrief();//@TODO 有个报错需要修复
        List<ArticleBriefVO> articleBrief = new ArrayList<>();
        allBriefResult.forEach(item -> {
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
        return CommonResponse.returnResult(articleBrief);
    }

    /**
     * 查看文章列表
     *
     * @return CommonResponse<IPage < Article>>
     */
    @GetMapping("/articles")
//    @RequiresPermissions("article:list")
    public CommonResponse<IPage<Article>> list(
            @RequestParam(value = "page", required = false, defaultValue = Query.DEFAULT_PAGE) Integer page,
            @RequestParam(value = "per_page", required = false, defaultValue = Query.DEFAULT_PAGE_SIZE) Integer perPage) {
        // NOTE 使用MP封装的翻页查询返回返回数据
        // IPage<Article> result = articleServiceImpl.page(new Page<Article>(page,
        // perPage),
        // new LambdaQueryWrapper<Article>().isNotNull(Article::getReleaseTime));
        // NOTE 使用自己封装的有状态的service返回分页数据，跟上面效果是一样的
        IPage<Article> result = articleServiceImpl.listWithPage(page, perPage);
        return CommonResponse.returnResult(result);
    }

    /**
     * 查看一个文章
     *
     * @param id
     * @return CommonResponse<Article>
     * @throws NotFoundException
     */
    @GetMapping("/article/{id}")
    public CommonResponse<Article> detail(@PathVariable("id") Long id) throws NotFoundException {
        // public Article detail(@RequestParam(value = "article_id") Integer id) throws
        return CommonResponse.returnResult(articleServiceImpl.increaseViewCountAndGet(id));
    }

    /**
     * 新增一个文章
     *
     * @param requestBody
     * @throws Exception
     * @example :
     * curl --location --request POST 'http://localhost:8080/article' \
     * --header 'Content-Type: application/json' \
     * --data-raw '{
     * "title": "各工构毛派",
     * "author": "nulla",
     * "brief": "labore culpa deserunt anim dolor",
     * "release_time": "2002-06-17 23:00:13"
     * }'
     */
    @PostMapping("/admin/article")
    @RequiresRoles("admin")
    public CommonResponse<Article> store(@RequestBody ArticleBodyDto requestBody) throws Exception {
        return CommonResponse.returnResult(articleServiceImpl.insertArticle(requestBody));
    }

    /**
     * 更新文章
     *
     * @param requestBody
     * @throws Exception
     */
    @PutMapping("/admin/article/{id}")
    @RequiresRoles("admin")
    public CommonResponse<Article> update(@PathVariable("id") Long id, @RequestBody ArticleBodyDto requestBody)
            throws Exception {
        return CommonResponse.returnResult(articleServiceImpl.updateArticle(id, requestBody));
    }

    /**
     * 通过Id删除(逻辑软删除)一个文章
     * 目前由于框架设计者主张逻辑删除不应被物理删除，因此无法实现物理删除
     * 若业务需要逻辑删除可以被物理删除，则需要自己实现（见ArticleMapper->forceDeleteById）
     * issue：（请愿太多了）
     * via:
     * https://github.com/baomidou/mybatis-plus/blob/a3e121c27cd26cb7c546dfb88190f3b1f574dc38/mybatis-plus-core/src/main/java/com/baomidou/mybatisplus/core/injector/methods/DeleteById.java
     *
     * @param id
     * @return boolean
     */
    @DeleteMapping("/admin/article/{id}")
    @RequiresRoles("admin")
    @RequiresPermissions(value = {"article:del", "article:delete"}, logical = Logical.OR)
    @CacheEvict(value = "article", key = "#id", beforeInvocation = true)
    public CommonResponse<String> delete(@PathVariable("id") Long id) {
        if (articleServiceImpl.removeById(id)) {
            return CommonResponse.success();
        } else {
            return CommonResponse.fail(CommonResponse.Type.FAIL,
                    "删除失败");
        }
    }

    @GetMapping(value = "/article/{article_id}/comment")
    /**
     * 文章评论列表
     *
     * @example
     *          "data": {
     *          "records": [
     *          {
     *          "id": 1,
     *          "name": "a",
     *          "articleId": 11,
     *          "content": "b"
     *          }
     *          ],
     *          "total": 0,
     *          "size": 15,
     *          }
     * @param articleId
     * @param page
     * @param perPage
     * @return CommonResponse<IPage < ArticleComment>>
     */
    @RequiresAuthentication
    public CommonResponse<IPage<ArticleComment>> comments(
            @PathVariable("article_id") @NonNull Long articleId,
            @RequestParam(value = "page", required = false, defaultValue = Query.DEFAULT_PAGE) Integer page,
            @RequestParam(value = "per_page", required = false, defaultValue = Query.DEFAULT_PAGE_SIZE) Integer perPage) {
        // NOTE 使用MP封装的翻页查询返回返回数据
        IPage<ArticleComment> result = articleCommentServiceImpl.page(
                new Page<ArticleComment>(
                        page,
                        perPage),
                new LambdaQueryWrapper<ArticleComment>().eq(
                        ArticleComment::getArticleId, articleId));
        return CommonResponse.returnResult(result);
    }
}
