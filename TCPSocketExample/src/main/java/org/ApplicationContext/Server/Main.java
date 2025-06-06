package org.ApplicationContext.Server;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        TCPSocketServer server = TCPSocketServer.virtualThreadedServer(9090);
        server.startServer();
    }
}