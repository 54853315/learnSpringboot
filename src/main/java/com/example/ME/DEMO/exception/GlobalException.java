/*
 * @Author: konakona konakona@crazyphper.com
 * @Date: 2022-05-05 16:01:13
 * @LastEditors: konakona konakona@crazyphper.com
 * @LastEditTime: 2022-05-11 17:24:02
 * @FilePath: /learning-java-spring-boot/src/main/java/com/example/ME/DEMO/exception/GlobalException.java
 * @Description: 
 * 
 * Copyright (c) 2022 by konakona konakona@crazyphper.com, All Rights Reserved. 
 */
package com.example.ME.DEMO.exception;

import com.example.ME.DEMO.response.CommonResponse;

import org.apache.ibatis.javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalException {

    private static final Logger logger = LoggerFactory.getLogger(GlobalException.class);

    /**
     * @description: 将Runtime异常根据情况进行输出处理
     * @param {*} e
     * @return {*} String
     * @throws: 
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String runtimeException(Exception e) {
        String stackTrack = e.getStackTrace().length > 0 ? e.getStackTrace()[0].toString() : "";
        String errorMsg = "遇到一个未捕获的野生RuntimeException：" + e.toString() + "\n strack:" + stackTrack;
        logger.error(errorMsg);
        return errorMsg;
    }

    /**
     * 将Api异常捕获并格式化输出
     * 
     * @param e
     * @return
     */
    @ExceptionHandler(ApiException.class)
    public CommonResponse<Object> apiException(ApiException e) {
        return CommonResponse.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public String notFoundException(Exception e) {
        String errorMsg = "403:" + e.toString();
        logger.error(errorMsg);
        return errorMsg;
    }
}
