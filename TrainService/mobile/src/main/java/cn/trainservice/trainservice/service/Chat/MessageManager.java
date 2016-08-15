package cn.trainservice.trainservice.service.Chat;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by 旭 on 2016/4/16.
 */

public class MessageManager {
    public static List<FriendAdapter.Friend> lists;
    private  Map<String, ConcurrentLinkedQueue<MyMessage>> message;
    private  Map<String, String> userClient;
    private static MessageManager mag = null;

    public MessageManager() {
        message = new HashMap<>();
        userClient = new HashMap<>();
    }

    public static MessageManager getMessageManager() {
        if (mag == null)
            mag = new MessageManager();
        return mag;
    }


    public String getClient(String user_id) {
        return userClient.get(user_id);
    }

    public void addMsg(String msg) {
        String user_id = null;
        try {
            JSONObject json = new JSONObject(msg);
            user_id = json.getString("user_id");
            String info = json.getString("info");
            int m_type = json.getInt("m_type");
            MyMessage message = new MyMessage(user_id,0,info ,m_type);
                    addMsg(message);

        } catch (JSONException je) {
            je.printStackTrace();

        }
    }

    private void addMsg(MyMessage msg) {
       if(!messageArriverList.containsKey(msg.user_id))
       { Log.d("data1","no listener");
            if (message.get(msg.user_id) == null) {
                ConcurrentLinkedQueue<MyMessage> msgLists = new ConcurrentLinkedQueue<>();
                msgLists.add(msg);
                message.put(msg.user_id, msgLists);
            } else {
                message.get(msg.user_id).add(msg);
            }
        } else {
            Log.d("data1","listener");
           if( messageArriverList.get(msg.user_id).messageReach(msg )){
               Log.d("data1","listener error");
               if (message.get((msg.user_id)) == null){
                   ConcurrentLinkedQueue<MyMessage> msgLists = new ConcurrentLinkedQueue<>();
                   msgLists.add(msg);
                   message.put(msg.user_id, msgLists);
               } else {
                   message.get((msg.user_id)).add(msg);
               }
           }

        }

    }

    public List<MyMessage> getUserMsg(String user_id) {
        List<MyMessage> lists = new ArrayList<>();
        ConcurrentLinkedQueue<MyMessage> queue = message.get(user_id);
        MyMessage msg = null;
        if(queue!=null){
            while((msg=queue.poll())!=null)
            {
                lists.add(msg);
            }
        }

        return  lists;
    }

    public MyMessage getMsg(String user_id) {
        MyMessage msg = null;
        if (message.get(user_id) == null) {
            msg = null;
        } else {
            msg = message.get(user_id).poll();
        }
        return msg;
    }

/*
* activity 需要实现 MessageArriverListener ，用来接受呀返回的数据
* 还需要重载 activity 的 onPause() ,onStop(),onDestory
 * ,onStart ,onRuseme(),onCreate()
* 改变 MessageArriverListener.ListenserStatus
* 的状态值
*
*
* */
    interface MessageArriverListener {///需要实现的监听
        boolean ListenserStatus = false;
        boolean messageReach(MyMessage msg);
    }

    private Map<String, MessageArriverListener> messageArriverList = new HashMap<>();

    public void registerListener(String user_id, MessageArriverListener listenr) {
        messageArriverList.put(user_id, listenr);
    }

    public void unregisterListener(String user_id) {
        messageArriverList.remove(user_id);
    }


    static class MyMessage {

        public int m_from;
        public int m_type;
        public String info;
        public String user_id;

        public  MyMessage(String user_id, int m_from, String info, int m_type) {
            this.user_id = user_id;
            this.m_from = m_from;
            this.info = info;
            this.m_type = m_type;
        }
    }
}
