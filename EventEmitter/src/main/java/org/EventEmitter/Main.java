package org.EventEmitter;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        record Increment() {}
        record Decrement() {}

        AtomicInteger counter = new AtomicInteger();

        var eventEmitter = EventEmitter.getInstance();

        eventEmitter.saveEvent("Increment", (Increment command) -> {
            int actualState = counter.addAndGet(1);
            System.out.println("Counter incremented : " + actualState);
        });

        eventEmitter.saveEvent("Decrement", (Decrement command) -> {
            int actualState = counter.addAndGet(-1);
            System.out.println("Counter decremented : " + actualState);
        });
        CompletableFuture<Boolean> f1  = eventEmitter.emit("Increment", new Increment());
        CompletableFuture<Boolean> f2  = eventEmitter.emit("Decrement", new Decrement());
        CompletableFuture<Boolean> f3  = eventEmitter.emit("Increment", new Increment());
        CompletableFuture<Boolean> f4  = eventEmitter.emit("Decrement", new Decrement());
        CompletableFuture<Boolean> f5  = eventEmitter.emit("Increment", new Increment());
        CompletableFuture<Boolean> f6  = eventEmitter.emit("Increment", new Increment());
        CompletableFuture<Boolean> f7  = eventEmitter.emit("Decrement", new Decrement());
        CompletableFuture<Boolean> f8  = eventEmitter.emit("Decrement", new Decrement());
        CompletableFuture<Boolean> f9  = eventEmitter.emit("Increment", new Increment());
        CompletableFuture<Boolean> f10 = eventEmitter.emit("Decrement", new Decrement());

        CompletableFuture<Void> combinedFutures = CompletableFuture.allOf(f1, f2,f3,f4,f5,f6,f7,f8,f9,f10);
        combinedFutures.get();
        eventEmitter.shutdownEventEmitter();
        System.out.println("Counter: " + counter.get());

    }
}