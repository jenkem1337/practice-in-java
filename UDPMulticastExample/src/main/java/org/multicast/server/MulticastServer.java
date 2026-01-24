package org.multicast.server;

import java.io.IOException;
import java.net.*;

public class MulticastServer {
    private final DatagramSocket socket;
    private final InetAddress group;

    public MulticastServer() throws SocketException, UnknownHostException {
        socket = new DatagramSocket();
        group = InetAddress.getByName("230.0.0.0");
        System.out.println("Stock price multicast server started !!");
    }

    public void multicastStockPrice(Stock stock) throws IOException {
        byte[] buf = stock.toString().getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, group, 4446);
        socket.send(packet);
    }

    public void closeSocket() {
        socket.close();
    }
}
