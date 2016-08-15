package cn.trainservice.trainservice.journey.model;

import org.json.JSONException;
import org.json.JSONObject;

import cn.trainservice.trainservice.TrainServiceApplication;

/**
 * Created by BrainWang on 2016/4/8.
 */
public class CityInfo {
    public int mId = -1;
    public String mName = "- -";
    public String mIntroduce = "";
    public String mImageUrl = "";
    public String mAllIntroduceUrl = "";
    public String mAddress = "";
    private  String date = "";
    private String temperature = "";
    private String weather = "";
    private String wind = "";

    private static CityInfo currentCityInfo = new CityInfo();

    public CityInfo() {

    }

    private CityInfo(String json) {
        try {
            JSONObject jso = new JSONObject(json);
            JSONObject city = jso.getJSONObject("city");
            JSONObject weather_info = jso.getJSONObject("weather_info");
            mId = Integer.parseInt(city.getString("station_id"));
            mName = city.getString("s_name");
            mIntroduce = city.getString("description");
            if (city.has("img_url")) {
                mImageUrl = TrainServiceApplication.getURLGetCityPicture() + city.getString("img_url");
            }
            mAllIntroduceUrl = TrainServiceApplication.getUrlAllIntroduce()+"?station="+mId;
            wind = weather_info.getString("wind");
            temperature = weather_info.getString("temperature");
            date = weather_info.getString("date");

            date = date.split(" ")[0];
            weather = weather_info.getString("weather");
            //String mAllIntroduceHomeUrl = TrainServiceApplication.getCityInfoUrl(mId);
            // mImageUrl = mAllIntroduceHomeUrl + "/thumb.jpg";
            //  mAllIntroduceUrl = mAllIntroduceHomeUrl + "/index.html";


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public  String getWeatherInfo() {
        return "( " + date+ " "+temperature+" "+weather+ " "+wind +" )";

    }

    public static CityInfo getCurrentCityInfo() {
        if (currentCityInfo == null)
            currentCityInfo = new CityInfo();
        return currentCityInfo;

    }

    public static void loadCurrentCityData(String json) {
        currentCityInfo = new CityInfo(json);
//        currentCityInfo.mId=id;
//        currentCityInfo.mName=mName;
//        currentCityInfo.mIntroduce=mIntroduce;
//        currentCityInfo.mImageUrl=mImageUrl;
//        currentCityInfo.mAllIntroduceUrl=mAllIntroduceUrl;
//        currentCityInfo.mAddress=mAddress;
    }

}
