package pers.learn.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import pers.learn.entity.Article;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.stereotype.Service;
@Service
public interface ArticleService extends IService<Article> {
    // IPage<Article> listWithPage();
    Article increaseViewCountAndGet(Long id) throws NotFoundException;
    // @Select("select * from articles order by id desc")
    // List<Article> list();
}
