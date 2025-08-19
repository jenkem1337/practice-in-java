package org.WakeUp;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        var buffer = new RingBufferProducerConsumer();
        var barrier = new CountDownLatch(1);
        var future1 = buffer.produce("Merhaba");
        var future2 = buffer.produce("Dünya");
        future1.thenCombineAsync(future2, (firstMessage, secondMessage) -> {
            System.out.println(firstMessage + secondMessage);
            return null;
        });
        System.out.println("merhaba");


    }
}