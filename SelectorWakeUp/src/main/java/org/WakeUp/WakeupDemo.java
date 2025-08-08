package org.WakeUp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

public class WakeupDemo {
    public static void start() throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel server = ServerSocketChannel.open();
        server.bind(new InetSocketAddress(5000));
        server.configureBlocking(false);
        server.register(selector, SelectionKey.OP_ACCEPT);

        var pool = Executors.newFixedThreadPool(4);
        boolean USE_WAKEUP = true; // false;

        System.out.println("Server started on port 5000");

        while (true) {
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> it = keys.iterator();

            while (it.hasNext()) {
                SelectionKey key = it.next();
                it.remove();

                try {
                    if (key.isAcceptable()) {
                        SocketChannel client = server.accept();
                        client.configureBlocking(false);
                        client.register(selector, SelectionKey.OP_READ);
                        System.out.println("Client connected");
                    }
                    else if (key.isReadable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer buf = ByteBuffer.allocate(1024);
                        int read = client.read(buf);
                        if (read == -1) {
                            client.close();
                            continue;
                        }
                        String message = new String(buf.array(), 0, read);
                        System.out.println("Received: " + message.trim());

                        CompletableFuture.supplyAsync(() -> {
                            try { Thread.sleep(2000); } catch (InterruptedException e) {}
                            return "DB Result for: " + message.trim() + "\n";
                        }, pool).thenAccept(result -> {
                            key.attach(ByteBuffer.wrap(result.getBytes()));
                            key.interestOps(SelectionKey.OP_WRITE);

                            if (USE_WAKEUP) {
                                selector.wakeup();
                            }
                        });
                    }
                    else if (key.isWritable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer buf = (ByteBuffer) key.attachment();
                        client.write(buf);
                        if (!buf.hasRemaining()) {
                            key.interestOps(SelectionKey.OP_READ);
                        }
                    }
                } catch (IOException e) {
                    key.cancel();
                    key.channel().close();
                }
            }
        }
    }
}
