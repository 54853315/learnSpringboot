/*
 * @Author: konakona konakona@crazyphper.com
 * @Date: 2022-05-16 21:29:21
 * @LastEditors: konakona konakona@crazyphper.com
 * @LastEditTime: 2022-05-16 21:35:33
 * @Description: 
 * 
 * Copyright (c) 2022 by konakona konakona@crazyphper.com, All Rights Reserved. 
 */
package pers.learn.blog.util;

import java.io.Serializable;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisUtil {
    RedisTemplate<String, Serializable> redisTemplate;

    public Serializable get(String key) {
        if (key.isEmpty()) {
            return null;
        }
        return redisTemplate.opsForValue().get(key);
    }

    public void set(String key, String value) {
        if (key.isEmpty() || value.isEmpty()) {
            return;
        }
        redisTemplate.opsForValue().set(key, value);
    }

    public void set(String key, Object value) {
        if (key.isEmpty() || value == null) {
            return;
        }
        // redisTemplate.opsForValue().set(key, value);
    }
}
