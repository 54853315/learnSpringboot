/*
 * @Author: konakona konakona@crazyphper.com
 * @Date: 2022-05-16 14:49:37
 * @LastEditors: konakona konakona@crazyphper.com
 * @LastEditTime: 2022-05-17 21:11:42
 * @Description: 
 * 
 * Copyright (c) 2022 by konakona konakona@crazyphper.com, All Rights Reserved. 
 */
package pers.learn.blog;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.Serializable;
import java.time.LocalDateTime;

import pers.learn.blog.entity.Article;
import pers.learn.blog.entity.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
public class RedisTests {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private RedisTemplate<Object, Object> objectRedisTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public final static String NAME = "konakona";
    public final static String VALUE = "It's xiong";

    @Test
    public void testStringSet() {
        stringRedisTemplate.opsForValue().set(NAME, VALUE);
        String name = stringRedisTemplate.opsForValue().get(NAME);
        System.out.println("testStringSet:" + name);
        assertEquals(name, VALUE);
    }

    @Test
    public void testObjectSet() {
        Message message = new Message();
        message.setContent("content");
        message.setName("name");
        message.setUrl("url");
        objectRedisTemplate.opsForValue().set("object", message);
        Object result = objectRedisTemplate.opsForValue().get("object");
        System.out.println("testObjectSet:" + result);
    }

    @Test
    public void testJsonSet() throws JsonProcessingException {
        Message message = new Message();
        message.setContent("content");
        message.setName("name");
        message.setUrl("url");
        String json = new ObjectMapper().writeValueAsString(message);
        redisTemplate.opsForValue().set("object_json", json);
        String result = redisTemplate.opsForValue().get("object_json");
        Message messageResult = new ObjectMapper().readValue(result, Message.class);
        System.out.println("testJsonSet json:" + json);
        System.out.println("testJsonSet result:" + messageResult);
    }

    @Test
    public void testDel() {
        boolean result1 = redisTemplate.delete("object");
        boolean result2 = redisTemplate.delete("object_json");
        boolean result3 = stringRedisTemplate.delete(NAME);

        assertEquals(result1, true, "success");
        assertEquals(result2, true, "success");
        assertEquals(result3, true, "success");
    }
}
