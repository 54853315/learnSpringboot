package com.example.ME.DEMO.response;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

import org.aspectj.bridge.IMessage;
import org.springframework.http.HttpStatus;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
// 通用返回
public class CommonResponse<T> {
    private String code;
    private String message;
    private T data;
    private String timestamp;

    public CommonResponse(String message) {
        this.setMessage(message);
        this.setTimestamp(long2TimeStr());
    }

    public CommonResponse(T data) {
        this.setData(data);
        this.setTimestamp(long2TimeStr());
    }

    public CommonResponse(String code, String message) {
        this.setCode(code);
        this.setMessage(message);
        this.setTimestamp(long2TimeStr());
    }

    public CommonResponse(String code, String message, T data) {
        this.setCode(code);
        this.setMessage(message);
        this.setData(data);
        this.setTimestamp(long2TimeStr());
    }

    /**
     * 成功返回信息
     * 
     * @param <T>
     * @param code
     * @param message
     * @return
     */
    public static <T> CommonResponse<T> success(String code, String message) {
        CommonResponse<T> r = new CommonResponse<T>(code, message);
        r.setTimestamp(long2TimeStr());
        return r;
    }

    public static <T> CommonResponse<T> fail(String code, String message) {
        CommonResponse<T> r = new CommonResponse<T>(code, message);
        r.setTimestamp(long2TimeStr());
        return r;
    }

    public static <T> CommonResponse<T> success() {
        return success(HttpStatus.OK.toString(), "success");
    }

    public static <T> CommonResponse<T> success(String message) {
        return success(HttpStatus.OK.toString(), message);
    }

    public static <T> CommonResponse<T> fail(String message) {
        return fail(HttpStatus.INTERNAL_SERVER_ERROR.toString(), message);
    }

    public static <T> CommonResponse<T> fail() {
        return fail(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "fail");
    }

    public static <T> CommonResponse<T> returnResult(T data) {
        CommonResponse<T> result = new CommonResponse<>(HttpStatus.OK.toString(), "success");
        result.setData(data);
        return result;
    }

    private static String long2TimeStr() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(new Date());
    }
}
