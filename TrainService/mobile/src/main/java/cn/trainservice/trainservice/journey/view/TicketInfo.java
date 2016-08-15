package cn.trainservice.trainservice.journey.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapLabel;

import cn.trainservice.trainservice.R;
import cn.trainservice.trainservice.TrainServiceApplication;
import cn.trainservice.trainservice.TrainTravel;

/**
 * Created by BrainWang on 2016/4/6.
 */
public class TicketInfo {
    public Context context;


    public TicketInfo(Context context) {
        this.context = context;
    }

    public TicketInfo(Context context, String ticketName, String userName, String ID, String from, String To) {
        this.context = context;
    }

    public FrameLayout getView() {
        FrameLayout view = (FrameLayout) LayoutInflater.from(context).inflate(R.layout.journey_ticket_info, null);
        FrameLayout containerview = (FrameLayout) view.findViewById(R.id.ticket_info_person);
        FrameLayout childview;
        if (TrainTravel.islogin ){
            childview = (FrameLayout) LayoutInflater.from(context).inflate(R.layout.journey_ticket_info_login, null);
            TextView tv_ticket_userName = (TextView) childview.findViewById(R.id.tv_ticket_userName);
            TextView tv_ticket_ID = (TextView) childview.findViewById(R.id.tv_ticket_ID);
            TextView tv_ticket_start = (TextView) childview.findViewById(R.id.tv_ticket_start);
            TextView tv_ticket_end = (TextView) childview.findViewById(R.id.tv_ticket_end);
            TextView tv_stations_less = (TextView) childview.findViewById(R.id.tv_stations_less);
            TextView tv_startTime = (TextView) childview.findViewById(R.id.startTime);
            TextView tv_arriveTime = (TextView) childview.findViewById(R.id.arrivetime);
            BootstrapLabel tv_seat_user_type = (BootstrapLabel) childview.findViewById(R.id.tv_seat_user_type);
            tv_seat_user_type.setText(TrainTravel.get_ticket_type());
            BootstrapLabel tv_seat_type = (BootstrapLabel) childview.findViewById(R.id.tv_seat_type);
            tv_seat_type.setText(TrainTravel.get_seatType());
            tv_ticket_userName.setText(TrainTravel.user_name);
            tv_ticket_ID.setText(String.format("%s****%s", TrainTravel.user_id.substring(0, 10), TrainTravel.user_id.substring(14, 18)));
            tv_ticket_start.setText(TrainTravel.StartStation);
            tv_ticket_end.setText(TrainTravel.EndStation);

        } else {
            childview = (FrameLayout) LayoutInflater.from(context).inflate(R.layout.journey_ticket_info_notlogin, null);
        }
        containerview.removeAllViews();
        containerview.addView(childview);

        //view.setLayoutParams(new ViewGroup.LayoutParams());
        return view;
    }


}
