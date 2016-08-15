package cn.trainservice.trainservice.service.model;

import java.util.HashMap;

/**
 * Created by BrainWang on 4/18/2016.
 */
public class ServiceItem {
    private HashMap<String,Object> ExtraInfo=new HashMap<>();
    public  void addExtraInfo(String key,String value){
        ExtraInfo.put(key,value);
    }
    public  Object getExtraByKey(String key){
        return ExtraInfo.get(key);
    }
}
