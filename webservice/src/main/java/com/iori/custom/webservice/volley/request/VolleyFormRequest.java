package com.iori.custom.webservice.volley.request;

import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.iori.custom.webservice.WebServiceInfo;
import com.iori.custom.webservice.tools.GsonTool;

import java.util.UUID;

/**
 * ref https://www.jianshu.com/p/1684aadf8a41
 */
public class VolleyFormRequest<Q,R,E> extends VolleyWebService<Q,R,E>{
    private final String BOUNDARY = "------" + UUID.randomUUID().toString();
    private final String NEW_LINE = "\r\n";
    private final String MULTIPART_FORM_DATA = "multipart/form-data";
    private final BaseVolleyRequestErrorListener formError;

    public VolleyFormRequest(Context context, int method, String url, Class<R> responseType, Class<E> errorEntityType, BaseVolleyRequestSuccessListener successListener, BaseVolleyRequestErrorListener formError) {
        super(context, method, url, responseType,errorEntityType, successListener);
        this.formError=formError;
    }

    @Override
    protected R parseResponseSuccessEntity(NetworkResponse response, String reponseString) {
        R responseEntity= GsonTool.convertSingleEntity(getGson(),reponseString,getResponseType());
        return responseEntity;
    }

    @Override
    protected void delegateResponseError(VolleyError error, WebServiceInfo<Q,R,E> webServiceInfo) {
        if(formError != null){
            formError.delegateResponseError(error,webServiceInfo,webServiceInfo.errorEntity);
        }
    }

    @Override
    protected E parseResponseErrorEntity(VolleyError error, WebServiceInfo<Q, R, E> webServiceInfo) {
        E errorEntity=GsonTool.convertSingleEntity(getGson(),webServiceInfo.getResponseString(),getErrorEntityType());
        return errorEntity;
    }

    @Override
    protected void unexpectedError(VolleyError error, WebServiceInfo<Q,R,E> webServiceInfo) {
        if(formError != null){
            formError.unexpectedError(error,webServiceInfo);
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

    public BaseVolleyRequestErrorListener getFormError() {
        return formError;
    }
}
