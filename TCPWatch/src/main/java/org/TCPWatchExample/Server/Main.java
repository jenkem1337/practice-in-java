package org.TCPWatchExample.Server;

import org.TCPWatchExample.Client.TCPSocketClient;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        var socket = new TCPSocketServer(9090);
        socket.startServer();
    }
}