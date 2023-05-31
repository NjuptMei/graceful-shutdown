package org.gracefulshutdown.http;

public enum ResultEnum {
    /**
     * 成功响应
     */
    SUCCESS(HttpConstant.Code.RESPONSE_SUCCESS, "success"),

    /**
     * 执行失败，异常入参
     */
    BAD_REQUEST(HttpConstant.Code.BAD_REQUEST, "bad request"),

    /**
     * 执行失败，异常入参
     */
    INTERNAL_ERROR(HttpConstant.Code.INTERNAL_SERVER_ERROR, "internal error");

    private final Integer httpCode;

    private final String msg;

    private ResultEnum(Integer httpCode, String msg) {
        this.httpCode = httpCode;
        this.msg = msg;
    }
}
