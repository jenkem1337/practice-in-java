package org.AsynchronousSocketChannel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.*;

public class Client {
    public static void main(String[] args) throws InterruptedException, IOException, ExecutionException {
        AsynchronousSocketChannel clientChannel = AsynchronousSocketChannel.open();
        InetSocketAddress serverAddress = new InetSocketAddress("localhost", 9090);
        Future<Void> future = clientChannel.connect(serverAddress);
        future.get();

        AsynchronousSocketChannel clientChannel2 = AsynchronousSocketChannel.open();
        InetSocketAddress serverAddress2 = new InetSocketAddress("localhost", 9090);
        Future<Void> future2 = clientChannel2.connect(serverAddress2);
        future2.get();

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                System.out.println(sendMessage(clientChannel2, "Merhaba Dünya !"));
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        },0, 1, TimeUnit.SECONDS);

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                System.out.println(sendMessage(clientChannel, "Hello World"));
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        },0, 1, TimeUnit.SECONDS);

    }

    private static String sendMessage(AsynchronousSocketChannel client, String message) throws ExecutionException, InterruptedException {
        byte[] byteMsg = new String(message).getBytes();
        ByteBuffer buffer = ByteBuffer.wrap(byteMsg);
        Future<Integer> writeResult = client.write(buffer);

        // do some computation

        writeResult.get();
        buffer.flip();
        Future<Integer> readResult = client.read(buffer);

        // do some computation

        readResult.get();
        String echo = new String(buffer.array()).trim();
        buffer.clear();
        return echo;

    }
}
