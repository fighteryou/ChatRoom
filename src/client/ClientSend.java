package client;

/**
 * Created by youzhou on 6/25/17.
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientSend {
    ClientSend(Socket s, Object message, String info, String name) throws IOException {
        String messages = info + ",," + message + ",," + name;
        PrintWriter pwOut = new PrintWriter(s.getOutputStream(), true);
        pwOut.println(messages);
    }
}
