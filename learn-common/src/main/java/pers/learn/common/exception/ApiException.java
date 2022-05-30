package pers.learn.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pers.learn.common.response.CommonResponse;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ApiException extends RuntimeException {
    private Integer code;
    private String message;

    public ApiException(String message) {
        this.code = CommonResponse.Type.FAIL.ordinal();
        this.message = message;
    }

    public ApiException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
