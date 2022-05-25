/*
 * @Author: konakona konakona@crazyphper.com
 * @Date: 2022-05-05 16:01:13
 * @LastEditors: konakona konakona@crazyphper.com
 * @LastEditTime: 2022-05-25 15:08:01
 * @Description: 全局异常处理器
 * 
 * Copyright (c) 2022 by konakona konakona@crazyphper.com, All Rights Reserved. 
 */
package pers.learn.framework.exception;

import pers.learn.response.CommonResponse;

import org.apache.ibatis.javassist.NotFoundException;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthorizedException;
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
        logger.error(errorMsg, e);
        return CommonResponse.fail(e.getMessage());
    }

    // 当用户访问未经授权的资源时，如果没有主动捕获，统一返回这条
    @ExceptionHandler(value = { AuthorizationException.class, UnauthorizedException.class })
    public CommonResponse<String> authorizationException(AuthorizationException e) {
        return CommonResponse.fail("无授权，不可访问");
    }

    // 当用户访问需要RequestBody的资源却没有携带时，统一返回这条
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public CommonResponse<Object> httpMessageNotReadableException(HttpMessageNotReadableException e) {
        return CommonResponse.fail("request body不能为空");
    }

    // 将Api异常捕获并格式化输出
    @ExceptionHandler(ApiException.class)
    public CommonResponse<Object> apiException(ApiException e) {
        return CommonResponse.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public CommonResponse<Object> notFoundException(Exception e) {
        return CommonResponse.fail(HttpStatus.NOT_FOUND.toString(), e.getMessage());
    }
}
