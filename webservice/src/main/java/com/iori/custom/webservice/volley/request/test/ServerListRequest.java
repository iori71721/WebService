package com.iori.custom.webservice.volley.request.test;

import android.content.Context;

import com.android.volley.NetworkResponse;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.iori.custom.webservice.WebService;
import com.iori.custom.webservice.volley.request.VolleyFormRequest;

import java.util.List;
import java.util.Map;

public class ServerListRequest extends VolleyFormRequest<Map<String,String>, List<ServerListRequest.ServerInfo>,String> {

    public ServerListRequest(Context context, ServiceListRequestSuccessListener successListener, ServiceListRequestErrorListener formError){
//        correct url
//        https://service-demo.hotsnet.com/api/app/Site
        super(context, WebService.Method.GET, "https://service-demo.hotsnet.com/api/app/Site"+"123", (Class<List<ServerListRequest.ServerInfo>>) (Class<?>) List.class,String.class, successListener, formError);
    }

    @Override
    protected List<ServerInfo> parseResponseSuccessEntity(NetworkResponse response, String reponseString) {
        return new Gson().fromJson(reponseString,new TypeToken<List<ServerInfo>>(){}.getType());
    }

    public static interface ServiceListRequestSuccessListener extends BaseVolleyRequestSuccessListener<Map<String,String>,List<ServerInfo>> {

    }

    public static interface ServiceListRequestErrorListener extends BaseVolleyRequestErrorListener<Map<String,String>,String> {}

    public static class ServerInfo{
        @SerializedName("name")
        private String name="";

        @SerializedName("site_url")
        private String site_url="";

        @SerializedName("app_live_websocket")
        private String app_live_websocket="";

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSite_url() {
            return site_url;
        }

        public void setSite_url(String site_url) {
            this.site_url = site_url;
        }

        public String getApp_live_websocket() {
            return app_live_websocket;
        }

        public void setApp_live_websocket(String app_live_websocket) {
            this.app_live_websocket = app_live_websocket;
        }
    }
}
