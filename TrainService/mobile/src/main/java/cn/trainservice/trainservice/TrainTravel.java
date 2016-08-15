package cn.trainservice.trainservice;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.trainservice.trainservice.journey.model.TrainStation;
import cn.trainservice.trainservice.service.Commoditys;

/**
 * Created by chenxu on 2016/7/29.
 */

public class TrainTravel {
    private static String[] ticket_types = {"", "儿童票", "成人票", "学生票", "伤残军人票"};
    private static String[] seatTypes = {"", "商务座", "特等座", "一等座", "二等座"};
    public static String user_name = "";
    public static String user_id = "";
    public static boolean islogin = false;
    public static boolean isload = false;
    public static boolean reset = false;
    public static int current_station = 3;
    public static String train_id = "";
    public static String StartStation = "~~~";
    public static String EndStation = "~~~";
    public static int StartStation_id = -1;
    public static int EndStationid_id = -1;
    public static String Carrige;
    public static String seat;
    public static int ticket_type = 0;//成人票
    public static int seatType = 0; //
    public static String SatrtTime = "";
    public static String ArriveTime = "";
    public static String ThoughtTime = "";
    // public static  int CurentStation= -1;////
    public static List<TrainStation> stationslist = new ArrayList<>();

    public static String get_ticket_type() {
        return ticket_types[ticket_type];
    }

    public static String get_seatType() {
        return seatTypes[seatType];
    }

    public static void reset() {

         user_name = "";
        user_id = "";
        islogin = false;
        isload = false;
        current_station = -1;
        train_id = "";
         StartStation = "~~~";
        EndStation = "~~~";
        StartStation_id = -1;
        EndStationid_id = -1;
        Carrige = "";
        seat = "";
        ticket_type = 0;//成人票
        seatType = 0; //
        SatrtTime = "";
        ArriveTime = "";
        ThoughtTime = "";
        // public static  int CurentStation= -1;////
        stationslist.clear();
        Commoditys.reset();
        reset = true;
    }

    public static void updateTrainTravel(boolean isload) {
        if(isload){
            int day = 0;
            Date Start = new Date();
            Date end = new Date();
            for (TrainStation trainStation : TrainTravel.stationslist) {
                if (trainStation.mId == TrainTravel.StartStation_id) {
                    day = trainStation.day - day;
                    Start = trainStation.ArrivalTime;
                    SatrtTime = trainStation.mArrivalTime;
                }
                if (trainStation.mId == TrainTravel.EndStationid_id) {
                    day = trainStation.day - day;
                    end = trainStation.ArrivalTime;
                    ArriveTime = trainStation.mArrivalTime;
                }
                int sub = (day * 24 + end.getHours()) * 60 + end.getMinutes() - (Start.getHours() * 60 + Start.getMinutes());
                String sday = (sub / (24 * 60) > 0) ? sub / (24 * 60) + "Day" : "";
                String shour = ((sub / 60) % 24 > 0) ? (sub / 60) % 24 + "Hour" : "";
                String sminu = (sub % 60 > 0) ? sub % 60 + "Minu" : "";

                ThoughtTime = sday + shour + sminu;
        }

        }

    }

}
