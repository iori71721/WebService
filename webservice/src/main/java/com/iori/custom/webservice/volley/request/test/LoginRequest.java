package com.iori.custom.webservice.volley.request.test;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.google.gson.annotations.SerializedName;
import com.iori.custom.webservice.WebService;
import com.iori.custom.webservice.WebServiceInfo;
import com.iori.custom.webservice.volley.request.VolleyFormRequest;

import java.util.Map;

public class LoginRequest extends VolleyFormRequest<Map<String,String>, LoginRequest.LoginSuccessEntity, LoginRequest.ErrorMessage> {
    public LoginRequest(Context context, LoginSuccessListener successListener, LoginFailListener formError) {
//        correct url
//        https://live01.168money.com.tw/api/app/backend/Auth/login
        super(context, WebService.Method.POST, "https://live01.168money.com.tw/api/app/backend/Auth/login", LoginSuccessEntity.class, LoginRequest.ErrorMessage.class, successListener, formError);
        initParentKeys();
    }

    private WebServiceInfo<Map<String, String>, LoginSuccessEntity, ErrorMessage> forceSetLogoutError(WebServiceInfo<Map<String, String>, LoginSuccessEntity, ErrorMessage> webServiceInfo){
        webServiceInfo.setResponseHttpStatusCode(403);
        return webServiceInfo;
    }

    private WebServiceInfo<Map<String, String>, LoginSuccessEntity, ErrorMessage> forceSetRefreshTokenError(WebServiceInfo<Map<String, String>, LoginSuccessEntity, ErrorMessage> webServiceInfo){
        webServiceInfo.setResponseHttpStatusCode(401);
        webServiceInfo.errorEntity.setStatus("700");
        return webServiceInfo;
    }

    @Override
    protected void commonUnexpectedErrorHandler(VolleyError error, WebServiceInfo<Map<String, String>, LoginSuccessEntity, ErrorMessage> webServiceInfo, String errorKey) {
        Log.d("iori_webservice", "commonUnexpectedErrorHandler: "+errorKey+" url "+webServiceInfo.getRequestUrl());
    }

    @Override
    protected void commonResponseErrorHandler(VolleyError error, WebServiceInfo<Map<String, String>, LoginSuccessEntity, ErrorMessage> webServiceInfo, String errorKey) {
        Log.d("iori_webservice", "commonResponseErrorHandler: "+errorKey+" url "+webServiceInfo.getRequestUrl());
    }

    @Override
    protected String parseUnexpectedError(VolleyError error, WebServiceInfo<Map<String, String>, LoginSuccessEntity, ErrorMessage> webServiceInfo) {
        String errorString="undefined";
        webServiceInfo=forceSetLogoutError(webServiceInfo);
        if(webServiceInfo.getResponseHttpStatusCode() == 403){
            errorString=ErrorKeys.LOGOUT;
        }
        Log.d("iori_webservice", "parseUnexpectedError: "+errorString+" url "+webServiceInfo.getRequestUrl());
        return errorString;
    }

    @Override
    protected String parseResponseError(VolleyError error, WebServiceInfo<Map<String, String>, LoginSuccessEntity, ErrorMessage> webServiceInfo) {
        String errorString="undefined";
//        webServiceInfo=forceSetRefreshTokenError(webServiceInfo);
        if(webServiceInfo.getResponseHttpStatusCode()==401 && webServiceInfo.errorEntity.status.equals("700")){
            errorString=ErrorKeys.REFRESH_TOKEN;
        }
        Log.d("iori_webservice", "parseResponseError: "+errorString+" url "+webServiceInfo.getRequestUrl());
        return errorString;
    }

    private void initParentKeys(){
        synchronized (handleByParentKeys){
            handleByParentKeys.add(ErrorKeys.REFRESH_TOKEN);
            handleByParentKeys.add(ErrorKeys.LOGOUT);
        }
    }

    public static interface LoginSuccessListener extends BaseVolleyRequestSuccessListener<Map<String,String>, LoginSuccessEntity> {}

    public static interface LoginFailListener extends BaseVolleyRequestErrorListener<Map<String,String>,ErrorMessage> {}

    public static class LoginSuccessEntity{
        @SerializedName("token")
        private String token;
        @SerializedName("expire_timestamp")
        private long expire_timestamp;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public long getExpire_timestamp() {
            return expire_timestamp;
        }

        public void setExpire_timestamp(long expire_timestamp) {
            this.expire_timestamp = expire_timestamp;
        }
    }

    public static class ErrorMessage{
        @SerializedName("status")
        private String status;
        @SerializedName("message")
        private String message;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static class ErrorKeys{
        public static final String REFRESH_TOKEN="refresh_token";
        public static final String PASSWORD_ERROR="password error";
        public static final String LOGOUT="LOGOUT";
    }
}
