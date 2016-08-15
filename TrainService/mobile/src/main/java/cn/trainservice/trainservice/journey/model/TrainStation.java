package cn.trainservice.trainservice.journey.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by BrainWang on 2016/4/6.
 */
public class TrainStation {
    public int mId=-1;
    public String mName="";
    public String mArrivalTime="";
    public  int mTimeStay=3;
    public int day;
    public Date ArrivalTime;
    public TrainStation(int id,String name,String arrivaltime,int timeStay){
        mName=name;
        mId=id;
        mArrivalTime=arrivaltime;
        mTimeStay=timeStay;
    }

    public TrainStation(JSONObject js){

        try {
            mName=js.getString("s_name");
            mId=Integer.parseInt(js.getString("station_id"));
            mArrivalTime=js.getString("arrival_time");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            ArrivalTime = (Date) simpleDateFormat.parse(mArrivalTime);//将这个字符型的时间转换成Date型的时间
            Log.d("data2" ,ArrivalTime.toString());
            day = Integer.parseInt(js.getString("day"));
            mTimeStay=Integer.parseInt(js.getString("stop_time"));
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (ParseException e) {
        e.printStackTrace();
    }
    }


}
