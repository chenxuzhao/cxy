package cn.trainservice.trainservice.journey.util;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Date;

import cn.trainservice.trainservice.journey.model.TrainStation;

/**
 * Created by BrainWang on 2016/4/8.
 */
public class JsonHelper {

    public static ArrayList<TrainStation> parseStationList(String str) {
        ArrayList<TrainStation> list = new ArrayList<>();
        try {
            JSONObject Json = new JSONObject(str);
            boolean result = Json.getBoolean("result");
            if (result) {
                int size = Json.getInt("station_size");
                JSONObject stationsJson = Json.getJSONObject("stations");

                String index = "0";
                Date start = new Date();
                Date end = new Date();
                int day =0;
                for (int i = 0; i < size; i++) {
                    JSONObject jsonObject = stationsJson.getJSONObject(index);
                    index = jsonObject.getString("station_id");
                    list.add(i ,new TrainStation(jsonObject));
                }



            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }
}
