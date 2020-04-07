package com.iori.custom.webservice;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.iori.custom.webservice.volley.request.VolleyFormRequest;
import com.iori.custom.webservice.volley.request.VolleyWebService;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private String domain="https://live01.168money.com.tw/api/app/backend";
    private String apiurl="/Auth/login";
    private String loginRequestUrl=domain+apiurl;
    private String serverListUrl="https://service-demo.hotsnet.com/api/app/Site";
    private Button test_volley;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        com_iori_custom_webservice_activity_main.xml
        Log.d("iori", "onCreate: 5");
        setContentView(R.layout.com_iori_custom_webservice_activity_main);
        initLayout();
        triggerSetup();
    }

    private void initLayout(){
        test_volley=findViewById(R.id.test_volley);
    }

    private void triggerSetup(){
        test_volley.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testVolley();
            }
        });
    }

    private void testVolley(){
        VolleyFormRequest<Map<String, String>, String> serverListRequest=new VolleyFormRequest<Map<String, String>, String>(this, WebService.Method.GET, serverListUrl
                , String.class
                , new VolleyWebService.BaseVolleySuccessListener<Map<String,String>,String>() {

            @Override
            public void volleySuccess(String responseEntity, WebServiceInfo<Map<String, String>, String> webServiceInfo) {
                Log.d("iori_webservice", "volleySuccess: status code "+webServiceInfo.getResponseHttpStatusCode()+" response entity "+responseEntity);
                Log.d("iori_webservice", "volleySuccess:  response "+webServiceInfo.getResponseString());
                Log.d("iori_webservice", "volleySuccess: request entity "+webServiceInfo.getRequestEntity()+" body "+webServiceInfo.requestBody);
            }
        }
                , new VolleyFormRequest.FormError<Map<String, String>, String>() {
            @Override
            public void delegateResponseError(VolleyError error,WebServiceInfo<Map<String, String>, String> webServiceInfo) {
                Log.d("iori_webservice", "delegateResponseError: "+" response code "+webServiceInfo.getResponseHttpStatusCode()+" string "+webServiceInfo.getResponseString());
            }

            @Override
            public void parseResponseError(VolleyError error, WebServiceInfo<Map<String, String>, String> webServiceInfo) {
                Log.d("iori_webservice", "parseResponseError: "+" response code "+webServiceInfo.getResponseHttpStatusCode()+" string "+webServiceInfo.getResponseString());
            }
        }
        );
        Map<String,String> testHeader=new HashMap<>();
        testHeader.put("test1","happy test 1");
        testHeader.put("test2","happy test 2");
        serverListRequest.setRequestHeaders(testHeader);
        WebServiceManager.getInstance().execute(serverListRequest,"happy");

        Log.d("iori_webservice", "testVolley2: login request start execute");

        VolleyWebService.BaseVolleySuccessListener<Map<String, String>, LoginSuccessEntity> loginSuccessListener=new VolleyWebService.BaseVolleySuccessListener<Map<String, String>, LoginSuccessEntity>() {
            @Override
            public void volleySuccess(LoginSuccessEntity responseEntity, WebServiceInfo<Map<String, String>, LoginSuccessEntity> webServiceInfo) {
                Log.d("iori_webservice", "login volleySuccess: response entity "+responseEntity+" request body "+webServiceInfo.requestBody);
                Log.d("iori_webservice", "login volleySuccess: response string "+webServiceInfo.getResponseString());
                Log.d("iori_webservice", "login volleySuccess: response entity token "+responseEntity.getToken()+" expire_timestamp "+responseEntity.getExpire_timestamp());
            }
        };

        VolleyFormRequest<Map<String, String>, LoginSuccessEntity> loginRequest=new VolleyFormRequest<>(this, WebService.Method.POST, loginRequestUrl
                , LoginSuccessEntity.class, loginSuccessListener, new VolleyFormRequest.FormError() {
            @Override
            public void delegateResponseError(VolleyError error, WebServiceInfo webServiceInfo) {
                ErrorMessage errorMessage=new Gson().fromJson(webServiceInfo.getResponseString(),ErrorMessage.class);
                Log.d("iori_webservice", "login delegateResponseError: "+" response entity "+webServiceInfo.getResponseEntity()+" request body "+webServiceInfo.requestBody+" status code "+webServiceInfo.getResponseHttpStatusCode()+" response string "+webServiceInfo.getResponseString());
                Log.d("iori_webservice", "login delegateResponseError: "+" error status "+errorMessage.getStatus()+" message "+errorMessage.getMessage());
            }

            @Override
            public void parseResponseError(VolleyError error, WebServiceInfo webServiceInfo) {
                Log.d("iori_webservice", "login parseResponseError: response code "+webServiceInfo.getResponseHttpStatusCode()+" request body "+webServiceInfo.requestBody+" response "+webServiceInfo.getResponseString());
            }
        });


        Map<String,String> loginRequestMap=new HashMap<>(10);
        loginRequestMap.put("account","TAKO12345");
        loginRequestMap.put("password","TAKO12345123");
        loginRequest.setRequestHeaders(loginRequestMap);

        WebServiceManager.getInstance().execute(loginRequest,"very haapy");

        test_volley.postDelayed(new Runnable() {
            @Override
            public void run() {
                WebServiceManager.getInstance().showHistoryWebServiceInfos();
            }
        },2000);
    }

    private static class ErrorMessage{
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

    private static class LoginSuccessEntity{
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
}
