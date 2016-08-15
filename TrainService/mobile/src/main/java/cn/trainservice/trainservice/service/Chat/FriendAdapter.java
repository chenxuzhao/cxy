package cn.trainservice.trainservice.service.Chat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;
import cn.trainservice.trainservice.R;
import cn.trainservice.trainservice.service.movie.MovieDetailActivity;

/**
 * Created by cx on 2016/4/16.
 */
public class FriendAdapter extends RecyclerView.Adapter {
    private Context context ;
    private List<Friend> lists ;

    public FriendAdapter(Context context ,List<Friend> lists){

        this.context =context;
        this.lists = lists;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(this.context).inflate(R.layout.friend_iterm, parent,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        MyViewHolder mholder = (MyViewHolder) holder;
        mholder.tv.setText(lists.get(position).name);
        mholder.msg_tv.setText(lists.get(position).last_msg);

    }

    @Override
    public int getItemCount() {
        return lists.size();
    }
    public void loadList( List<Friend> values){
        lists=values;
    }
    class MyViewHolder extends RecyclerView.ViewHolder
    {
        public  View view;
        public   TextView tv;
        public   TextView msg_tv;
        public MyViewHolder(View view)
        {

            super(view);
            this.view = view;
            this.msg_tv= (TextView)  view .findViewById(R.id.last_massage);
            this.tv = (TextView) view.findViewById(R.id.friend_iterm);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                            Friend friend = lists.get(getPosition());
                    Log.d("data1","index :"+getPosition() );

                    Log.d("data1",friend.name );
                            Context context = v.getContext();
                            Intent intent = new Intent(context, ChattingActivity.class);
                            intent.putExtra("user_id",friend.user_id);
                            intent.putExtra("name",friend.name);
                    Log.d("data1",friend.ip);
                    intent.putExtra("ip",friend.ip);
                            context.startActivity(intent);
                }
            });
        }


    }
    /**
     * Created by chenxu on 2016/4/16.
     */
    public static class Friend {
        public String user_id;
        public String name;
        public String ip;
        public String last_msg ;
        public boolean TimeOut = true;
        public Friend(){

        }
        public Friend(String user_id , String name ,String ip, boolean timeOut){
            this.user_id =user_id;
            this.name = name;
            this.ip =ip;
            this.TimeOut = timeOut;
        }
    }
}
