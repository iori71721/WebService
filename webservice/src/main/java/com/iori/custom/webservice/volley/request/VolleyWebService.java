package com.iori.custom.webservice.volley.request;

import android.content.Context;
import android.util.Log;

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
import java.util.Map;

/**
 *
 * @param <Q> request type
 * @param <R> response type
 */
public abstract class VolleyWebService<Q,R> extends Request<R> implements WebService<Q,R> {
    private Context context;
    private Gson gson=new Gson();
    protected WebServiceInfo<Q,R> webServiceInfo =new WebServiceInfo<>();
    private BaseVolleySuccessListener successListener;
    private Class<R> responseType;
    private final Response.ErrorListener errorVolleyListener=new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            setupResponseInfo(error.networkResponse,error.networkResponse.statusCode);
            callbackWebServiceFinish(webServiceInfo);
            if(error instanceof ParseError){
                parseResponseError(error);
            }else {
                delegateResponseError(error);
            }
        }
    };
    public WebServiceMonitor webServiceMonitor;

    public VolleyWebService(Context context, int method, String url, Class<R> responseType, BaseVolleySuccessListener successListener) {
        super(method, url, null);
        this.context=context;
        this.successListener=successListener;
        this.responseType=responseType;
        setupErrorVolleyListener();
        setRequestMethod(method);
        setRequestUrl(url);
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
        webServiceInfo.setResponseEntity(responseEntity);
    }

    @Override
    public R getResponseEntity() {
        return webServiceInfo.getResponseEntity();
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
                responseEntity = parseResponseEntity(response, webServiceInfo.getResponseString());
            }
            webServiceInfo.setResponseEntity(responseEntity);
            return Response.success(responseEntity, HttpHeaderParser.parseCacheHeaders(response));
        }catch (Exception e){
            return Response.error(new ParseError(response));
        }
    }

    @Override
    protected void deliverResponse(R response) {
        successListener.volleySuccess(response,webServiceInfo);
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

    protected abstract R parseResponseEntity(NetworkResponse response, String reponseString);

    protected abstract void delegateResponseError(VolleyError error);

    protected abstract void parseResponseError(VolleyError error);

    protected abstract boolean ifEmptyRequest();

    protected abstract String generateBody();

    public Class<R> getResponseType() {
        return responseType;
    }

    /**
     * @param <Q> request type
     * @param <R> response type
     */
    public static interface BaseVolleySuccessListener<Q,R>{
        void volleySuccess(R responseEntity,WebServiceInfo<Q,R> webServiceInfo);
    }
}
