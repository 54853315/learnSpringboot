package com.example.ME.DEMO.entity;

import org.springframework.lang.Nullable;

import lombok.Data;
import lombok.NonNull;

@Data
public class Message {
    private int id;
    // 标注这个字段的值不能为空
    @Nullable
    private String name;
    private String content;
    // 标注这个字段的值可以为空
    @NonNull
    private String url;
}
