package org.EventEmitter;

import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    public static void main(String[] args) {
        record Increment() {}
        AtomicInteger counter = new AtomicInteger();
        var eventEmitter = EventEmitter.getInstance();
        eventEmitter.saveEvent("Increment", (Increment command) -> {
            int actualState = counter.addAndGet(1);
            System.out.println("Counter incremented : " + actualState);
        });
        eventEmitter.emit("Increment", new Increment());
        eventEmitter.emit("Increment", new Increment());
        eventEmitter.emit("Increment", new Increment());
        eventEmitter.emit("Increment", new Increment());
        eventEmitter.emit("Increment", new Increment());

        eventEmitter.shutdownEventEmitter();

        System.out.println(counter.get());
    }
}