package org.AsynchronousSocketChannel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncServer {
    private AsynchronousServerSocketChannel serverChannel;
    private volatile boolean isRunning = false;
    //private AsynchronousSocketChannel clientChannel;
    private int port;
    private String host;
    public AsyncServer(ExecutorService executorService) throws IOException {
        this.serverChannel = AsynchronousServerSocketChannel.open(
                AsynchronousChannelGroup.withThreadPool(executorService)
        );

    }

    public void bind(String host, int port) throws IOException {
        this.host = host;
        this.port = port;
        serverChannel.bind(new InetSocketAddress(this.host, this.port));
    }

    public void startServer() throws IOException, InterruptedException {
        System.out.println("Server started on "+ port);
        isRunning = true;
        while (isRunning) {
            serverChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
                @Override
                public void completed(AsynchronousSocketChannel result, Void attachment) {
                    if(serverChannel.isOpen()){
                        serverChannel.accept(null, this);
                    }
                    var clientChannel = result;
                    if ((clientChannel != null) && (clientChannel.isOpen())) {
                        ReadWriteHandler handler = new ReadWriteHandler(clientChannel);
                        ByteBuffer buffer = ByteBuffer.allocate(1024);

                        Map<String, Object> readInfo = new HashMap<>();
                        readInfo.put("action", "read");
                        readInfo.put("buffer", buffer);

                        clientChannel.read(buffer, readInfo, handler);
                    }

                }
                @Override
                public void failed(Throwable exc, Void attachment) {
                }
            });
            //System.in.read();
            Thread.currentThread().join();
        }

    }
    public void closeChannel() throws IOException {
        isRunning = false;
        serverChannel.close();
        System.out.println("Server channel closed gracefully!!");
    }
}
