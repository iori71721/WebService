package com.iori.custom.webservice;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;
import com.iori.custom.webservice.volley.request.VolleyFormRequest;
import com.iori.custom.webservice.volley.request.VolleyWebService;
import com.iori.custom.webservice.volley.request.test.LoginRequest;
import com.iori.custom.webservice.volley.request.test.ServerListRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private String domain="https://live01.168money.com.tw/api/app/backend";
    private String apiurl="/Auth/login";
    private String loginRequestUrl=domain+apiurl;
    private String serverListUrl="https://service-demo.hotsnet.com/api/app/Site";
    private Button test_volley;
    private Button test_volley_mutli;

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
        test_volley_mutli=findViewById(R.id.test_volley_mutli);
    }

    private void triggerSetup(){
        test_volley.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testVolley();
            }
        });
        test_volley_mutli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testVolleyMutil();
            }
        });
    }

    private void setupExcludeInfo(VolleyFormRequest request){
//        request.debug_ErrorKey=VolleyFormRequest.DEBUG_ERROR_KEY_SON_IGNORE;
//        request.debug_ErrorKey=VolleyFormRequest.DEBUG_ERROR_KEY_PARENT_IGNORE;
    }
    
    private void testVolleyMutil(){
        ServerListRequest serverListRequest=new ServerListRequest(this, new ServerListRequest.ServiceListRequestSuccessListener() {
            @Override
            public void requestSuccess(List<ServerListRequest.ServerInfo> responseEntity, WebServiceInfo<Map<String, String>, List<ServerListRequest.ServerInfo>,Object> webServiceInfo) {
                Log.d("iori_VolleyMutil", "volleySuccess: response entity "+responseEntity.get(0).getName());
                Log.d("iori_VolleyMutil", "volleySuccess: response webservieinfo response entity "+webServiceInfo.getResponseSuccessEntity().get(0).getName());
                Log.d("iori_VolleyMutil", "volleySuccess: response webservieinfo request body "+webServiceInfo.requestBody+" response "+webServiceInfo.getResponseString());
                Log.d("iori_VolleyMutil", "volleySuccess: response webservieinfo request entity "+webServiceInfo.getRequestEntity()+" response entity "+webServiceInfo.getResponseSuccessEntity());
            }
        }, new ServerListRequest.ServiceListRequestErrorListener() {
            @Override
            public void delegateResponseError(VolleyError error, WebServiceInfo<Map<String, String>, Object,String> webServiceInfo, String errorEntity) {
                Log.d("iori_VolleyMutil", "delegateResponseError request "+webServiceInfo.requestBody+" response "+webServiceInfo.getResponseString());
                Log.d("iori_VolleyMutil", "delegateResponseError webservieinfo request entity "+webServiceInfo.getRequestEntity()+" error entity "+webServiceInfo.errorEntity);
            }

            @Override
            public void unexpectedError(VolleyError error, WebServiceInfo<Map<String, String>, Object,Object> webServiceInfo) {
                Log.d("iori_VolleyMutil", "unexpectedError request "+webServiceInfo.requestBody+" response "+webServiceInfo.getResponseString());
                Log.d("iori_VolleyMutil", "unexpectedError webserviceinfo request entity "+webServiceInfo.getRequestEntity());
            }
        });

//        setupExcludeInfo(serverListRequest);

        WebServiceManager.getInstance().execute(serverListRequest);

        LoginRequest loginRequest=new LoginRequest(this, new LoginRequest.LoginSuccessListener() {
            @Override
            public void requestSuccess(LoginRequest.LoginSuccessEntity responseEntity, WebServiceInfo<Map<String, String>, LoginRequest.LoginSuccessEntity,Object> webServiceInfo) {
                Log.d("iori_VolleyMutil", "login success request "+webServiceInfo.requestBody+" response "+webServiceInfo.getResponseString()+" token "+responseEntity.getToken());
                Log.d("iori_VolleyMutil", "login success request webserviceinfo request entity "+webServiceInfo.getRequestEntity());
            }
        }, new LoginRequest.LoginFailListener() {
            @Override
            public void delegateResponseError(VolleyError error, WebServiceInfo<Map<String, String>, Object, LoginRequest.ErrorMessage> webServiceInfo, LoginRequest.ErrorMessage errorEntity) {
                Log.d("iori_VolleyMutil", "login delegateResponseError: error response "+webServiceInfo.getResponseString()+" message "+errorEntity.getMessage());
                Log.d("iori_VolleyMutil", "login delegateResponseError: webserviceinfo request entity "+webServiceInfo.getRequestEntity()+" request body "+webServiceInfo.requestBody);
            }

            @Override
            public void unexpectedError(VolleyError error, WebServiceInfo<Map<String, String>, Object, Object> webServiceInfo) {
                Log.d("iori_VolleyMutil", "login unexpectedError: request body "+webServiceInfo.requestBody+" response "+webServiceInfo.getResponseString()+" status code "+webServiceInfo.getResponseHttpStatusCode());
                Log.d("iori_VolleyMutil", "login unexpectedError: webserviceinfo request entity "+webServiceInfo.getRequestEntity());
            }
        });

        Map<String,String> loginRequestMap=new HashMap<>(10);
        loginRequestMap.put("account","TAKO12345");
        loginRequestMap.put("password","TAKO12345123");
        loginRequest.setRequestHeaders(loginRequestMap);

//        setupExcludeInfo(loginRequest);

        WebServiceManager.getInstance().execute(loginRequest);
    }

    private void testVolley(){
        VolleyFormRequest<Map<String, String>, String,String> serverListRequest=new VolleyFormRequest<Map<String, String>, String,String>(this, WebService.Method.GET, serverListUrl+"123"
                , String.class,String.class
                , new VolleyWebService.BaseVolleyRequestSuccessListener<Map<String,String>,String>() {

            @Override
            public void requestSuccess(String responseEntity, WebServiceInfo<Map<String, String>, String,Object> webServiceInfo) {
                Log.d("iori_webservice", "volleySuccess: status code "+webServiceInfo.getResponseHttpStatusCode()+" response entity "+responseEntity);
                Log.d("iori_webservice", "volleySuccess:  response "+webServiceInfo.getResponseString());
                Log.d("iori_webservice", "volleySuccess: request entity "+webServiceInfo.getRequestEntity()+" body "+webServiceInfo.requestBody);
            }
        }
                , new VolleyWebService.BaseVolleyRequestErrorListener<Map<String, String>, String>() {
            @Override
            public void delegateResponseError(VolleyError error,WebServiceInfo<Map<String, String>,Object, String> webServiceInfo,String errorEntity) {
                Log.d("iori_webservice", "delegateResponseError: "+" response code "+webServiceInfo.getResponseHttpStatusCode()+" string "+webServiceInfo.getResponseString());
                Log.d("iori_webservice", "delegateResponseError: "+"error entity "+errorEntity);
            }

            @Override
            public void unexpectedError(VolleyError error, WebServiceInfo<Map<String, String>,Object, Object> webServiceInfo) {
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

        VolleyWebService.BaseVolleyRequestSuccessListener<Map<String, String>, LoginRequest.LoginSuccessEntity> loginSuccessListener=new VolleyWebService.BaseVolleyRequestSuccessListener<Map<String, String>, LoginRequest.LoginSuccessEntity>() {
            @Override
            public void requestSuccess(LoginRequest.LoginSuccessEntity responseEntity, WebServiceInfo<Map<String, String>, LoginRequest.LoginSuccessEntity,Object> webServiceInfo) {
                Log.d("iori_webservice", "login volleySuccess: response entity "+responseEntity+" request body "+webServiceInfo.requestBody);
                Log.d("iori_webservice", "login volleySuccess: response string "+webServiceInfo.getResponseString());
                Log.d("iori_webservice", "login volleySuccess: response entity token "+responseEntity.getToken()+" expire_timestamp "+responseEntity.getExpire_timestamp());
            }
        };

        VolleyFormRequest<Map<String, String>, LoginRequest.LoginSuccessEntity, LoginRequest.ErrorMessage> loginRequest=new VolleyFormRequest<>(this, WebService.Method.POST, loginRequestUrl+"1234"
                , LoginRequest.LoginSuccessEntity.class, LoginRequest.ErrorMessage.class, loginSuccessListener, new VolleyWebService.BaseVolleyRequestErrorListener<Map<String, String>, LoginRequest.ErrorMessage>() {
            @Override
            public void delegateResponseError(VolleyError error, WebServiceInfo webServiceInfo, LoginRequest.ErrorMessage errorEntity) {
                Log.d("iori_webservice", "login delegateResponseError: "+" response entity "+webServiceInfo.getResponseSuccessEntity()+" request body "+webServiceInfo.requestBody+" status code "+webServiceInfo.getResponseHttpStatusCode()+" response string "+webServiceInfo.getResponseString());
                Log.d("iori_webservice", "login delegateResponseError: "+" error status "+errorEntity.getStatus()+" message "+errorEntity.getMessage());
            }

            @Override
            public void unexpectedError(VolleyError error, WebServiceInfo webServiceInfo) {
                Log.d("iori_webservice", "login parseResponseError: response code "+webServiceInfo.getResponseHttpStatusCode()+" request body "+webServiceInfo.requestBody+" response "+webServiceInfo.getResponseString());
            }
        });


        Map<String,String> loginRequestMap=new HashMap<>(10);
        loginRequestMap.put("account","TAKO12345");
        loginRequestMap.put("password","TAKO12345");
        loginRequest.setRequestHeaders(loginRequestMap);

        WebServiceManager.getInstance().execute(loginRequest,"very haapy");

        test_volley.postDelayed(new Runnable() {
            @Override
            public void run() {
                WebServiceManager.getInstance().showHistoryWebServiceInfos();
            }
        },2000);
    }
}
