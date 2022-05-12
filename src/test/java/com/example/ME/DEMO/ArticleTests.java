package com.example.ME.DEMO;

import java.time.LocalDateTime;
import com.example.ME.DEMO.entity.Article;
import com.example.ME.DEMO.mapper.ArticleMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ArticleTests {
    @Autowired
    private ArticleMapper articleMapper;

    @Test
    public void insertOne() {
        Article article = new Article();
        article.setAuthor("作者");
        article.setTitle("我是一个随随便便的标题");
        article.setBrief("简介");
        article.setContent("随便弄点内容...");
        article.setReleaseTime(LocalDateTime.now());
        int rows = articleMapper.insert(article);
        System.out.println("影像记录数：" + rows);
    }

    @Test
    public void getOne() {
        Article article = articleMapper.selectOne(null);
        System.out.println("刚刚插入的数据：" + article);
    }

    @Test
    public void changeOne() {
        Article article = articleMapper.selectOne(null);
        if (article != null) {
            article.setTitle("新的随便的标题");
            articleMapper.update(article, null);
        }
        System.out.println("没有足够的数据");
    }

    @Test
    public void deleteOne() {
        int rows = articleMapper.delete(null);
        System.out.printf("删除了%d条数据", rows);
    }
}