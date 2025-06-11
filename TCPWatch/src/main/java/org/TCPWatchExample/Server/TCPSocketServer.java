package org.TCPWatchExample.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class TCPSocketServer {
    private final ServerSocket serverSocket;
    //private final ConcurrentMap<String, ClientHandler> map;

    private final ExecutorService pool;
    private final int port;
    private final ObservableCache cache;
    public TCPSocketServer(int port) throws IOException {
        this.port = port;
        //map = new ConcurrentHashMap<>();
        pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        serverSocket = new ServerSocket(this.port);
        cache = new ObservableCache(new ConcurrentSkipListMap<>(), new ConcurrentHashMap<>());

    }

    public void startServer() throws IOException {
        System.out.println("Server listening port " + port );
        while(true) {
            System.out.println("Server waiting connection ....");
            Socket clientSocket = serverSocket.accept();
            pool.execute(new ClientHandler(clientSocket, cache));
        }
    }

}
