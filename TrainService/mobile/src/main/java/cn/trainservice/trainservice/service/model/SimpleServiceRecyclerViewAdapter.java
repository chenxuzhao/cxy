package cn.trainservice.trainservice.service.model;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.litesuits.http.HttpConfig;
import com.litesuits.http.LiteHttp;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.listener.HttpListener;
import com.litesuits.http.request.StringRequest;
import com.litesuits.http.response.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import cn.trainservice.trainservice.TrainServiceApplication;
import cn.trainservice.trainservice.TrainTravel;
import cn.trainservice.trainservice.journey.util.JsonHelper;

/**
 * Created by BrainWang on 4/18/2016.
 */
public abstract class SimpleServiceRecyclerViewAdapter
        extends RecyclerView.Adapter<SimpleServiceRecyclerViewAdapter.ViewHolder> {
    public ArrayList<ServiceItem> mServiceItems = new ArrayList<>();

    public SimpleServiceRecyclerViewAdapter(final Context context, final String url) {


        new Thread(new Runnable() {
            @Override
            public void run() {
                TrainServiceApplication.getLiteHttp(context).execute(new StringRequest(url).setHttpListener(
                        new HttpListener<String>() {
                            @Override
                            public void onSuccess(String data, Response<String> response) {
                                loadListFromData(data);
                            }

                            @Override
                            public void onFailure(HttpException e, Response<String> response) {
                                Log.i("httpp", e.toString());
                            }
                        }));
            }
        }).start();

    }


    private void loadListFromData(String data) {
        try {
            Log.d("data3", data);
            JSONObject jso = new JSONObject(data);
            boolean success = jso.getBoolean("result");
            if (success) {
                JSONArray items = jso.getJSONArray("items");

                for (int i = 0; i < items.length(); i++) {
                    JSONObject js = (JSONObject) items.get(i);
                    Iterator it = js.keys();
                    ServiceItem item = new ServiceItem();
                    while (it.hasNext()) {
                        String key = String.valueOf(it.next());
                        String value = (String) js.get(key);
                        item.addExtraInfo(key, value);

                    }
                    mServiceItems.add(item);
                }
                notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public abstract class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}

