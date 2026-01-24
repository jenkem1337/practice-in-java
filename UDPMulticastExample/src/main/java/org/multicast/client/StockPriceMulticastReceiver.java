package org.multicast.client;

import java.io.IOException;
import java.net.*;

public class StockPriceMulticastReceiver implements  Runnable{
    protected MulticastSocket socket = null;
    protected byte[] buf = new byte[4096];
    protected InetAddress group;
    public StockPriceMulticastReceiver() throws IOException {
        socket = new MulticastSocket(null);
        socket.setReuseAddress(true);
        socket.bind(new InetSocketAddress(4446));
        group = InetAddress.getByName("230.0.0.0");

        NetworkInterface networkInterface =
                NetworkInterface.getByInetAddress(InetAddress.getLocalHost());

        socket.joinGroup(
                new InetSocketAddress(group, 4446),
                networkInterface
        );

    }

    @Override
    public void run() {
        while (true) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String received = new String(
                    packet.getData(), 0, packet.getLength());
            System.out.println(received);
        }
    }
}
