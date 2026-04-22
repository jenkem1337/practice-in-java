package org.lock;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Test {
    int counter = 0;
    public static void main(String[] args) throws InterruptedException {
        Test test = new Test();
        Lock lock = new TicketLock();
        CountDownLatch cdl = new CountDownLatch(1_000_000);
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        for(int i = 0; i < 1_000_000; i++) {
            executorService.execute(() -> {
                lock.lock();
                test.counter++;
                lock.unlock();
                cdl.countDown();

            });

        }
        cdl.await();
        executorService.shutdown();
        System.out.println(test.counter);
    }
}
