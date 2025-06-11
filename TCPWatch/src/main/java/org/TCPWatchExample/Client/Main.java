package org.TCPWatchExample.Client;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        var socket = new TCPSocketClient("localhost",9090);
        socket.watchDelete();

        var msg = socket.sendMessage("PING");
        System.out.println(msg);

        msg = socket.sendMessage("PUT 1 MERHABA");
        System.out.println(msg);
        msg = socket.sendMessage("GET 1");
        System.out.println(msg);
        socket.sendMessage("REMOVE 1");

//        socket.sendMessage("EXIT");
//        socket.closeConnection();

    }
}
