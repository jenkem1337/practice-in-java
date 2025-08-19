package org.WakeUp;

import java.util.concurrent.*;

public class RingBufferProducerConsumer {
    private final ArrayBlockingQueue<Message> ringBuffer = new ArrayBlockingQueue<>(1024);
    private final ExecutorService singleExecutor = Executors.newSingleThreadExecutor();
    private volatile boolean isRunning = true;
    public RingBufferProducerConsumer() {
        singleExecutor.execute(() -> {
            try {
                consume();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }
    public CompletableFuture<String> produce(String msg) throws InterruptedException {
        final var future = new CompletableFuture<String>();
        ringBuffer.put(new Message(msg, future));
        return future;
    }

    private void consume() throws InterruptedException {
        while(isRunning) {
            var message = ringBuffer.take();
            message.complete(message.msg());
        }
    }

    public void close() {
        isRunning = false;

        singleExecutor.shutdown();
        try {
            if (!singleExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                singleExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            singleExecutor.shutdownNow();
        }


    }
}
