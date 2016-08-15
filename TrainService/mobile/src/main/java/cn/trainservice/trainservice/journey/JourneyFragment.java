package cn.trainservice.trainservice.journey;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.litesuits.http.exception.HttpException;
import com.litesuits.http.listener.HttpListener;
import com.litesuits.http.request.StringRequest;
import com.litesuits.http.response.Response;

import org.json.JSONException;
import org.json.JSONObject;

import cn.trainservice.trainservice.R;
import cn.trainservice.trainservice.TrainServiceApplication;
import cn.trainservice.trainservice.journey.model.CityInfo;
import cn.trainservice.trainservice.journey.model.StationlistRecyclerViewAdapter;
import cn.trainservice.trainservice.journey.model.TrainStation;
import cn.trainservice.trainservice.journey.util.JsonHelper;
import cn.trainservice.trainservice.journey.view.TicketInfo;
import cn.trainservice.trainservice.TrainTravel;
import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageLoader;
import in.srain.cube.image.ImageLoaderFactory;
import in.srain.cube.image.impl.DefaultImageLoadHandler;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link JourneyFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link JourneyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JourneyFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextToSpeech speaker;
    private OnFragmentInteractionListener mListener;
    private ImageLoader imageLoader;
    private TicketInfo ticket;
    private int currentCityId = -1;
    private View view;
    private JourneyBroadcastReceiver receiver;
    private SwipeRefreshLayout swipeRefreshLayout;
    private StationlistRecyclerViewAdapter adapter;
    private CubeImageView thumb_city_brief;
    private CardView cardCurrentCity;
    private TextView tv_train_start;
    private TextView tv_train_end;
    private TextView tv_notices;
    private TextView tv_current_cityName;
    private TextView tv_city_introduce;
    private TextView tv_city_address;
    private TextView tv_station_stop;
    private TextView Train_textView;
    private TextView city_weather_info;
    private LinearLayout llayout;
    FrameLayout childview;

    public JourneyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment JourneyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static JourneyFragment newInstance(String param1, String param2) {
        JourneyFragment fragment = new JourneyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


        registerReceiver();


    }

    private void registerReceiver() {
        receiver = new JourneyBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(TrainServiceApplication.JourneyBroadcastAction);
        getActivity().registerReceiver(receiver, filter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_journey, container, false);
            llayout = (LinearLayout) view.findViewById(R.id.journeyLlayout);
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.stationlistView);
            adapter = new StationlistRecyclerViewAdapter(TrainTravel.stationslist, getContext());
            recyclerView.setAdapter(adapter);
            city_weather_info = (TextView) view.findViewById(R.id.city_weather_info);
            swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
            swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

                @Override
                public void onRefresh() {
                    refreshSections();

                }
            });

            if (TrainServiceApplication.getTicket() == null)
                TrainServiceApplication.setTickt(new TicketInfo(getContext()));
            ticket = TrainServiceApplication.getTicket();
            childview = ticket.getView();
            childview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TrainServiceApplication.attemptToEnterUserCenter(getActivity());
                }
            });

            llayout.addView(childview, 0);
            cardCurrentCity = (CardView) view.findViewById(R.id.card_current_city);


            cardCurrentCity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getActivity(), CityDetailActivity.class);
                    intent.putExtra("url", CityInfo.getCurrentCityInfo().mAllIntroduceUrl);
                    intent.putExtra("cityName", CityInfo.getCurrentCityInfo().mName);
                    intent.putExtra("imgUrl", CityInfo.getCurrentCityInfo().mImageUrl);
                    startActivity(intent);
                }
            });

            thumb_city_brief = (CubeImageView) view.findViewById(R.id.thumb_city_brief);
            DefaultImageLoadHandler handler = new DefaultImageLoadHandler(getActivity());
            handler.setLoadingResources(R.mipmap.loading);
            imageLoader = ImageLoaderFactory.create(getActivity(), handler);

            tv_train_start = (TextView) view.findViewById(R.id.tv_train_start);
            tv_train_end = (TextView) view.findViewById(R.id.tv_train_end);
            tv_notices = (TextView) view.findViewById(R.id.tv_notices);
            tv_station_stop = (TextView) view.findViewById(R.id.tv_station_stop);

            tv_current_cityName = (TextView) llayout.findViewById(R.id.tv_current_cityName);
            tv_city_introduce = (TextView) llayout.findViewById(R.id.tv_city_introduce);
            tv_city_address = (TextView) llayout.findViewById(R.id.tv_city_address);

        }
        if (speaker == null) {
            speaker = new TextToSpeech(getContext(), null);
        }


        refreshSections();
        return view;
    }

    public void onResume() {
        if((!TrainTravel.isload && TrainTravel.islogin) || TrainTravel.reset) {

            llayout.removeView(childview);
            if (TrainServiceApplication.getTicket() == null)
                TrainServiceApplication.setTickt(new TicketInfo(getContext()));
            ticket = TrainServiceApplication.getTicket();
            childview = ticket.getView();
            childview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TrainServiceApplication.attemptToEnterUserCenter(getActivity());
                }
            });

            llayout.addView(childview, 0);
            TrainTravel.reset= false;
            refreshSections();
        }

        super.onResume();

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void updateChildView(boolean isload) {
         try{
             ((TextView) childview.findViewById(R.id.startTime)).setText(TrainTravel.SatrtTime);
             ((TextView) childview.findViewById(R.id.arrivetime)).setText(TrainTravel.ArriveTime);
             ((TextView) childview.findViewById(R.id.tv_stations_less)).setText(TrainTravel.ThoughtTime);
             ((TextView) childview.findViewById(R.id.Train_textView)).setText("NO. " + TrainTravel.train_id);

         }catch (NullPointerException noe){
             noe.printStackTrace();
         }

    }

    /**
     * 刷新站点列表、城市介绍板块
     */

    private void refreshStations() {
        swipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });
        new Thread(new Runnable() {

            //请求站点列表信息
            @Override
            public void run() {
                String url = TrainServiceApplication.getStationListUrl();
                Log.d("data2", TrainTravel.train_id);
                String params = "?train_id=" + TrainTravel.train_id;
                url += params;
                TrainServiceApplication.getLiteHttp(getContext()).execute(new StringRequest(url).setHttpListener(
                        new HttpListener<String>() {
                            @Override
                            public void onSuccess(String data, Response<String> response) {

                                try {
                                    JSONObject Json = new JSONObject(data);
                                    boolean result = Json.getBoolean("result");
                                    if (result) {
                                        TrainTravel.stationslist = JsonHelper.parseStationList(data);
                                        tv_station_stop.setText(TrainTravel.stationslist.size() + "stops");
                                        tv_train_start.setText(TrainTravel.stationslist.get(0).mName);
                                        tv_train_end.setText(TrainTravel.stationslist.get(TrainTravel.stationslist.size() - 1).mName);


                                    } else {
                                        tv_station_stop.setText( "共-站");
                                        tv_train_start.setText("  ");
                                        tv_train_end.setText("  ");

                                    }

                                    adapter.loadStation(TrainTravel.stationslist);
                                    adapter.notifyDataSetChanged();
                                    TrainTravel.updateTrainTravel(result);
                                    updateChildView(result);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                              ;


                            }

                            @Override
                            public void onFailure(HttpException e, Response<String> response) {
                                Log.i("httpp", e.toString());
                            }
                        }
                ));
            }
        }).start();

        swipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void refreshSections() {

        refreshStations();
        refreshCurrentcity();

    }


    private void refreshCurrentcity() {
        swipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });

        new Thread(new Runnable() {
            //请求站点列表信息
            @Override
            public void run() {

                String url = TrainServiceApplication.getCurrentCityInfoUrl();
                String params = "?station=" + TrainTravel.current_station;
                url += params;
                TrainServiceApplication.getLiteHttp(getContext()).execute(new StringRequest(url).setHttpListener(
                        new HttpListener<String>() {


                            @Override
                            public void onSuccess(String data, Response<String> response) {
                                Log.d("data2", data);
                                try {


                                    JSONObject jso = new JSONObject(data);
                                    if (jso.getBoolean("result")) {
                                        CityInfo.loadCurrentCityData(jso.getString("info"));//填充当前城市信息
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                city_weather_info.setText(CityInfo.getCurrentCityInfo().getWeatherInfo());
                                currentCityId = CityInfo.getCurrentCityInfo().mId;
                                adapter.setMarkAtCity(currentCityId);
                                final String mName = CityInfo.getCurrentCityInfo().mName;
                                final String mImageUrl = CityInfo.getCurrentCityInfo().mImageUrl;
                                //////////////异步加载图片///////////

                                thumb_city_brief.loadImage(imageLoader, mImageUrl);
                                tv_current_cityName.setText(CityInfo.getCurrentCityInfo().mName);
                                tv_city_introduce.setText(CityInfo.getCurrentCityInfo().mIntroduce);
                                tv_city_address.setText(CityInfo.getCurrentCityInfo().mAddress);

                                int currentIndex = -1;
                                int destIndex = -1;

                                int i = 0;
                                for (TrainStation trainStation : TrainTravel.stationslist) {
                                    if (trainStation.mId == CityInfo.getCurrentCityInfo().mId) {
                                        currentIndex = i;

                                    }
                                    if (trainStation.mId == TrainTravel.EndStationid_id) {
                                        destIndex = i;

                                    }
                                    i++;
                                }


                                int less = destIndex - currentIndex;


                                boolean g = (currentIndex > 0 && less > -1);
                                if (g && less == 0) {
                                    notifyReach();
                                }

                                String noticeHtml = "您从 <font color=\"red\"><b>" + TrainTravel.StartStation
                                        + "</b> </font>站," +
                                        " 到达 <font color=\"red\"><b>" + TrainTravel.EndStation
                                        + " </b></font>站的旅途.本站 <font color=\"red\"><b>"
                                        + CityInfo.getCurrentCityInfo().mName + "</b></font> 站"
                                        + (g ? ",还有 <font color=\"red\"><b>" + less + "</b></font> 站到达目的地" : "");
                                tv_notices.setText(Html.fromHtml(noticeHtml));
                                // cardCurrentCity.setEnabled(true);
                                cardCurrentCity.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        Intent intent = new Intent(getActivity(), CityDetailActivity.class);
                                        intent.putExtra("url", CityInfo.getCurrentCityInfo().mAllIntroduceUrl);
                                        intent.putExtra("cityName", mName);
                                        intent.putExtra("imgUrl", mImageUrl);
                                        startActivity(intent);
                                    }
                                });

                            }

                            @Override
                            public void onFailure(HttpException e, Response<String> response) {
                                Log.i("httpp", e.toString());
                            }
                        }
                ));
            }
        }).start();
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(receiver);
        speaker.shutdown();
        super.onDestroy();
    }


    private void notifyReach() {

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        final Ringtone r = RingtoneManager.getRingtone(getActivity(), notification);
        r.play();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //    设置Title的图标
        builder.setIcon(R.mipmap.ic_launcher);
        //    设置Title的内容
        builder.setTitle("Your Destination is Arrived!");
        //    设置Content来显示一个信息
        builder.setMessage("Your Destination is Arrived!pLease get off the train");
        //    设置一个PositiveButton
        builder.setPositiveButton("I got it", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                r.stop();
            }
        });
        builder.show();
    }

    //private
    class JourneyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int stationId = intent.getIntExtra("stationId", -1);
            String stationName = intent.getStringExtra("stationName");
            String nextStation = intent.getStringExtra("nextStation");

            if (currentCityId != stationId
                    && stationId != TrainTravel.stationslist.get(TrainTravel.stationslist.size() - 1).mId) {

                speaker.speak("Dear passengers,Here is " + stationName + "  Railway Station , and the next Station is " + nextStation, TextToSpeech.QUEUE_ADD, null);

            }
            refreshSections();
            currentCityId = stationId;
        }
    }


}
