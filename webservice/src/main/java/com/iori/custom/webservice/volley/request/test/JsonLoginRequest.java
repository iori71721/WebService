package com.iori.custom.webservice.volley.request.test;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.google.gson.annotations.SerializedName;
import com.iori.custom.webservice.WebService;
import com.iori.custom.webservice.WebServiceInfo;
import com.iori.custom.webservice.volley.request.VolleyJsonRequest;

import java.util.Map;

public class JsonLoginRequest extends VolleyJsonRequest<JsonLoginRequest.LoginRequestEntity, JsonLoginRequest.LoginSuccessEntity, JsonLoginRequest.ErrorMessage> {
    public JsonLoginRequest(Context context, LoginSuccessListener successListener, LoginFailListener formError) {
//        correct url
//        https://live01.168money.com.tw/api/app/backend/Auth/login
        super(context, WebService.Method.POST, "https://live01.168money.com.tw/api/app/LiveStreamer/Store/login",LoginRequestEntity.class, LoginSuccessEntity.class, JsonLoginRequest.ErrorMessage.class, successListener, formError);
    }

    @Override
    protected void commonUnexpectedErrorHandler(VolleyError error, WebServiceInfo<JsonLoginRequest.LoginRequestEntity, LoginSuccessEntity, ErrorMessage> webServiceInfo, String errorKey) {
        Log.d("iori_webservice", "commonUnexpectedErrorHandler: "+errorKey+" url "+webServiceInfo.getRequestUrl());
    }

    @Override
    protected void commonResponseErrorHandler(VolleyError error, WebServiceInfo<JsonLoginRequest.LoginRequestEntity, LoginSuccessEntity, ErrorMessage> webServiceInfo, String errorKey) {
        Log.d("iori_webservice", "commonResponseErrorHandler: "+errorKey+" url "+webServiceInfo.getRequestUrl());
    }

    @Override
    protected String parseUnexpectedError(VolleyError error, WebServiceInfo<JsonLoginRequest.LoginRequestEntity, LoginSuccessEntity, ErrorMessage> webServiceInfo) {
        return "";
    }

    @Override
    protected String parseResponseError(VolleyError error, WebServiceInfo<JsonLoginRequest.LoginRequestEntity, LoginSuccessEntity, ErrorMessage> webServiceInfo) {
        return "";
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

    public static class LoginRequestEntity{
        @SerializedName("account")
        private String account="";

        @SerializedName("password")
        private String password="";

        @SerializedName("device_id")
        private String deviceID="";

        public void setAccount(String account) {
            this.account = account;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public void setDeviceID(String deviceID) {
            this.deviceID = deviceID;
        }

        public String getAccount() {
            return account;
        }

        public String getPassword() {
            return password;
        }

        public String getDeviceID() {
            return deviceID;
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
}
