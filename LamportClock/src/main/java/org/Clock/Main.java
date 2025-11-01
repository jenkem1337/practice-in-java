package org.Clock;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        var co1 = new ConcurrentObject1();
        var co2 = new ConcurrentObject2();
        var co3 = new ConcurrentObject3();

        co1.addNode(co2);
        co1.addNode(co3);

        co2.addNode(co1);
        co2.addNode(co3);

        co3.addNode(co1);
        co3.addNode(co2);
        co1.sendMessage();
        var t1 = new Thread(() -> {
            try {
                co1.processMessageQueue();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        var t2 = new Thread(() -> {
            try {
                co2.processMessageQueue();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        var t3 = new Thread(() -> {
            try {
                co3.processMessageQueue();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        t1.start();
        t2.start();
        t3.start();

    }
}