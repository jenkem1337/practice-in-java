package org.Lock;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.LockSupport;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Counter counter = new Counter(new SpinLock());
        var cdl = new CountDownLatch(1_000_000);
        for(int i = 0; i < 1_000_000; i++) {
            Thread.ofVirtual().start(() -> {
                try {
                    counter.incrementSync();
                } finally {
                    cdl.countDown();
                }
            });
        }
        cdl.await();
        System.out.println(counter.get());
    }
}