package org.ApplicationContext.Client;

import org.ApplicationContext.Server.TCPSocketServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TCPClient {
    private final Socket clientSocket;
    private PrintWriter output;
    private BufferedReader input;

    public TCPClient(String address, int port) throws IOException {
        clientSocket = new Socket(address, port);
        output = new PrintWriter(clientSocket.getOutputStream(), true);
        input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

    }

    public String sendMessage(String message) throws IOException {
        output.println(message);
        return input.readLine();
    }
    public void closeConnection() throws IOException {
        clientSocket.close();
    }
}
