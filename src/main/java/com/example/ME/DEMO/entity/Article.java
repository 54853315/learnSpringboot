package com.example.ME.DEMO.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;

@Data
@NoArgsConstructor
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

    @Column()
    @Version    //乐观锁注解
    private Integer viewCount;

    @NonNull
    @Column(columnDefinition = "datetime")
    private LocalDateTime releaseTime;

    @NonNull
    @TableField(fill = FieldFill.INSERT)
    @Column(columnDefinition = "datetime")
    private LocalDateTime createTime;

    @NonNull
    @TableField(fill = FieldFill.UPDATE)
    @Column(columnDefinition = "datetime")
    private LocalDateTime updateTime;

    // 标识为逻辑删除字段
    @TableLogic
    // 查询时不显示
    @TableField(select = false)
    private Integer deleted;
}
