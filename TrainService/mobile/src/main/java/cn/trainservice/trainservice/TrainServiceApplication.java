package cn.trainservice.trainservice;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.beardedhen.androidbootstrap.font.FontAwesome;
import com.litesuits.http.HttpConfig;
import com.litesuits.http.LiteHttp;
import com.litesuits.http.data.NameValuePair;

import java.util.ArrayList;
import java.util.List;

import cn.trainservice.trainservice.journey.view.TicketInfo;

/**
 * Created by BrainWang on 2016/3/27.
 */
public class TrainServiceApplication extends Application {

    public static boolean hasLogin = false;
    public static String JourneyBroadcastAction = "cn.trainservice.trainservice.JourneyBroadcastAction";
    public static String ChatDiscoverBroadcastAction = "cn.trainservice.trainservice.ChatDiscoverBroadcastAction";

    //some url
    private static String http = "http";
    private static String serverIP = "www.cngeek.wang/web_trainServer";
    private static final String URLGetStationList = "/home/mobile/stations_by_train";
    private static final String URLGetCurrentCityInfo = "/home/mobile/currentCityInfo";
    private static final String URLGetCityInfo = "/home/mobile/cityInfo";
    private static final String UrlGetAllIntroduce = "/home/mobile/AllScenicIntroduce";
    private static final String URLGetCityPicture = "/Uploads/station_img/";
    private static final String URLGetCityDetailInfo = "/home/mobile/cityDetailInfo";
    private static final String URLLogin = "/home/mobile/login";
    private static final String URLSubmitOreder = "/home/mobile/submit_order";
    private static final String URLGetMovieList = "/home/mobile/vedios";
    private static final String URLGetMovieImagePath = "/Uploads/vedio_img/";
    private static final String URLGetFoodImagePath = "/Uploads/food_img/";
    private static final String URLGetMoviePath = "/Uploads/vedios/";
    private static final String URLGetSubmitOrderInfo = "/home/mobile/SubmitOrderInfo";
    private static final String URLGetConfimeOrderInfo = "/home/mobile/confirm_order";
    private static final String URLGetSuperPlayerApk = "/Uploads/SuperPlay/Superplayer-debug.apk";
    //public static String URLGetCurrentCityInfo="/journey/currentCityInfo";

    private static TicketInfo ticket;
    private static String URLGetFoodList = "/home/mobile/foodlist/";

    public static String[] getTabTitles(Context context) {
        String[] result = {
                context.getString(R.string.tab_journey),
                context.getString(R.string.tab_services),
                context.getString(R.string.tab_chat)
        };
        return result;
    }

    public static String user_Login() {
        return http + "://" + serverIP + URLLogin;
    }

    public static String submit_order() {
        return http + "://" + serverIP + URLSubmitOreder;
    }

    public static String[] getTabIcons(Context context) {
        String[] result = {
                FontAwesome.FA_TRAIN,
                FontAwesome.FA_TH_LARGE,
                FontAwesome.FA_COMMENTS
        };
        return result;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        TypefaceProvider.registerDefaultIconSets();
    }

    public static void setTickt(TicketInfo tk) {
        ticket = tk;
    }

    public static TicketInfo getTicket() {
        return ticket;
    }

    public static void attemptToEnterUserCenter(Activity activity) {
        if (!TrainTravel.islogin) {
            activity.startActivity(new Intent(activity, LoginActivity.class));
        } else {
            activity.startActivity(new Intent(activity, UserCenterActivity.class));
        }
    }

    public static String getWebServerHome() {
        return http + "://" + serverIP;
    }

    public static String getStationListUrl() {
        return http + "://" + serverIP + URLGetStationList;
    }

    public static String getURLGetCityPicture() {
        return http + "://" + serverIP + URLGetCityPicture + "/";
    }

    public static String getCurrentCityInfoUrl() {
        return http + "://" + serverIP + URLGetCurrentCityInfo;
    }

    public static String getMovieImagePath(String relativePath) {
        return http + "://" + serverIP + URLGetMovieImagePath + relativePath;
    }

    public static String getaFoodImagePath(String relativePath) {
        return http + "://" + serverIP + URLGetFoodImagePath + relativePath;
    }

    public static String getMoviePath(String relativePath) {
        return http + "://" + serverIP + URLGetMoviePath + relativePath;
    }

    public static String getUrlAllIntroduce() {
        return http + "://" + serverIP + UrlGetAllIntroduce;
    }

    public static String getSubmitOrderInfo() {
        return http + "://" + serverIP + URLGetSubmitOrderInfo + "?user_id=" + TrainTravel.user_id;
    }
    public static String getConfirmOrderInfo(String order_id ,String order_detail_id) {
        return http + "://" + serverIP + URLGetConfimeOrderInfo
                + "?order_id=" +order_id+"&order_detail_id="+order_detail_id;
    }

    public static String getMovieListUrl() {
        return http + "://" + serverIP + URLGetMovieList;
    }

    public static String getFoodListUrl() {
        return http + "://" + serverIP + URLGetFoodList+"?comm_ty=1";
    }
    public static String getComListListUrl() {
        return http + "://" + serverIP + URLGetFoodList+"?comm_ty=2";
    }

    public static String URLGetSuperPlayerApk() {
        return http + "://" + serverIP + URLGetSuperPlayerApk;
    }

    public static LiteHttp getLiteHttp(Context context) {
        List<NameValuePair> headers = new ArrayList<NameValuePair>();
        headers.add(new NameValuePair("Accept", "text/html,application/xhtml+xml," +
                "application/xml;q=0.9,*/*;q=0.8"));
        headers.add(new NameValuePair("User-Agent", "Mozilla / 5.0 (Windows NT 6.3;" +
                "WOW64)AppleWebKit / 537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 " +
                "Safari/537.36 SE 2.X MetaSr 1.0"));
        headers.add(new NameValuePair("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"));
        headers.add(new NameValuePair("Accept-Language", "zh-CN"));
        headers.add(new NameValuePair("Charset", "UTF-8"));
        headers.add(new NameValuePair("Connection", "Keep-Alive"));

        HttpConfig config = new HttpConfig(context) // configuration quickly
                .setDebugged(true)                   // log output when debugged
                .setDetectNetwork(true)              // detect network before connect
                .setDoStatistics(true);
        config.setCommonHeaders(headers);
        // connect and socket timeout: 10s
        return LiteHttp.newApacheHttpClient(config);

    }


}
