package org.ApplicationContext.Client;

import java.io.IOException;

import static java.lang.Thread.sleep;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
            TCPClient client = new TCPClient("localhost", 9090);
            var res = client.sendMessage("PING");
            System.out.println(res);
            res = client.sendMessage("GNIP");
            System.out.println(res);
            res = client.sendMessage("EXIT");
            System.out.println(res);

        client.closeConnection();
    }



}
