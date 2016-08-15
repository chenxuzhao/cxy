package cn.trainservice.trainservice.service.model;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import cn.trainservice.trainservice.R;
import cn.trainservice.trainservice.service.serviceItem.ServiceItemDetailActivity;
import cn.trainservice.trainservice.service.serviceItem.ServiceItemDetailFragment;
import cn.trainservice.trainservice.service.serviceItem.ServiceItemListActivity;
import cn.trainservice.trainservice.service.serviceItem.dummy.DummyContent;

/**
 * Created by BrainWang on 4/18/2016.
 */
public abstract class SingleSimpleService {
    public final String mName,mListUrl;
    public final String[] types;
    private HashMap<String,Object> ExtraInfo;
    public SingleSimpleService(String name, String listUrl, String[] types){

        this.mName = name;
        this.mListUrl = listUrl;
        this.types = types;
    }
    public  void addExtraInfo(String key,String value){
        ExtraInfo.put(key,value);
    }
    public  Object getExtraByKey(String key){
       return ExtraInfo.get(key);
    }

    public abstract SimpleServiceRecyclerViewAdapter getViewAdapter();
    public abstract Drawable getThemeColor();
    public abstract void jumpToDetail();


}
