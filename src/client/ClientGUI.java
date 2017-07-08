package client;

/**
 * Created by youzhou on 6/15/17.
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ClientGUI {
    public static JFrame window;
    public static JButton connect, exit;
    public static JTextArea textMessage;
    public static Socket socket = null;
    public static JList<String> user;

    JTextField nickName, port, message, whis;
    JButton whisper, send;

    //main method
    public static void main(String[] args) {
        new ClientGUI();
    }

    public ClientGUI() {
        init();
    }

    public void init() {
        window = new JFrame("client");
        window.setLayout(null);
        window.setBounds(200, 200, 500, 400);
        window.setResizable(false);

        JLabel label_nickName = new JLabel("nickname:");
        label_nickName.setBounds(10, 8, 70, 30);
        window.add(label_nickName);

        nickName = new JTextField();
        nickName.setBounds(90, 8, 80, 30);
        window.add(nickName);

        JLabel label_port = new JLabel("port:");
        label_port.setBounds(180, 8, 40, 30);
        window.add(label_port);

        port = new JTextField();
        port.setBounds(230, 8, 60, 30);
        window.add(port);

        connect = new JButton("connect");
        connect.setBounds(300, 8, 90, 30);
        window.add(connect);

        exit = new JButton("exit");
        exit.setBounds(400, 8, 90, 30);
        window.add(exit);

        textMessage = new JTextArea();
        textMessage.setBounds(10, 70, 340, 220);
        textMessage.setEditable(false);

        textMessage.setLineWrap(true);
        textMessage.setWrapStyleWord(true);

        JLabel label_text = new JLabel("Text message");
        label_text.setBounds(140, 40, 100, 30);
        window.add(label_text);

        JScrollPane paneText = new JScrollPane(textMessage);
        paneText.setBounds(10, 70, 340, 220);
        window.add(paneText);

        JLabel label_userlist = new JLabel("User list");
        label_userlist.setBounds(380, 40, 80, 30);
        window.add(label_userlist);

        user = new JList<String>();
        JScrollPane paneUser = new JScrollPane(user);
        paneUser.setBounds(355, 70, 120, 220);
        window.add(paneUser);

        message = new JTextField();
        message.setBounds(10, 300, 340, 50);
        message.setText(null);
        window.add(message);

        whis = new JTextField();
        whis.setBounds(400, 300, 75, 30);
        window.add(whis);

        whisper = new JButton("whisper");
        whisper.setBounds(400, 335, 75, 30);
        window.add(whisper);

        send = new JButton("send");
        send.setBounds(350, 300, 50, 50);
        window.add(send);

        myEvent();  // add listeners
        window.setVisible(true);
    }


    public void myEvent() {
        window.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (socket != null && socket.isConnected()) {
                    try {
                        new ClientSend(socket, getNickName(), "3", "");
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                System.exit(0);
            }
        });

        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (socket == null) {
                    JOptionPane.showMessageDialog(window, "connection has been closed!");
                } else if (socket != null && socket.isConnected()) {
                    try {
                        new ClientSend(socket, getNickName(), "3", "");
                        connect.setText("connect");
                        exit.setText("exited");
                        socket.close();
                        socket = null;
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        connect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (socket != null && socket.isConnected()) {
                    JOptionPane.showMessageDialog(window, "connection good!");
                } else {
                    String ipString = "127.0.0.1";
                    String portClinet = port.getText();
                    String name = nickName.getText();

                    if ("".equals(name) || "".equals(portClinet)) {
                        JOptionPane.showMessageDialog(window, "nickname or port can't be empty!");
                    } else {
                        try {
                            int ports = Integer.parseInt(portClinet);
                            socket = new Socket(ipString, ports);
                            connect.setText("connected");
                            exit.setText("exit");
                            new ClientSend(socket, getNickName(), "2", "");
                            new Thread(new ClientReceive(socket)).start();
                        } catch (Exception e2) {
                            JOptionPane.showMessageDialog(window, "fail to connect, check the ip and port, or server");
                        }
                    }
                }
            }
        });

        whisper.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMsgWhis();
            }
        });

        send.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMsg();
            }
        });
    }

    public void sendMsg() {
        String messages = message.getText();
        if ("".equals(messages)) {
            JOptionPane.showMessageDialog(window, "there is nothing to send!");
        } else if (socket == null || !socket.isConnected()) {
            JOptionPane.showMessageDialog(window, "no connection");
        } else {
            try {
                new ClientSend(socket, getNickName() + ": " + messages, "1", "");
                message.setText(null);
            } catch (IOException e1) {
                JOptionPane.showMessageDialog(window, "fail to send!");
            }
        }
    }

    public void sendMsgWhis() {
        String messages = message.getText();
        if ("".equals(messages)) {
            JOptionPane.showMessageDialog(window, "there is nothing to whisper!");
        } else if (socket == null || !socket.isConnected()) {
            JOptionPane.showMessageDialog(window, "no connection");
        } else {
            try {
                new ClientSend(socket, getNickName() + ": " + messages, "4", getWhisper());
                ClientGUI.textMessage.append(getNickName() + ": " + messages + "\r\n");
                message.setText(null);
            } catch (IOException e1) {
                JOptionPane.showMessageDialog(window, "fail to whisper!");
            }
        }
    }

    public String getNickName() {
        return nickName.getText();
    }

    public String getWhisper() {
        return whis.getText();
    }
}
