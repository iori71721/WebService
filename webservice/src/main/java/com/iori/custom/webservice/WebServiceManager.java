package com.iori.custom.webservice;

import android.util.Log;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class WebServiceManager {
    public static final String TAG=WebServiceManager.class.getSimpleName();
    private static WebServiceManager instance;
    private final List<WeakReference<WebServiceInfo>> historyWebServiceInfos=new ArrayList<>(500);
    private final ReferenceQueue<WebServiceInfo> referenceQueue=new ReferenceQueue<>();

    private WebServiceManager() {
    }
    public static synchronized WebServiceManager getInstance(){
        if(instance == null){
            instance=new WebServiceManager();
        }
        return instance;
    }

    private void addWebServiceInfo(WebServiceInfo webServiceInfo){
        synchronized (historyWebServiceInfos){
            historyWebServiceInfos.add(new WeakReference<WebServiceInfo>(webServiceInfo,referenceQueue));
        }
    }

    public void execute(WebService webService,String tag){
        webService.setMonitorTag(tag);
        webService.setWebServiceMonitor(new WebServiceMonitor() {
            @Override
            public void finish(WebServiceInfo webServiceInfo) {
                addWebServiceInfo(webServiceInfo);
            }
        });
        webService.execute();
    }

    public void execute(WebService webService){
        execute(webService,"");
    }

    public void showHistoryWebServiceInfos(){
        synchronized (historyWebServiceInfos){
            WebServiceInfo historyInfo;
            for(WeakReference<WebServiceInfo> historyInfoReference :historyWebServiceInfos){
                historyInfo=historyInfoReference.get();
                if(historyInfo != null){
                    Log.i(TAG, "showHistoryWebServiceInfos: monitor tag "+historyInfo.monitorTag+" url "+historyInfo.getRequestUrl()+" response status code "+historyInfo.getResponseHttpStatusCode());
                    Log.i(TAG, "showHistoryWebServiceInfos: request "+historyInfo.requestBody);
                    Log.i(TAG, "showHistoryWebServiceInfos: response "+historyInfo.getResponseString());
                }else{
                    Log.i(TAG, "showHistoryWebServiceInfos: empty history ");
                }
            }
        }
    }

    public static interface WebServiceMonitor{
        /**
         * when execute success or error,will call back
         * @param webServiceInfo
         */
        void finish (WebServiceInfo webServiceInfo);
    }
}
