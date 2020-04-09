package com.iori.custom.webservice;

import java.util.HashMap;
import java.util.Map;

/**
 * save webservice function data
 * @param <Q> request type
 * @param <R> response type
 * @param <E> error entity type
 */
public class WebServiceInfo<Q,R,E>{
    /**
     * used for {@link WebServiceManager} see {@link WebServiceManager#execute(WebService, String)}
     */
    public String monitorTag;
    private String charSet="utf-8";
    private final Map<String, String> requestHeaders=new HashMap<String, String>(10);
    private @WebService.Method.EnumRange int requestMethod= WebService.Method.POST;
    private String requestUrl="";
    public String requestBody="";
    private Q requestEntity;
    private final Map<String, String> responseHeaders=new HashMap<String, String>(10);
    private String responseString;
    private R responseSuccessEntity;
    private int responseHttpStatusCode=-1;
    public E errorEntity;

    public void setupCharSet(String charSet) {
        this.charSet=charSet;
    }

    public void setupRequestMethod(@WebService.Method.EnumRange int method) {
        this.requestMethod=method;
    }

    public void setRequestUrl(String url) {
        requestUrl=url;
    }

    public Q getRequestEntity() {
        return requestEntity;
    }

    public void setRequestEntity(Q requestEntity) {
        this.requestEntity = requestEntity;
    }

    public R getResponseSuccessEntity() {
        return responseSuccessEntity;
    }

    public String getResponseString() {
        return responseString;
    }

    public void setResponseString(String responseString) {
        this.responseString = responseString;
    }

    public int getResponseHttpStatusCode() {
        return responseHttpStatusCode;
    }

    public void setResponseHttpStatusCode(int responseHttpStatusCode) {
        this.responseHttpStatusCode = responseHttpStatusCode;
    }

    public void setResponseSuccessEntity(R responseSuccessEntity) {
        this.responseSuccessEntity = responseSuccessEntity;
    }

    public String getCharSet() {
        return charSet;
    }

    public void setupRequestHeaders(Map<String, String> requestHeaders) {
        synchronized (this.requestHeaders){
            this.requestHeaders.clear();
            this.requestHeaders.putAll(requestHeaders);
        }
    }

    public Map<String, String> getRequestHeaders() {
        return requestHeaders;
    }

    public void setupResponseHeaders(Map<String, String> responseHeaders) {
        synchronized (this.responseHeaders){
            this.responseHeaders.clear();
            this.responseHeaders.putAll(responseHeaders);
        }
    }

    public Map<String, String> getResponseHeaders() {
        return responseHeaders;
    }

    public String getRequestUrl() {
        return requestUrl;
    }
}
