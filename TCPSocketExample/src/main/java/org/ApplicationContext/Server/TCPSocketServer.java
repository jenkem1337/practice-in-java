package org.ApplicationContext.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPSocketServer{
    private final ServerSocket serverSocket;
    private final int port;
    private final ConcurrencyStrategy concurrencyStrategy;

    private TCPSocketServer(int port, ConcurrencyStrategy concurrencyStrategy) throws IOException {
        this.port = port;
        serverSocket = new ServerSocket(this.port);
        this.concurrencyStrategy = concurrencyStrategy;
    }

    static TCPSocketServer virtualThreadedServer(int port) throws IOException {
        return new TCPSocketServer(port, new VirtualThreadStrategy());
    }

    static TCPSocketServer platformThreadedServer(int port) throws IOException {
        return new TCPSocketServer(port, new PlatformThreadStrategy());
    }

    public void startServer() throws IOException {
        System.out.println("Server listening port " + port );
        while(true) {
            System.out.println("Server waiting connection ....");
            Socket clientSocket = serverSocket.accept();
            concurrencyStrategy.execute(new ClientHandler(clientSocket));
        }

    }
}
