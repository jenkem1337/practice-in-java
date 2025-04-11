package org.Algorithms;

import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<RequestWrapper> queue = new LinkedBlockingQueue<>();

        Producer counterProducer =  Producer.of(new CounterConsumer());

        var service = Executors.newFixedThreadPool(10);

        for(int i = 0; i < 100; i++) {
            service.execute(() ->
            {
                try {
                    counterProducer.sendMessage("INCREMENT");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        Thread.sleep(500);

        CompletableFuture<ResponseMessage> response = counterProducer.sendMessage("GET");
        response.thenAccept(res -> System.out.println(res.message()));
    }
}