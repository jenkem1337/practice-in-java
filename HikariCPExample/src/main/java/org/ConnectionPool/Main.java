package org.ConnectionPool;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.*;

public class Main {
    public final static int TASK_COUNT = 10_000;
    public static void main(String[] args) throws InterruptedException {
        var dao = UserDao.hikariCp();
        var downLatch = new CountDownLatch(TASK_COUNT);
            Instant start = Instant.now();
            Semaphore semaphore = new Semaphore(HikariDataSourceProvider.MAX_POOL_SIZE);
            for (int i = 0; i < TASK_COUNT; i++) {
                Thread.startVirtualThread(() -> {
                    try {
                        semaphore.acquire();
                        for(User u : dao.findAllUsers()) {
                            System.out.println(u);
                        }

                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } finally {
                        semaphore.release();
                        downLatch.countDown();
                    }
                });
            }
            downLatch.await();
            Instant end = Instant.now();
            long durationMs = Duration.between(start, end).toMillis();
            double throughput = TASK_COUNT / (durationMs / 1000.0);
            System.out.printf("Toplam süre: %d ms, Throughput: %.2f sorgu/sn%n",
                    durationMs, throughput);

//        Instant start = Instant.now();
//
//        for (int i = 0; i < TASK_COUNT; i++) {
//            new Thread(() -> {
//                for(User u : dao.findAllUsers()) {
//                    System.out.println(u);
//                }
//                downLatch.countDown();
//            }).start();
//        }
//        downLatch.await();
//        Instant end = Instant.now();
//        long durationMs = Duration.between(start, end).toMillis();
//        double throughput = TASK_COUNT / (durationMs / 1000.0);
//        System.out.printf("Toplam süre: %d ms, Throughput: %.2f sorgu/sn%n",durationMs, throughput);
    }
}
