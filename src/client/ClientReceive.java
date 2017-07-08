package client;

/**
 * Created by youzhou on 6/25/17.
 */

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientReceive implements Runnable {
    private Socket s;

    public ClientReceive(Socket s) {
        this.s = s;
    }

    public void run() {
        try {
            BufferedReader brIn = new BufferedReader(new InputStreamReader(s.getInputStream()));
            while (true) {
                String s = brIn.readLine();
                String[] strs = s.split("\\.");
                String info = strs[0];     //judge the kind of info
                String name = "", line = "";
                if (strs.length == 2)
                    line = strs[1];
                else if (strs.length == 3) {
                    line = strs[1];
                    name = strs[2];
                }

                if (info.equals("1")) {  // 1 for message
                    ClientGUI.textMessage.append(line + "\r\n");
                    ClientGUI.textMessage.setCaretPosition(ClientGUI.textMessage.getText().length());
                } else if (info.equals("2") || info.equals("3")) { // 2 for join and 3 for exit
                    if (info.equals("2")) {
                        ClientGUI.textMessage.append("(Attention) " + name + " has connected!" + "\r\n");
                        ClientGUI.textMessage.setCaretPosition(ClientGUI.textMessage.getText().length());
                    } else {
                        ClientGUI.textMessage.append("(Attention) " + name + " has exited!" + "\r\n");
                        ClientGUI.textMessage.setCaretPosition(ClientGUI.textMessage.getText().length());
                    }
                    String list = line.substring(1, line.length() - 1);
                    String[] data = list.split(",");
                    /*for (String ss : data)
                        if (ss.charAt(0) == ' ')
                            ss = ss.substring(1);*/
                    ClientGUI.user.clearSelection();
                    ClientGUI.user.setListData(data);
                } else if (info.equals("4")) {  // 4 to return warns
                    ClientGUI.connect.setText("connect");
                    ClientGUI.exit.setText("exit");
                    ClientGUI.socket.close();
                    ClientGUI.socket = null;
                    JOptionPane.showMessageDialog(ClientGUI.window, "Someone has used the nickname already");
                    break;
                } else if (info.equals("5")) {   // 5 to close server
                    ClientGUI.connect.setText("connect");
                    ClientGUI.exit.setText("exited");
                    ClientGUI.socket.close();
                    ClientGUI.socket = null;
                    break;
                } else if (info.equals("6")) {  // 6 for whisper message
                    ClientGUI.textMessage.append("(Whisper) " + line + "\r\n");
                    ClientGUI.textMessage.setCaretPosition(ClientGUI.textMessage.getText().length());
                } else if (info.equals("7")) {
                    JOptionPane.showMessageDialog(ClientGUI.window, "The name you type is not online");
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(ClientGUI.window, "The client has closed the connection");
        }
    }
}
