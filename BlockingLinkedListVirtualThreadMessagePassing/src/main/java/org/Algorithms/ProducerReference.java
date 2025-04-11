package org.Algorithms;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;

public class ProducerReference implements Producer{
    private final BlockingQueue<RequestWrapper> sharedQueue;

    public ProducerReference(Consumer consumer) {
        this.sharedQueue = new LinkedBlockingQueue<>();
        Thread.ofVirtual().start(() -> {
            while(true) {
                try {
                    RequestWrapper requestWrapper = this.sharedQueue.take();
                    var response = consumer.onMessage(requestWrapper.request());
                    requestWrapper.complete(response);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }

    @Override
    public CompletableFuture<ResponseMessage> sendMessage(Object obj) throws InterruptedException {
        RequestWrapper msg = new RequestWrapper(new RequestMessage(obj));
        sharedQueue.add(msg);
        return msg.future();
    }
}
