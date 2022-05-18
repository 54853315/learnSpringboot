/*
 * @Author: konakona konakona@crazyphper.com
 * @Date: 2022-05-16 22:38:08
 * @LastEditors: konakona konakona@crazyphper.com
 * @LastEditTime: 2022-05-18 10:27:15
 * @Description: 
 * 
 * Copyright (c) 2022 by konakona konakona@crazyphper.com, All Rights Reserved. 
 */
package pers.learn.blog.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.baomidou.mybatisplus.annotation.TableField;

import org.springframework.lang.Nullable;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@Entity
@Table
public class ArticleComment implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private Long id;

    private String name;

    @NonNull
    private Long articleId = 0L;

    @NonNull
    private String content;
}
