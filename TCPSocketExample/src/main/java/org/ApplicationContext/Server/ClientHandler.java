package org.ApplicationContext.Server;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private BufferedReader input;
    private PrintWriter output;
    private Socket clientSocket;
    public ClientHandler(Socket socket) {
        clientSocket = socket;
    }
    @Override
    public void run() {
        try {
            output = new PrintWriter(clientSocket.getOutputStream(), true);
            input = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));

            String inputLine;
            while ((inputLine = input.readLine()) != null) {
                if ("EXIT".equals(inputLine)) {
                    output.println("bye");
                    break;
                }
                if ("PING".equals(inputLine)) {
                    output.println("PONG");
                } else {
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
