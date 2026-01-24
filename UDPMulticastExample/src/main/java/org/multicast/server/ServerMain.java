package org.multicast.server;

import java.net.SocketException;
import java.net.UnknownHostException;

public class ServerMain {
    public static void main(String[] args) throws SocketException, UnknownHostException {
        var scheduler = new RandomPriceMovementScheduler();
        scheduler.startPriceMovementScheduler();

        Runtime.getRuntime().addShutdownHook(new Thread(scheduler::closeScheduler));
    }
}
