package com.iori.custom.webservice.volley.request;

import android.content.Context;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.iori.custom.webservice.WebServiceInfo;

import java.util.UUID;

/**
 * ref https://www.jianshu.com/p/1684aadf8a41
 */
public class VolleyFormRequest<Q,R> extends VolleyWebService<Q,R>{
    private final String BOUNDARY = "------" + UUID.randomUUID().toString();
    private final String NEW_LINE = "\r\n";
    private final String MULTIPART_FORM_DATA = "multipart/form-data";
    private FormError formError;
    private Gson gson=new Gson();

    public VolleyFormRequest(Context context, int method, String url, Class<R> responseType, BaseVolleySuccessListener successListener, FormError formError) {
        super(context, method, url, responseType, successListener);
        this.formError=formError;
    }

    @Override
    protected R parseResponseEntity(NetworkResponse response, String reponseString) {
        R responseEntity= gson.fromJson(reponseString,getResponseType());
        return responseEntity;
    }

    @Override
    protected void delegateResponseError(VolleyError error) {
        if(formError != null){
            formError.delegateResponseError(error,webServiceInfo);
        }
    }

    @Override
    protected void parseResponseError(VolleyError error) {
        if(formError != null){
            formError.parseResponseError(error,webServiceInfo);
        }
    }

    @Override
    protected boolean ifEmptyRequest() {
        return getRequestHeaders() == null || getRequestHeaders().size() <= 0;
    }

    @Override
    protected String generateBody() {
        // ------WebKitFormBoundarykR96Kta4gvMACHfq                 第一行
        // Content-Disposition: form-data; name="login_username"    第二行
        //                                                          第三行
        // abcde                                                    第四行
        // ------WebKitFormBoundarykR96Kta4gvMACHfq--               结束行
        // 開始拼接數據
        StringBuffer stringBuffer = new StringBuffer();
        for (String key : getRequestHeaders().keySet()) {
            Object value = getRequestHeaders().get(key);
            stringBuffer.append("--" + BOUNDARY).append(NEW_LINE); // 第一行
            stringBuffer.append("Content-Disposition: form-data; name=\"").append(key).append("\"").append(NEW_LINE); // 第二行
            stringBuffer.append(NEW_LINE); // 第三行
            stringBuffer.append(value).append(NEW_LINE); // 第四行
        }
        // 所有参数拼接完成，拼接结束行
        stringBuffer.append("--" + BOUNDARY + "--").append(NEW_LINE);// 结束行
        return stringBuffer.toString();
    }

    @Override
    public String getBodyContentType() {
        // multipart/form-data; boundary=----WebKitFormBoundarykR96Kta4gvMACHfq
        return MULTIPART_FORM_DATA + ";boundary=" + BOUNDARY;
    }

    @Override
    public String fetchBody() {
        return webServiceInfo.requestBody;
    }

    /**
     *
     * @param <Q> request type
     * @param <R> response type
     */
    public static interface FormError<Q,R>{
        void delegateResponseError(VolleyError error, WebServiceInfo<Q,R> webServiceInfo);
        void parseResponseError(VolleyError error, WebServiceInfo<Q,R> webServiceInfo);
    }
}
