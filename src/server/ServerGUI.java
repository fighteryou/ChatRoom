package server;

/**
 * Created by youzhou on 6/15/17.
 */

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class ServerGUI {
    public static JFrame window;
    public static int ports;
    public static JTextArea console;
    public static JList<String> user;

    JButton launch, exit, send;
    JTextField serverName, serverPort, message;

    //main method
    public static void main(String[] args) {
        new ServerGUI();
    }

    public ServerGUI() {
        init();
    }

    public void init() {   // absolute layout
        window = new JFrame("server");
        window.setLayout(null);
        window.setBounds(200, 200, 500, 400);
        window.setResizable(false);

        JLabel labelServerName = new JLabel("server name:");
        labelServerName.setBounds(10, 8, 80, 30);
        window.add(labelServerName);

        serverName = new JTextField();
        serverName.setBounds(100, 8, 60, 30);
        window.add(serverName);

        JLabel label_port = new JLabel("port:");
        label_port.setBounds(170, 8, 40, 30);
        window.add(label_port);

        serverPort = new JTextField();
        serverPort.setBounds(220, 8, 70, 30);
        window.add(serverPort);

        launch = new JButton("launch");
        launch.setBounds(300, 8, 90, 30);
        window.add(launch);

        exit = new JButton("exit");
        exit.setBounds(400, 8, 90, 30);
        window.add(exit);

        console = new JTextArea();
        console.setBounds(10, 70, 340, 220);
        console.setEditable(false);  // can't be edited

        console.setLineWrap(true);  // automatic content line feed
        console.setWrapStyleWord(true);

        JLabel label_text = new JLabel("Console");
        label_text.setBounds(140, 40, 100, 30);
        window.add(label_text);

        JScrollPane paneText = new JScrollPane(console);
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
        window.add(message);

        send = new JButton("send");
        send.setBounds(380, 305, 70, 40);
        window.add(send);

        myEvent();  // add listeners
        window.setVisible(true);
    }

    public void myEvent() {
        window.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (ServerLaunch.userList != null && ServerLaunch.userList.size() != 0) {
                    try {
                        new ServerSend(ServerLaunch.userList, "", "5", "");
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                System.exit(0); // exit the window
            }
        });

        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (ServerLaunch.ss == null || ServerLaunch.ss.isClosed()) {
                    JOptionPane.showMessageDialog(window, "server has been closed!");
                } else {
                    if (ServerLaunch.userList != null && ServerLaunch.userList.size() != 0) {
                        try {
                            new ServerSend(ServerLaunch.userList, "", "5", "");
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                    try {
                        launch.setText("launch");
                        exit.setText("closed");
                        ServerLaunch.ss.close();
                        ServerLaunch.ss = null;
                        ServerLaunch.userList = null;
                        ServerLaunch.flag = false;   // important
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        launch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (ServerLaunch.ss != null && !ServerLaunch.ss.isClosed()) {
                    JOptionPane.showMessageDialog(window, "server has launched");
                } else {
                    ports = getPort();
                    if (ports != 0) {
                        try {
                            ServerLaunch.flag = true;
                            new Thread(new ServerLaunch(ports)).start(); // start server thread
                            launch.setText("launched");
                            exit.setText("close");
                        } catch (IOException e1) {
                            JOptionPane.showMessageDialog(window, "fail to run");
                        }
                    }
                }
            }
        });

        message.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMsg();
                }
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
        } else if (ServerLaunch.userList == null || ServerLaunch.userList.size() == 0) {
            JOptionPane.showMessageDialog(window, "there is no connection!");
        } else {
            try {
                new ServerSend(ServerLaunch.userList, getServerName() + ": " + messages, "1", "");
                message.setText(null);
            } catch (IOException e1) {
                JOptionPane.showMessageDialog(window, "fail to send!");
            }
        }
    }

    public int getPort() {
        String port = serverPort.getText();
        String name = serverName.getText();
        if ("".equals(port) || "".equals(name)) {
            JOptionPane.showMessageDialog(window, "no port found or no name found");
            return 0;
        } else {
            return Integer.parseInt(port);
        }
    }

    public String getServerName() {
        return serverName.getText();
    }
}