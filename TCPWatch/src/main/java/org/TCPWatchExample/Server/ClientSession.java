package org.TCPWatchExample.Server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientSession {
    private final Socket socket;
    private final PrintWriter writer;

    public ClientSession(Socket socket, PrintWriter printWriter) throws IOException {
        this.socket = socket;
        this.writer = printWriter;
    }

    public void send(String message) {
        writer.println(message);
    }

    public boolean isAlive() {
        return !socket.isClosed();
    }

}
