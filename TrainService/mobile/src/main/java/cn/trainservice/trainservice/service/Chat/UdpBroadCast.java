package cn.trainservice.trainservice.service.Chat;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import cn.trainservice.trainservice.TrainTravel;

/**
 * Created by 旭 on 2016/4/16.
 */
public class UdpBroadCast extends Thread {
    private static final String BROADCAST_IP = "230.0.0.1";
    private static final int PORT = 8888;

    public void run() {
        while (true) {
            try {
                InetAddress inetRemoteAddr = InetAddress
                        .getByName(BROADCAST_IP);
                MulticastSocket socket = new MulticastSocket();
                socket.joinGroup(inetRemoteAddr);
                if (TrainTravel.islogin) {
                    String msg = "cmd:1--" + TrainTravel.user_id  + "," + TrainTravel.user_name;
                    Log.d("data1", "send" + msg);
                    DatagramPacket packet = new DatagramPacket(msg.getBytes("UTF-8"), 0, msg.getBytes("UTF-8").length, inetRemoteAddr, PORT);
                    //socket.setBroadcast(true);
                    socket.send(packet);
                }

                Thread.sleep(10 * 1000);

            }  catch (InterruptedException e) {
                e.printStackTrace();
            }catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
}
