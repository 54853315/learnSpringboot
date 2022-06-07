/*
 * @Author: konakona konakona@crazyphper.com
 * @Date: 2022-05-11 15:06:24
 * @LastEditors: konakona konakona@crazyphper.com
 * @LastEditTime: 2022-05-25 16:58:34
 * @Description:
 *
 * Copyright (c) 2022 by konakona konakona@crazyphper.com, All Rights Reserved.
 */
package pers.learn.common.response;

import pers.learn.common.util.DateAdopter;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
// 通用返回
public class CommonResponse<T> {
    private Type code;
    private String message;
    private T data;
    private String timestamp;

    /**
     * 返回的Code的状态类型，除了成功为0，其他与http code的想法实现一致
     */
    public enum Type {
        /**
         * 成功
         */
        SUCCESS(0),
        /**
         * 执行失败
         */
        FAIL(422),
        /**
         * 禁止访问
         */
        UNAUTHORIZED(401),
        /**
         * 找不到
         */
        NOT_FOUND(404),
        /**
         * 错误
         */
        ERROR(500);
        private final int value;

        Type(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }
    }

    public CommonResponse(String message) {
        this.setMessage(message);
        this.setTimestamp(DateAdopter.long2TimeStr());
    }

    public CommonResponse(T data) {
        this.setData(data);
        this.setTimestamp(DateAdopter.long2TimeStr());
    }

    public CommonResponse(Type code, String message) {
        this.setCode(code);
        this.setMessage(message);
        this.setTimestamp(DateAdopter.long2TimeStr());
    }

    public CommonResponse(Type code, String message, T data) {
        this.setCode(code);
        this.setMessage(message);
        this.setData(data);
        this.setTimestamp(DateAdopter.long2TimeStr());
    }

    /**
     * 成功返回信息
     *
     * @param <T>
     * @param code
     * @param message
     * @return
     */
    public static <T> CommonResponse<T> success(Type code, String message) {
        CommonResponse<T> r = new CommonResponse<T>(code, message);
        r.setTimestamp(DateAdopter.long2TimeStr());
        return r;
    }

    public static <T> CommonResponse<T> fail(Type code, String message) {
        CommonResponse<T> r = new CommonResponse<T>(code, message);
        r.setTimestamp(DateAdopter.long2TimeStr());
        return r;
    }

    public static <T> CommonResponse<T> success() {
        return success(Type.SUCCESS, "success");
    }

    public static <T> CommonResponse<T> success(String message) {
        return success(Type.SUCCESS, message);
    }

    public static <T> CommonResponse<T> fail(String message) {
        return fail(Type.ERROR, message);
    }

    public static <T> CommonResponse<T> fail() {
        return fail(Type.ERROR, "fail");
    }

    /**
     * 返回成功，携带一大堆数据
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> CommonResponse<T> returnResult(T data) {
        CommonResponse<T> result = new CommonResponse<>(Type.SUCCESS, "success");
        result.setData(data);
        return result;
    }

    /**
     * 返回成功，携带一条kv数据
     *
     * @param key
     * @param value
     * @param <T>
     * @return
     */
    @SuppressWarnings (value="unchecked")
    public static <T> CommonResponse<T> returnResult(String key, String value) {
        CommonResponse<T> result = new CommonResponse<>(Type.SUCCESS, "success");
        Map<String, String> map = new HashMap<>();
        map.put(key, value);
        result.setData((T) map);
        return result;
    }
}
