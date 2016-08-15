package cn.trainservice.trainservice.service.Chat;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import cn.trainservice.trainservice.TrainTravel;

/**
 * Created by 旭 on 2016/4/16.
 */

/*
*
* 一个实现线程的socket ，用来发送消息
*
* */
public class ClientThread extends Thread {
    private String ip ;
    private static int PORT = 12345;
    private MessageManager.MyMessage msg;

    public ClientThread(String ip ,MessageManager.MyMessage msg) {
        this.ip = ip;
        this.msg = msg;
    }
    private String string_encode(){
        String str = null;
        JSONObject  json = null;
        try{
            json = new JSONObject();
            json.put("user_id", TrainTravel.user_id);
            json.put("info",this.msg.info);
            json.put("m_type",this.msg.m_type);
            Log.d("data1","msg : "+json.toString());
            return json.toString();
        }catch (JSONException jn){
            jn.printStackTrace();
        }

        return str;
    }
    public void run() {
        DataOutputStream wr = null;
        Socket socket = null;
        try {
            socket = new Socket(ip, PORT);
            wr = new DataOutputStream(socket.getOutputStream());
            wr.write(string_encode().getBytes());
            wr.close();
            socket.close();
        } catch(NullPointerException ne){
            ne.printStackTrace();
        }catch (UnknownHostException une) {
            une.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }


    }
}
