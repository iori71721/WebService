package com.iori.custom.webservice.volley.request;

import android.content.Context;
import com.iori.custom.common.string.StringTool;

public class VolleyJsonRequest <Q,R,E> extends VolleyFormRequest<Q,R,E>{
    private Class<Q> requestType;
    private Q requestEntity;

    public VolleyJsonRequest(Context context, int method, String url,Class<Q> requestType, Class<R> responseType, Class<E> errorEntityType, BaseVolleyRequestSuccessListener successListener, BaseVolleyRequestErrorListener formError) {
        super(context, method, url, responseType, errorEntityType, successListener, formError);
        this.requestType=requestType;
    }

    @Override
    protected boolean ifEmptyRequest() {
        String body=generateBody();
        return StringTool.isEmpty(body);
    }

    @Override
    protected String generateBody() {
        if(requestType.equals(String.class)){
            return requestEntity.toString();
        }else{
            return getGson().toJson(requestEntity);
        }
    }

    @Override
    public String getBodyContentType() {
        return "text/plain";
    }

    @Override
    public Q getRequestEntity() {
        return requestEntity;
    }

    @Override
    public void setRequestEntity(Q requestEntity) {
        this.requestEntity = requestEntity;
    }

    public Class<Q> getRequestType() {
        return requestType;
    }
}
