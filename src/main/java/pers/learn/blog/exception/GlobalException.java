/*
 * @Author: konakona konakona@crazyphper.com
 * @Date: 2022-05-05 16:01:13
 * @LastEditors: konakona konakona@crazyphper.com
 * @LastEditTime: 2022-05-23 13:49:22
 * @Description: 
 * 
 * Copyright (c) 2022 by konakona konakona@crazyphper.com, All Rights Reserved. 
 */
package pers.learn.blog.exception;

import pers.learn.blog.response.CommonResponse;

import org.apache.ibatis.javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
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
    public CommonResponse<String> runtimeException(Exception e) {
        String stackTrack = e.getStackTrace().length > 0 ? e.getStackTrace()[0].toString() : "";
        String errorMsg = "异常来自：" + e.getClass() + " \n 错误原因：" + e.toString() + "\nStrack:"
                + stackTrack;
        // 命令行会高亮一行ERROR行，注意看
        logger.error(errorMsg);
        return CommonResponse.fail(e.getClass() + "发生错误" + e.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public CommonResponse<Object> httpMessageNotReadableException(HttpMessageNotReadableException e) {
        return CommonResponse.fail("request body不能为空");
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
