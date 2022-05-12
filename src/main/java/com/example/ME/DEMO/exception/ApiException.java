package com.example.ME.DEMO.exception;

import org.springframework.http.HttpStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ApiException extends RuntimeException {
    private String code;
    private String message;

    public ApiException(String message) {
        this.code = HttpStatus.UNPROCESSABLE_ENTITY.toString();
        this.message = message;
    }

    public ApiException(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
