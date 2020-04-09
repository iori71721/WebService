package com.iori.custom.webservice;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;

/**
 * define webservice function
 * @param <Q> request type
 * @param <R> success response type
 * @param <E> error response type,example login fail...
 */
public interface WebService<Q,R,E> {
    String DEFAULT_CHAR_SET="utf-8";
    int SUCCESS_CODE =200;
    void setRequestHeaders(Map<String, String> requestHeaders);
    Map<String, String> getRequestHeaders();
    void setCharSet(String charSet);
    void setRequestMethod(@Method.EnumRange int method);
    void setRequestUrl(String url);
    String getBodyContentType();
    String fetchBody();
    void setRequestEntity(Q requestEntity);
    Q getRequestEntity();
    void execute();
    Map<String, String> getResponseHeaders();
    void setResponseEntity(R responseEntity);
    R getResponseEntity();
    String getResponse();
    int getResponseHttpStatusCode();
    void setErrorEntity(E errorEntity);
    E getErrorEntity();

    void setWebServiceMonitor(WebServiceManager.WebServiceMonitor webServiceMonitor);
    void setMonitorTag(String tag);

    public static class Method {
        public static final int DEPRECATED_GET_OR_POST = -1;
        public static final int GET = 0;
        public static final int POST = 1;
        public static final int PUT = 2;
        public static final int DELETE = 3;
        public static final int HEAD = 4;
        public static final int OPTIONS = 5;
        public static final int TRACE = 6;
        public static final int PATCH = 7;

        @IntDef({DEPRECATED_GET_OR_POST, GET, POST, PUT, DELETE, HEAD, OPTIONS, TRACE, PATCH})
        @Retention(RetentionPolicy.SOURCE)
        public @interface EnumRange {
        }
    }
}
