package cn.trainservice.trainservice.journey.model;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.beardedhen.androidbootstrap.AwesomeTextView;

import java.util.ArrayList;
import java.util.List;

import cn.trainservice.trainservice.R;

/**
 * Created by BrainWang on 2016/4/8.
 */

public class StationlistRecyclerViewAdapter extends RecyclerView.Adapter<StationlistRecyclerViewAdapter.ViewHolder> {

    private List<TrainStation> mValues;
    private Context mcontext;
    private int currentStationId=-1;
    public StationlistRecyclerViewAdapter(List<TrainStation> stations, Context context) {
        mValues = stations;
        mcontext=context;
    }

    public void loadStation( List<TrainStation> values){
        mValues=values;
    }

    @Override
    public StationlistRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.journey_station_item, parent, false);
        return new ViewHolder(view, mcontext);
    }

    @Override
    public void onBindViewHolder(StationlistRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.tv_station_index.setText(position+1+"");
        holder.tv_station_name.setText(mValues.get(position).mName);
        holder.tv_arrivalTime.setText(mValues.get(position).mArrivalTime);
        holder.tv_time_stay.setText(mValues.get(position).mTimeStay+mcontext.getString(R.string.minute));
        if(currentStationId==mValues.get(position).mId){
            holder.mView.setBackgroundColor(Color.LTGRAY);
            holder.tv_station_index.setTextColor(mcontext.getResources().getColor(R.color.colorPrimary));
            holder.tv_station_name.setTextColor(mcontext.getResources().getColor(R.color.colorPrimary));
            holder.tv_arrivalTime.setTextColor(mcontext.getResources().getColor(R.color.colorPrimary));
            holder.tv_time_stay.setTextColor(mcontext.getResources().getColor(R.color.colorPrimary));
            holder.tv_station_mark.setVisibility(View.VISIBLE);
            holder.tv_station_mark.setFontAwesomeIcon("fa_hand_o_right");
        }else{
            holder.mView.setBackgroundColor(Color.WHITE);
            holder.tv_station_index.setTextColor(mcontext.getResources().getColor(R.color.stationItem));
            holder.tv_station_name.setTextColor(mcontext.getResources().getColor(R.color.stationItem));
            holder.tv_arrivalTime.setTextColor(mcontext.getResources().getColor(R.color.stationItem));
            holder.tv_time_stay.setTextColor(mcontext.getResources().getColor(R.color.stationItem));
            //holder.tv_station_mark.set
            holder.tv_station_mark.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    public void clearStations() {
        mValues.clear();
        notifyDataSetChanged();
    }

        public void setMarkAtCity(int cityId){
        currentStationId=cityId;
        notifyDataSetChanged();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView  tv_station_index;
        public final AwesomeTextView tv_station_mark;
        public final TextView tv_arrivalTime;
        public final TextView tv_time_stay;
        public final TextView tv_station_name;
        public ViewHolder(View view, Context mcontext) {
            super(view);
            mView = view;
            tv_station_index= (TextView) mView.findViewById(R.id.tv_station_index);
            tv_station_mark= (AwesomeTextView) mView.findViewById(R.id.tv_current_station_mark);
            tv_arrivalTime= (TextView) mView.findViewById(R.id.tv_station_arrivalTime);
            tv_time_stay= (TextView) mView.findViewById(R.id.tv_station_timeStay);
            tv_station_name= (TextView) mView.findViewById(R.id.tv_station_name);

        }
    }
}
