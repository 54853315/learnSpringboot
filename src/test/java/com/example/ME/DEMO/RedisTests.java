/*
 * @Author: konakona konakona@crazyphper.com
 * @Date: 2022-05-16 14:49:37
 * @LastEditors: konakona konakona@crazyphper.com
 * @LastEditTime: 2022-05-16 15:46:33
 * @Description: 
 * 
 * Copyright (c) 2022 by konakona konakona@crazyphper.com, All Rights Reserved. 
 */
package com.example.ME.DEMO;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
public class RedisTests {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public final static String NAME = "konakona";
    public final static String VALUE = "It's xiong";

    @Test
    public void testSet() {
        redisTemplate.opsForValue().set(NAME, VALUE);
        Object name = redisTemplate.boundValueOps(NAME).get();
        assertEquals(name, VALUE, "success.");
    }

    @Test
    public void testDel() {
        boolean result = redisTemplate.delete(NAME);
        assertEquals(result, true, "success");
    }
}
