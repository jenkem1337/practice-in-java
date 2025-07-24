package org.AsynchronousSocketChannel;

import java.io.IOException;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Hello, World!");

        var server = new AsyncServer(Executors.newVirtualThreadPerTaskExecutor());
        server.bind("localhost",9090);
        server.startServer();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                server.closeChannel();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));
    }
}