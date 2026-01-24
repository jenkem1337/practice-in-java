package org.multicast.client;

import java.io.IOException;

public class ClientMain {
    public static void main(String[] args) throws IOException {
        var receiver = new StockPriceMulticastReceiver();
        new Thread(receiver).start();
    }
}
