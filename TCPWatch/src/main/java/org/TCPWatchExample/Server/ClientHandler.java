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
                else if ("WATCH".equals(commands[0])) {
                    cache.addObserver(commands[1], new ClientSession(clientSocket, output));
                    output.println("Client watching " + commands[1] + " key");
                }
                else if ("REMOVE".equals(commands[0])) {
                    cache.remove(commands[1]);
                    output.println();
                }
                else if ("GET".equals(commands[0])) {
                    var response = cache.get(commands[1]);
                    output.println("GET VALUE -> " + response);

                }
                else if ("PUT".equals(commands[0])) {
                    var response =cache.put(commands[1], commands[2]);
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