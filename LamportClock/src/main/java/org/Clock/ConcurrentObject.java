package org.Clock;

public interface ConcurrentObject {
    void sendMessage() throws InterruptedException;
    void receiveMessage(Message msg) throws InterruptedException;
    void addNode(ConcurrentObject other);
    void writeToQueue(Message msg) throws InterruptedException;
    void processMessageQueue() throws InterruptedException;
}
