package com.iori.custom.webservice.volley.request.test;

import android.content.Context;

import com.google.gson.annotations.SerializedName;
import com.iori.custom.webservice.WebService;
import com.iori.custom.webservice.volley.request.VolleyFormRequest;

import java.util.Map;

public class LoginRequest extends VolleyFormRequest<Map<String,String>, LoginRequest.LoginSuccessEntity, LoginRequest.ErrorMessage> {
    public LoginRequest(Context context, LoginSuccessListener successListener, LoginFailListener formError) {
//        correct url
//        https://live01.168money.com.tw/api/app/backend/Auth/login
        super(context, WebService.Method.POST, "https://live01.168money.com.tw/api/app/backend/Auth/login", LoginSuccessEntity.class, LoginRequest.ErrorMessage.class, successListener, formError);
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
}
