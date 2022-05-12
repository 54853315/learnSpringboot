package com.example.ME.DEMO.controller;

import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ME.DEMO.controller.vo.ArticleBriefVO;
import com.example.ME.DEMO.entity.Article;
import com.example.ME.DEMO.exception.ApiException;
import com.example.ME.DEMO.response.CommonResponse;
import com.example.ME.DEMO.service.impl.ArticleServiceImpl;
import com.example.ME.constant.Query;

import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
// 整个Article功能都是基于springboot+mybatis plus + hibernate实现

@RestController
public class ArticleController {
    @Autowired
    private ArticleServiceImpl articleServiceImpl;

    /**
     * 抛出一个api异常，展示显示效果
     * 
     * @example:
     *           {
     *           "code": "422 UNPROCESSABLE_ENTITY",
     *           "message": "haha",
     *           "data": null,
     *           "timestamp": "2022-05-12 12:38:32"
     *           }
     */
    @GetMapping("/article/exception")
    public void throwExampleException() {
        throw new ApiException("haha");
    }

    /**
     * 返回所有文章的概要信息
     * 
     * @example :
     *          {
     *          "code": "200 OK",
     *          "message": "success",
     *          "data": [{
     *          "id": 2,
     *          "title": "abc",
     *          "author": "1",
     *          "brief": "22"
     *          }],
     *          "timestamp": "2022-05-12 15:00:41"
     *          }
     * @return List<Article>
     */
    @GetMapping("/articles/allBrief")
    public CommonResponse<List<ArticleBriefVO>> allBrief() {
        return CommonResponse.returnResult(articleServiceImpl.allBrief());
    }

    /**
     * 查看文章列表
     * 
     * @return CommonResponse<IPage<Article>>
     */
    @GetMapping("/articles")
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
     */
    @PostMapping("/article")
    public CommonResponse<String> store(@RequestBody Article requestBody) throws Exception {
        System.out.println(requestBody);
        if (articleServiceImpl.insertArticle(requestBody)) {
            return CommonResponse.success();
        }
        System.out.println("到不了这一步吧？");
        return CommonResponse.fail("保存失败");
    }

    /**
     * 更新文章
     * 
     * @param requestBody
     * @throws Exception
     */
    @PutMapping("/article/{id}")
    public CommonResponse<String> update(@PathVariable("id") Long id, @RequestBody Article requestBody)
            throws Exception {
        System.out.println(requestBody);
        if (articleServiceImpl.updateArticle(id, requestBody)) {

        }
        return CommonResponse.success();
    }

    /**
     * 通过Id删除一个文章
     * 
     * @param id
     * @return boolean
     */
    @DeleteMapping("/article/{id}")
    public CommonResponse<String> delete(@PathVariable("id") Long id) {
        articleServiceImpl.removeById(id);
        return CommonResponse.success();
    }
}
