package org.TCPWatchExample.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {
    private BufferedReader input;
    private PrintWriter output;
    private Socket clientSocket;
    private final ObservableCache cache;
    public ClientHandler(Socket socket, ObservableCache cache) {
        clientSocket = socket;
        this.cache = cache;
    }
    @Override
    public void run() {
        try {
            output = new PrintWriter(clientSocket.getOutputStream(), true);
            input = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));

            String inputLine;
            while ((inputLine = input.readLine()) != null) {
                String[] commands = inputLine.split(" ");
                if ("EXIT".equals(commands[0])) {
                    output.println("bye");
                    break;
                }
                if ("PING".equals(commands[0])) {
                    output.println("PONG");
                }
                else if ("WATCH_REMOVE".equals(commands[0])) {
                    cache.addObserver(new ClientSession(clientSocket, output));
                    output.println("Client watching cache remove");
                }
                else if ("REMOVE".equals(commands[0])) {
                    Integer key = Integer.parseInt(commands[1]);
                    cache.remove(key);

                }
                else if ("GET".equals(commands[0])) {
                    Integer key = Integer.parseInt(commands[1]);
                    var response = cache.get(key);
                    output.println("GET VALUE -> " + response);

                }
                else if ("PUT".equals(commands[0])) {
                    Integer key = Integer.parseInt(commands[1]);
                    String val = commands[2];
                    var response =cache.put(key, val);
                    output.println("PUT VALUE -> " + response);

                }

                else {
                    output.println("Unknown message, try again !");

                }
            }

            input.close();
            output.close();
            clientSocket.close();

        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }

    }

}