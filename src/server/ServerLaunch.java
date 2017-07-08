package server;

/**
 * Created by youzhou on 6/15/17.
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.Map;

import javax.swing.JOptionPane;

public class ServerLaunch implements Runnable {
    private int port;
    public static ArrayList<Socket> userList = null;
    public static Vector<String> userName = null;    // thread security
    public static Map<String, Socket> map = null;
    public static ServerSocket ss = null;
    public static boolean flag = true;

    public ServerLaunch(int port) throws IOException {
        this.port = port;
    }

    public void run() {
        Socket s = null;
        userList = new ArrayList<Socket>();   //clients ports container
        userName = new Vector<String>();      //clients' serverName container
        map = new HashMap<String, Socket>();   //name to socket one on one map

        System.out.println("server run!");

        try {
            ss = new ServerSocket(port);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        while (flag) {
            try {
                s = ss.accept();
                userList.add(s);
                new Thread(new ServerReceive(s, userList, userName, map)).start();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(ServerGUI.window, "server exited！");
            }
        }
    }
}
