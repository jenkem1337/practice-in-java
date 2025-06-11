package org.TCPWatchExample.Client;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        var socket = new TCPSocketClient("localhost",9090);
        var socket2 = new TCPSocketClient("localhost",9090);

        socket.watch("/service/java-app-1");
        socket2.watch("/service/java-app-1");
        var msg = socket.sendMessage("PING");
        System.out.println(msg);
        msg = socket.sendMessage("PUT /service/java-app-1 127.0.0.1");
        System.out.println(msg);
        msg = socket.sendMessage("GET /service/java-app-1");
        System.out.println(msg);
        socket.sendMessage("REMOVE /service/java-app-1");
        msg = socket.sendMessage("PUT /service/java-app-1 143.10.55.199");
        System.out.println(msg);

        msg = socket.sendMessage("PUT /service/java-app-1 143.10.55.119");
        System.out.println(msg);

        msg = socket.sendMessage("GET /service/java-app-1");
        System.out.println(msg);
        socket.sendMessage("REMOVE /service/java-app-1");

        socket.closeEventListenerConnection();
        socket.closeConnection();
    }
}
