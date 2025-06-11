package org.TCPWatchExample.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;

public class TCPSocketClient {
    private final Socket clientSocket;
    private PrintWriter output;
    private BufferedReader input;

    public TCPSocketClient(String address, int port) throws IOException {
        clientSocket = new Socket(address, port);
        output = new PrintWriter(clientSocket.getOutputStream(), true);
        input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

    }
    public void watchDelete() throws IOException {
        Socket _clientSocket = new Socket("localhost", 9090);
        PrintWriter _output = new PrintWriter(_clientSocket.getOutputStream(), true);
        BufferedReader _input = new BufferedReader(new InputStreamReader(_clientSocket.getInputStream()));

        Thread listenerThread = new Thread(() -> {
            _output.println("WATCH_REMOVE");
            String line;
            try {
                while ((line = _input.readLine()) != null) {
                    System.out.println("EVENT: " + line);
                }
            } catch (IOException e) {
                System.out.println("Connection closed or error while watching: " + e.getMessage());
            }
        });
        listenerThread.setDaemon(true);
        listenerThread.start();
    }
    public String sendMessage(String message) throws IOException {
        output.println(message);
        return input.readLine();
    }
    public void closeConnection() throws IOException {
        clientSocket.close();
    }

}