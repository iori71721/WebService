package com.iori.custom.webservice.tools;

import com.google.gson.Gson;

public class GsonTool {
    /**
     * only convert single entity,non used for collections
     * @param gson
     * @param parseString
     * @param parseClass
     * @param <X>
     * @return
     */
    public static <X> X convertSingleEntity(Gson gson, String parseString, Class<X> parseClass){
        X parseEntity;
        if(parseClass.equals(String.class)){
            parseEntity=parseClass.cast(parseString);
        }else{
            parseEntity=gson.fromJson(parseString,parseClass);
        }
        return parseEntity;
    }
}
