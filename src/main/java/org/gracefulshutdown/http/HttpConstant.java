package org.gracefulshutdown.http;

public class HttpConstant {
    public interface Code {
        Integer RESPONSE_SUCCESS = 200;

        Integer BAD_REQUEST = 400;

        Integer INTERNAL_SERVER_ERROR = 500;
    }

    public interface Header {
        /**
         * 授权
         */
        String AUTH = "Authorization";

        /**
         * 内容类型
         */
        String CONTENT_TYPE = "Content-Type";

        /**
         * json类型
         */
        String APPLICATION_JSON = "application/json";
    }

    public interface Method {
        String POST = "POST";
    }
}
