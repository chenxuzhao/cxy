package cn.trainservice.trainservice.journey.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import cn.trainservice.trainservice.R;

/**
 * Created by BrainWang on 2016/4/6.
 */
public class StationList {
    private Context context;
    public String userName="",ID="", fromTo="";

    public StationList(Context context, String userName, String ID, String fromTo){
        this.context=context;
        this.userName=userName;
        this.ID=ID;
        this.fromTo=fromTo;
    }
    public FrameLayout getView (){
        FrameLayout view= (FrameLayout) LayoutInflater.from(context).inflate(R.layout.journey_station_list,null);


        TextView tv_ticket_userName=(TextView)view.findViewById(R.id.tv_ticket_userName);
        TextView tv_ticket_ID=(TextView)view.findViewById(R.id.tv_ticket_ID);
        //TextView tv_ticket_FromTo=(TextView)view.findViewById(R.id.tv_ticket_FromTo);

        tv_ticket_userName.setText(userName);
        tv_ticket_ID.setText(ID);
       // tv_ticket_FromTo.setText(fromTo);

        //view.setLayoutParams(new ViewGroup.LayoutParams());
        return view;
    }


}
