/*
 * @Author: konakona konakona@crazyphper.com
 * @Date: 2022-05-06 15:31:16
 * @LastEditors: konakona konakona@crazyphper.com
 * @LastEditTime: 2022-05-17 21:11:12
 * @Description: 
 * QQ:54583315 
 *  https://www.crazyphper.com
 * Copyright (c) 2022 by konakona konakona@crazyphper.com, All Rights Reserved. 
 */
package pers.learn.blog.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.lang.Nullable;

@Data
@NoArgsConstructor
@Accessors(chain = true)
@Entity
@Table
public class Article implements Serializable {

    private static final long serialVersionUID = 1L;
    // 标注用于声明一个实体类的属性映射为数据库的主键列
    @Id
    // @SequenceGenerator(name = "article", sequenceName = "article", allocationSize
    // = 1)
    // 使用mysql的自增字段
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "article")
    private Long id;

    @Column(unique = true, length = 100, nullable = false)
    private String title;

    @Column(length = 50, nullable = false)
    private String author;

    @Column(length = 150, nullable = false)
    private String brief;

    @Column(columnDefinition = "text")
    private String content;

    @Column(columnDefinition = "int default 0", nullable = false)
    private Integer viewCount = 0;

    @NonNull
    @Column(columnDefinition = "datetime")
    @JsonFormat(pattern = "yyyy-MM-dd HH-mm-ss")
    private LocalDateTime releaseTime;

    @NonNull
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH-mm-ss")
    @Column(columnDefinition = "datetime")
    private LocalDateTime createTime;

    @NonNull
    @TableField(fill = FieldFill.UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH-mm-ss")
    @Column(columnDefinition = "datetime")
    private LocalDateTime updateTime;

    // 标识为逻辑删除字段
    @TableLogic
    // 返回json不显示
    @JsonIgnore
    // 查询时不显示
    @TableField(select = false)
    @Column(columnDefinition = "tinyint default 0", nullable = false)
    private Integer deleted;

    // 评论，可能为null
    @Nullable
    @TableField(exist = false)
    @Transient
    private List<ArticleComment> comments;
}
