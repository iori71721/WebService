package com.iori.custom.webservice.volley.request;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.iori.custom.webservice.WebService;
import com.iori.custom.webservice.WebServiceInfo;
import com.iori.custom.webservice.WebServiceManager.*;
import com.iori.custom.webservice.volley.VolleyExecuter;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @param <Q> request type
 * @param <R> response type
 * @param <E> error entity type
 */
public abstract class VolleyWebService<Q,R,E> extends Request<R> implements WebService<Q,R,E> {
    private boolean debug=false;
    private final String TAG=VolleyWebService.class.getName();
    public static final String DEBUG_ERROR_KEY_SON_IGNORE="DEBUG_ERROR_KEY_SON_IGNORE";
    public static final String DEBUG_ERROR_KEY_PARENT_IGNORE="DEBUG_ERROR_KEY_PARENT_IGNORE";
    public String debug_ErrorKey="debug_ErrorKey";

    private Context context;
    private Gson gson=new Gson();
    protected WebServiceInfo<Q,R,E> webServiceInfo =new WebServiceInfo<>();

    protected boolean commonResponseErrorHandler=true;
    protected boolean deliverResponseError =true;
    protected boolean commonUnexpectedErrorHandler=true;
    protected boolean deliverUnexpectedError =true;
    protected @NonNull Set<String> notDownKeys=new HashSet<>(10);
    protected @NonNull Set<String> onlyExecuteByDownKeys=new HashSet<>(10);

    private final BaseVolleyRequestSuccessListener successListener;
    private Class<R> responseType;
    private Class<E> errorEntityType;
    private final Response.ErrorListener errorVolleyListener=new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            if(error.networkResponse != null) {
                setupResponseInfo(error.networkResponse, error.networkResponse.statusCode);
                callbackWebServiceFinish(webServiceInfo);
                if (isUnexpectedError(error)) {
                    handleUnexpectedError(error, webServiceInfo);
                } else {
                    try {
                        E errorEntity = parseResponseErrorEntity(error, webServiceInfo);
                        setErrorEntity(errorEntity);
                        handleResponseError(error, webServiceInfo);
                    } catch (Exception e) {
                        handleUnexpectedError(error, webServiceInfo);
                    }
                }
            }else{
                handleUnexpectedError(error, webServiceInfo);
            }
        }
    };
    public WebServiceMonitor webServiceMonitor;

    protected abstract void commonResponseErrorHandler(VolleyError error, WebServiceInfo<Q, R, E> webServiceInfo);

    protected abstract void commonUnexpectedErrorHandler(VolleyError error, WebServiceInfo<Q, R, E> webServiceInfo);

    protected abstract String parseResponseError(VolleyError error, WebServiceInfo<Q, R, E> webServiceInfo);

    protected abstract String parseUnexpectedError(VolleyError error, WebServiceInfo<Q, R, E> webServiceInfo);

    private boolean isNotDownKeys(String errorKey){
        synchronized (notDownKeys){
            return notDownKeys.contains(errorKey)?true:false;
        }
    }

    private boolean isOnlyExecuteByDownKeys(String errorKey){
        synchronized (onlyExecuteByDownKeys){
            return onlyExecuteByDownKeys.contains(errorKey)?true:false;
        }
    }

    public VolleyWebService(Context context, int method, String url, Class<R> responseType,Class<E> errorEntityType, BaseVolleyRequestSuccessListener successListener) {
        super(method, url, null);
        this.context=context;
        this.successListener=successListener;
        this.responseType=responseType;
        this.errorEntityType=errorEntityType;
        setupErrorVolleyListener();
        setRequestMethod(method);
        setRequestUrl(url);
    }

    public static boolean isUnexpectedError(VolleyError error){
        if(error instanceof ParseError){
            return true;
        }
        return false;
    }

    private void setupErrorVolleyListener(){
        try {
            Field mErrorListener=Request.class.getDeclaredField("mErrorListener");
            mErrorListener.setAccessible(true);
            mErrorListener.set(this,errorVolleyListener);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setRequestHeaders(Map<String, String> requestHeaders) {
        webServiceInfo.setupRequestHeaders(requestHeaders);
    }

    @Override
    public void setCharSet(String charSet) {
        webServiceInfo.setupCharSet(charSet);
    }

    @Override
    public Map<String, String> getRequestHeaders() {
        return webServiceInfo.getRequestHeaders();
    }

    @Override
    public void setRequestMethod(int method) {
        webServiceInfo.setupRequestMethod(method);
    }

    @Override
    public void setRequestUrl(String url) {
        webServiceInfo.setRequestUrl(url);
    }



    @Override
    public void execute() {
        VolleyExecuter.getInstance(context).addToRequestQueue(this);
    }

    @Override
    public Map<String, String> getResponseHeaders() {
        return webServiceInfo.getResponseHeaders();
    }

    @Override
    public String getResponse() {
        return webServiceInfo.getResponseString();
    }

    @Override
    public int getResponseHttpStatusCode() {
        return webServiceInfo.getResponseHttpStatusCode();
    }

    @Override
    public void setRequestEntity(Q requestEntity) {
        webServiceInfo.setRequestEntity(requestEntity);
    }

    @Override
    public Q getRequestEntity() {
        return webServiceInfo.getRequestEntity();
    }

    @Override
    public void setResponseEntity(R responseEntity) {
        webServiceInfo.setResponseSuccessEntity(responseEntity);
    }

    @Override
    public R getResponseEntity() {
        return webServiceInfo.getResponseSuccessEntity();
    }

    @Override
    public void setErrorEntity(E errorEntity) {
        webServiceInfo.errorEntity=errorEntity;
    }

    @Override
    public E getErrorEntity() {
        return webServiceInfo.errorEntity;
    }

    @Override
    public void setWebServiceMonitor(WebServiceMonitor webServiceMonitor) {
        this.webServiceMonitor = webServiceMonitor;
    }

    @Override
    public void setMonitorTag(String tag) {
        webServiceInfo.monitorTag=tag;
    }

    @Override
    protected Response<R> parseNetworkResponse(NetworkResponse response) {
        setupResponseInfo(response,response.statusCode);
        try {
            R responseEntity=null;
            if(getResponseType().equals(String.class)) {
                responseEntity =getResponseType().cast(webServiceInfo.getResponseString());
            }else{
                responseEntity = parseResponseSuccessEntity(response, webServiceInfo.getResponseString());
            }
            webServiceInfo.setResponseSuccessEntity(responseEntity);
            return Response.success(responseEntity, HttpHeaderParser.parseCacheHeaders(response));
        }catch (Exception e){
            return Response.error(new ParseError(response));
        }
    }

    @Override
    protected void deliverResponse(R response) {
        successListener.requestSuccess(response,webServiceInfo);
        callbackWebServiceFinish(webServiceInfo);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return webServiceInfo.getRequestHeaders();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        if(ifEmptyRequest()){
            return super.getBody();
        }
        String body=generateBody();
        webServiceInfo.requestBody=body;
        try {
            return body.getBytes(WebService.DEFAULT_CHAR_SET);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
//            使用默認的編碼方式，Android為utf-8
            return body.getBytes();
        }
    }

    private void setupResponseInfo(NetworkResponse response, int responseHttpStatusCode){
        String responseCharSet= HttpHeaderParser.parseCharset(response.headers, webServiceInfo.getCharSet());
        webServiceInfo.setupResponseHeaders(response.headers);
        String responseString= parseResponseString(response,responseCharSet);
        webServiceInfo.setResponseString(responseString);
        webServiceInfo.setResponseHttpStatusCode(responseHttpStatusCode);
    }

    protected String parseResponseString(NetworkResponse response, String charSet){
        String responseString="";
        try {
            responseString=new String(response.data,charSet);
        } catch (UnsupportedEncodingException e) {
            responseString=new String(response.data);
            e.printStackTrace();
        }
        return responseString;
    }

    public void callbackWebServiceFinish(WebServiceInfo webServiceInfo){
        if(webServiceMonitor != null){
            webServiceMonitor.finish(webServiceInfo);
        }
    }

    protected abstract R parseResponseSuccessEntity(NetworkResponse response, String reponseString);

    protected abstract E parseResponseErrorEntity(VolleyError error, WebServiceInfo<Q,R,E> webServiceInfo);

    protected abstract void delegateResponseError(VolleyError error, WebServiceInfo<Q,R,E> webServiceInfo);

    /**
     * unexpected server response error,like url fail,server shut down...
     * @param error
     * @param webServiceInfo
     */
    protected abstract void unexpectedError(VolleyError error, WebServiceInfo<Q,R,E> webServiceInfo);

    private void handleResponseError(VolleyError error, WebServiceInfo<Q,R,E> webServiceInfo){
        String errorKey=debug?debug_ErrorKey:parseResponseError(error,webServiceInfo);
        if(commonResponseErrorHandler && !isOnlyExecuteByDownKeys(errorKey)){
            commonResponseErrorHandler(error,webServiceInfo);
        }

        if(deliverResponseError && !isNotDownKeys(errorKey)){
            delegateResponseError(error,webServiceInfo);
        }
    }

    private void handleUnexpectedError(VolleyError error, WebServiceInfo<Q,R,E> webServiceInfo){
        String errorKey=debug?debug_ErrorKey:parseUnexpectedError(error,webServiceInfo);
        if(commonUnexpectedErrorHandler && !isOnlyExecuteByDownKeys(errorKey)){
            commonUnexpectedErrorHandler(error,webServiceInfo);
        }

        if(deliverUnexpectedError && !isNotDownKeys(errorKey)){
            unexpectedError(error,webServiceInfo);
        }
    }

    protected abstract boolean ifEmptyRequest();

    protected abstract String generateBody();

    public Class<R> getResponseType() {
        return responseType;
    }

    public Class<E> getErrorEntityType() {
        return errorEntityType;
    }

    public Gson getGson() {
        return gson;
    }

    public BaseVolleyRequestSuccessListener getSuccessListener() {
        return successListener;
    }

    /**
     * @param <Q> request type
     * @param <R> response type
     */
    public static interface BaseVolleyRequestSuccessListener<Q,R>{
        void requestSuccess(R responseEntity, WebServiceInfo<Q,R,Object> webServiceInfo);
    }

    /**
     *
     * @param <Q> request type
     * @param <E> error entity type
     */
    public static interface BaseVolleyRequestErrorListener<Q,E>{
        void delegateResponseError(VolleyError error, WebServiceInfo<Q,Object,E> webServiceInfo,E errorEntity);
        void unexpectedError(VolleyError error, WebServiceInfo<Q,Object,Object> webServiceInfo);
    }
}
