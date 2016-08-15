package cn.trainservice.trainservice.service.live;

/**
 * Created by BrainWang on 2016/3/21.
 */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

import cn.trainservice.trainservice.service.live.handleReceiveData;

/**
 * 基于UDP协议的服务器端，对来自客户端的数据包进行应答
 *
 * @author <a href="http://wangbaiyuan">王柏元</a>
 */
public class UDPServer {
    /**
     * 端口
     */
    int port = 1888;
    private static String BROADCAST_IP = "230.0.0.1";
    private final InetAddress inetRemoteAddr;
    MulticastSocket socket;

    public handleReceiveData callback;
    private boolean run=true;

    public UDPServer(int port) throws SocketException, IOException {

        inetRemoteAddr = InetAddress.getByName(BROADCAST_IP);
        this.port = port;
        socket = new MulticastSocket(port);
        socket.joinGroup(inetRemoteAddr);
        // System.out.println("服务器启动。");
    }

    public void setReceiveCallback(handleReceiveData call) {
        callback = call;
    }

    public void service() throws IOException {
        while (run) {
            DatagramPacket dp = new DatagramPacket(new byte[102400], 102400);

            socket.receive(dp); //接收客户端信息

            byte[] data = dp.getData();
            callback.handleReceive(data);
        }
    }

    public void start() throws SocketException, IOException {
        service();
    }

    public void stop()  {
        run=false;
    }

    public void sendMsg(final byte[] data) {

        Thread send = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (socket != null) {

                        // SocketAddress socketAddres = new InetSocketAddress("192.168.1.110", 8804);
                        socket.send(new DatagramPacket(data, data.length, inetRemoteAddr, port));
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        send.start();
    }


}