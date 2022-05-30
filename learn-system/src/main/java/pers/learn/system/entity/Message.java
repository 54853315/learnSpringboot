/*
 * @Author: konakona konakona@crazyphper.com
 * @Date: 2022-05-05 17:52:10
 * @LastEditors: konakona konakona@crazyphper.com
 * @LastEditTime: 2022-05-16 22:27:52
 * @Description: 
 * 
 * Copyright (c) 2022 by konakona konakona@crazyphper.com, All Rights Reserved. 
 */
package pers.learn.system.entity;

import org.springframework.lang.Nullable;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
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
