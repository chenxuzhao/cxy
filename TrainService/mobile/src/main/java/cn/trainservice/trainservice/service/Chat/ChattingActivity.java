package cn.trainservice.trainservice.service.Chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.trainservice.trainservice.R;
import cn.trainservice.trainservice.TrainTravel;
import cn.trainservice.trainservice.view.IconTextView;

public class ChattingActivity extends AppCompatActivity implements MessageManager.MessageArriverListener {


    private Button send;
    private EditText editText;
    private ScrollView scrollView;
    private static String user_id;
    private static String ip;
    private Toolbar toolbar;
    private TextView chat_title;
    private List<MessageManager.MyMessage> lists = new ArrayList<>();
    private LinearLayout content;
    private MessageManager mag = MessageManager.getMessageManager();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);
        Intent intent = getIntent();
        user_id = intent.getStringExtra("user_id");

        Log.d("data1", "user_id" + user_id);
        chat_title = (TextView) findViewById(R.id.chat_title);
        chat_title.setText(intent.getStringExtra("name"));
        ip = intent.getStringExtra("ip");


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        content = (LinearLayout) findViewById(R.id.content);
        scrollView = (ScrollView) findViewById(R.id.chat_cont);
        send = (Button) findViewById(R.id.send_button);

        IconTextView send_changeSeat=(IconTextView)findViewById(R.id.send_changeSeat);

        editText = (EditText) findViewById(R.id.input);
        initList();
        toolbar = (Toolbar) findViewById(R.id.toolbar);


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editText.getText().toString();
                if (!text.equals("")) {

                    if (ChatFragment.ip_list.containsKey(user_id))
                        ChatFragment.ip_list.get(user_id).last_msg = text;
                    MessageManager msgManager = new MessageManager();
                    Log.d("data1","text :"+text);
                    MessageManager.MyMessage msg =new MessageManager.MyMessage(TrainTravel.user_id, 1, text, 0);
                    new ClientThread(ip, msg).start();
                    editText.setText("");
                    content.addView(new MesView(msg).getMsgView());
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);

                }
            }
        });
        send_changeSeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageManager.MyMessage msg = new MessageManager.MyMessage(TrainTravel.user_id ,1, "", 1);
                content.addView(new MesView(msg).getMsgView());
            }
        });
    }

    public void initList() {
        List<MessageManager.MyMessage> list = mag.getUserMsg(user_id);
        for (int i = 0, size = list.size(); i < size; i++) {
            lists.add(list.get(i));
        }

        for (int i = 0, len = lists.size(); i < len; i++) {
            content.addView(new MesView(lists.get(i)).getMsgView());

        }
        scrollView.fullScroll(ScrollView.FOCUS_DOWN);

    }

    @Override
    public void onResume() {

        mag.registerListener(user_id, this);
        super.onResume();
    }

    @Override
    public void onStop() {
        mag.unregisterListener(user_id);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        mag.unregisterListener(user_id);
        super.onDestroy();
    }

    @Override
    public void onPause() {
        mag.unregisterListener(user_id);
        super.onPause();
    }


    @Override
    public boolean messageReach(MessageManager.MyMessage msg) {

        if ((msg.user_id).equals(ChattingActivity.this.user_id)) {
            ChattingActivity.this.runOnUiThread(new upDateUi(msg));
            ChatFragment.ip_list.get(user_id).last_msg = msg.info;
            return true;
        } else {
            return false;
        }
    }


    class upDateUi implements Runnable {
        private MessageManager.MyMessage msg;

        public upDateUi(MessageManager.MyMessage msg) {
            this.msg = msg;
        }

        @Override
        public void run() {
            ChattingActivity.this.content.addView(new MesView(msg).getMsgView());
            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
    }


    class MesView {

        public TextView left_tv;
        public TextView right_tv;
        public cyimageview left_image;
        public cyimageview right_image;
        public RelativeLayout left_view;
        public RelativeLayout right_view;
        private MessageManager.MyMessage msg;

        public MesView(MessageManager.MyMessage msg) {
            this.msg = msg;


        }

        public View getMsgView() {
            View view=null;
            switch(msg.m_type){
                case 0:
                    view = LayoutInflater.from(ChattingActivity.this).inflate(R.layout.chat_content_view, null);
                    left_view = (RelativeLayout) view.findViewById(R.id.left_view);
                    right_view = (RelativeLayout) view.findViewById(R.id.right_view);
                    this.left_tv = (TextView) view.findViewById(R.id.left_content);
                    this.left_image = (cyimageview) view.findViewById(R.id.left_image);
                    this.right_tv = (TextView) view.findViewById(R.id.right_content);
                    this.right_image = (cyimageview) view.findViewById(R.id.right_image);

                    if (msg.m_from == 0) {
                        left_tv.setText(msg.info);
                        right_image.setVisibility(View.INVISIBLE);
                        right_tv.setVisibility(View.INVISIBLE);
                        right_view.setVisibility(View.INVISIBLE);
                    } else {
                        right_tv.setText(msg.info);
                        left_image.setVisibility(View.INVISIBLE);
                        left_tv.setVisibility(View.INVISIBLE);
                        left_view.setVisibility(View.INVISIBLE);
                    }
                    break;
                case 1:
                    view = LayoutInflater.from(ChattingActivity.this).inflate(R.layout.chat_change_seat_view, null);
                    break;
            }

            return view;
        }
    }


}