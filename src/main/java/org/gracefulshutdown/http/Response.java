package org.gracefulshutdown.http;

public class Response<T> {
    private Integer code = HttpConstant.Code.RESPONSE_SUCCESS;

    private String msg = "success";

    private T result;

    public Integer getCode() {
        return code;
    }
    public Response() {}

    public Response(T result) {
        this.result = result;
    }

    public Response(Integer code, String msg, T result) {
        this.code = code;
        this.msg = msg;
        this.result = result;
    }

    public Response<T> success() {
        return this.setMsg(msg);
    }

    public Response<T> success(String msg) {
        return this.setMsg(msg);
    }

    public Response<T> success(T result) {
        return this.setResult(result);
    }

    public Response<T> success(String msg, T result) {
        return this.setMsg(msg).setResult(result);
    }

    public Response<T> fail(String msg) {
        return this.setMsg(msg);
    }

    public Response<T> fail(Integer code, String msg) {
        return this.setCode(code).setMsg(msg);
    }

    public Response<T> fail(Integer code, String msg, T result) {
        return this.setCode(code).setMsg(msg).setResult(result);
    }


    public Response<T> setCode(Integer code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public Response<T> setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public T getResult() {
        return result;
    }

    public Response<T> setResult(T result) {
        this.result = result;
        return this;
    }
}
