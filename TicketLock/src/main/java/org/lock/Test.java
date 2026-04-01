package org.lock;

import java.util.concurrent.CountDownLatch;

public class Test {
    int counter = 0;
    public static void main(String[] args) throws InterruptedException {
        Test test = new Test();
        Lock lock = new TicketLock();
        CountDownLatch cdl = new CountDownLatch(1_000_000);
        for(int i = 0; i < 1_000_000; i++) {
            Thread.ofVirtual().start(() -> {
                lock.lock();
                test.counter++;
                lock.unlock();
                cdl.countDown();

            });

        }
        cdl.await();
        System.out.println(test.counter);
    }
}
