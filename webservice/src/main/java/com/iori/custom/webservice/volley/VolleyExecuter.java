package com.iori.custom.webservice.volley;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleyExecuter {
    private static VolleyExecuter executer;
    private final Context context;
    private RequestQueue requestQueue;

    private VolleyExecuter(Context context) {
        this.context=context;
    }

    /**
     * only bind init context
     * @param context
     * @return
     */
    public static synchronized VolleyExecuter getInstance(Context context){
        if (executer == null) {
            executer = new VolleyExecuter(context);
        }
        return executer;
    }

    private RequestQueue getRequestQueue() {
        if(requestQueue == null){
            requestQueue= Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
