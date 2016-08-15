package cn.trainservice.trainservice;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import cn.trainservice.trainservice.service.Chat.ChatFragment;
import cn.trainservice.trainservice.service.Chat.ChatServer;
import cn.trainservice.trainservice.service.Chat.UdpBroadCast;

public class NotifyService extends Service {


    private static final String BROADCAST_IP = "230.0.0.1";
    private int mPort = 8888;

    public NotifyService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        listenUDPServer.start();
        UdpBroadCast.start();
        chatServer.startServer();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void createNotify(String title, String nextStation) {

        NotificationManager nm = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setContentTitle(title)
                .setContentText(nextStation)
                .setSmallIcon(R.mipmap.ic_launcher).setContentIntent(pi)
                .build();
        notification.flags = Notification.FLAG_NO_CLEAR;
        nm.notify(0, notification);

    }

    public void recv() {
        InetAddress inetRemoteAddr = null;
        try {
            inetRemoteAddr = InetAddress.getByName(BROADCAST_IP);
        } catch (UnknownHostException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }

        DatagramPacket recvPack = new DatagramPacket(new byte[1024], 1024);

        MulticastSocket server = null;
        try {
            server = new MulticastSocket(8888);
        } catch (IOException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }
            /*
	         * 如果是发送数据报包,可以不加入多播组; 如果是接收数据报包,必须加入多播组; 这里是接收数据报包,所以必须加入多播组;
	         */
        try {
            server.joinGroup(inetRemoteAddr);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        while (true) {
            try {
                server.receive(recvPack);

                String str = new String (recvPack.getData() , 0 ,recvPack.getLength(),"UTF-8");

                Log.d("data1",str);
                if (str.startsWith("cmd:1")) {
                    int start  = str.indexOf("--");
                    int end = str.indexOf(",");
                    String user_id = str.substring(start + 2, end);
                    String user_name = str.substring(end + 1);
                   String ip = recvPack.getAddress().getHostAddress();
                    Log.d("data1","IP" +ip );

                    Intent notifyIntent = new Intent();

                    notifyIntent.setAction(TrainServiceApplication.ChatDiscoverBroadcastAction);
                    notifyIntent.putExtra("user_id", user_id);

                    notifyIntent.putExtra("user_name", user_name);
                    notifyIntent.putExtra("ip", ip);
                    //createNotify("Here is " + currentStation + " Railway Station", "Next：" + nextStation);
                    sendBroadcast(notifyIntent);


                } else {
                    JSONObject js = new JSONObject(str);
                    String currentStation = js.getString("到站");
                    String nextStation = js.getString("下站");
                    int currentStation_id = js.getInt("站点");
                    int stopTime = js.getInt("停车");

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Intent notifyIntent = new Intent();
                    notifyIntent.setAction(TrainServiceApplication.JourneyBroadcastAction);
                    notifyIntent.putExtra("stationId", currentStation_id);

                    notifyIntent.putExtra("stationName", currentStation);
                    notifyIntent.putExtra("nextStation", nextStation);
                    createNotify("Here is " + currentStation + " Railway Station", "Next：" + nextStation);
                    sendBroadcast(notifyIntent);
                }

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (Exception E) {
                E.printStackTrace();
            }


        }

    }

    private ChatServer chatServer =  new ChatServer();
    private UdpBroadCast UdpBroadCast = new UdpBroadCast();
    private Thread listenUDPServer = new Thread(new Runnable() {
        @Override
        public void run() {
            recv();
        }
    });

}
