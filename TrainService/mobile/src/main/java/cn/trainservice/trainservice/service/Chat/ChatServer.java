package cn.trainservice.trainservice.service.Chat;
import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by 旭 on 2016/4/16.
 */

/*
*   socketServer
 *
  * */
public class ChatServer {
    private ServerSocket server;
    private static int PORT = 12345;
    private static int MAX_BUFFER = 2048;
    private SocketServer socketserver = null;
    private MessageManager mag = MessageManager.getMessageManager();

    public  ChatServer() {///直接 new 个对象 然后开启 startServer
        init();
    }

    public void init() {
        try {
            server = new ServerSocket(PORT);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    public void startServer(){///开启socket server 服务
        if(socketserver == null){
            socketserver = new SocketServer();
            socketserver.start();
        }else{
            if(!socketserver.isAlive())
                socketserver.start();
        }
    }


    class SocketServer extends Thread {
        public void run() {
            try {
                Socket socket = null;
                while ((socket = server.accept()) != null) {
                    DataInputStream wr = new DataInputStream(socket.getInputStream());
                    byte[] data = new byte[MAX_BUFFER];

                    int re = 0;

                    StringBuilder msg = new StringBuilder();

                    while ((re = wr.read(data)) > 0) {
                        msg.append(new String(data, 0, re));
                        data =null;
                        data = new byte[MAX_BUFFER];
                    }
                    Log.d("data1","server"+msg.toString());
                    mag.addMsg(msg.toString());
                    wr.close();
                    socket.close();
                }
            } catch (NullPointerException ne) {
                ne.printStackTrace();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

        }
    }
}
