package org.TCPWatchExample.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;

public class TCPSocketClient {
    private final Socket clientSocket;
    private final PrintWriter output;
    private final BufferedReader input;
    private final String address;
    private final int port;
    private Thread eventListenerThread;
    private Socket eventListenerSocket;
    private PrintWriter eventListenerOutput;
    private BufferedReader eventListenerInput;

    public TCPSocketClient(String address, int port) throws IOException {
        this.address = address;
        this.port = port;
        clientSocket = new Socket(address, port);
        output = new PrintWriter(clientSocket.getOutputStream(), true);
        input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

    }
    public void watch(String key) throws IOException {

        eventListenerThread = new Thread(() -> {
            try {
                eventListenerSocket = new Socket(address, port);
                eventListenerOutput = new PrintWriter(eventListenerSocket.getOutputStream(), true);
                eventListenerInput = new BufferedReader(new InputStreamReader(eventListenerSocket.getInputStream()));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            eventListenerOutput.println("WATCH "+ key);
            String line;
            try {
                while ((line = eventListenerInput.readLine()) != null) {
                    System.out.println("EVENT: " + line);
                }
            } catch (IOException e) {
                System.out.println("Connection closed or error while watching: " + e.getMessage());
            }
        });
        eventListenerThread.start();
    }
    public String sendMessage(String message) throws IOException {
        output.println(message);
        return input.readLine();
    }
    public void closeConnection() throws IOException {
        output.println("EXIT");
        clientSocket.close();
        input.close();
        output.close();
    }
    public void closeEventListenerConnection() throws IOException {
        eventListenerOutput.println("EXIT");
        eventListenerSocket.close();
        eventListenerInput.close();
        eventListenerOutput.close();
        eventListenerThread.interrupt();
    }
}