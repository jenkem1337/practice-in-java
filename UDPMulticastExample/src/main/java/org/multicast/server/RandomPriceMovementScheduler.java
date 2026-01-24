package org.multicast.server;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class RandomPriceMovementScheduler {
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private final MulticastServer multicastServer = new MulticastServer();
    private final List<Stock> stockList = new ArrayList<>();
    private final ThreadLocalRandom random = ThreadLocalRandom.current();
    public RandomPriceMovementScheduler() throws SocketException, UnknownHostException {
        stockList.add(new Stock("ISMEN"));
        stockList.add(new Stock("TUPRS"));
        stockList.add(new Stock("ENJSA"));
        stockList.add(new Stock("EREGL"));

    }

    public void startPriceMovementScheduler() {

        Runnable job = () -> {
            for(Stock stock : stockList) {
                float percentageRandomPriceMovement = random.nextFloat(-0.002f, 0.005f);
                float coefficient = 1.00f + percentageRandomPriceMovement;
                stock.changePrice(coefficient);
//                System.out.println(stock);
                try {
                    multicastServer.multicastStockPrice(stock);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        scheduledExecutorService.scheduleAtFixedRate(job, 0, 1, TimeUnit.SECONDS);
        System.out.println("Scheduler started");

    }
    public void closeScheduler() {
        scheduledExecutorService.close();
        multicastServer.closeSocket();
    }
}
