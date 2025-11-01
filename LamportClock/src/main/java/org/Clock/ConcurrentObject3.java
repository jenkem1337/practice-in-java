package org.Clock;

import java.util.concurrent.*;

public class ConcurrentObject3 implements ConcurrentObject {
    private final LamportClock clock = new LamportClock();
    private final CopyOnWriteArrayList<ConcurrentObject> others = new CopyOnWriteArrayList<>();
    private final BlockingQueue<Message> messageQueue = new LinkedBlockingQueue<>();


    @Override
    public void sendMessage() throws InterruptedException {
        long version = clock.incrementAndGet();
        var otherNode = others.get(ThreadLocalRandom.current().nextInt(0, others.size()));
        otherNode.writeToQueue(new Message("Hi, i am "+ this.getClass().getName() + " and this message writen to " + otherNode.getClass().getName(), version));
    }

    @Override
    public void receiveMessage(Message msg) throws InterruptedException {
        long oldVersion = clock.version();
        clock.receive(msg.version());
        System.out.println("Old Version : " + oldVersion + " Message And Sender Version: " + msg.msg() + ", " + msg.version() + " New Version : " + clock.version());
        sendMessage();
    }
    @Override
    public void addNode(ConcurrentObject other) {
        others.add(other);
    }

    @Override
    public void writeToQueue(Message msg) throws InterruptedException {
        this.messageQueue.put(msg);
    }
    @Override
    public void processMessageQueue() throws InterruptedException {

        while(true) {
            receiveMessage(messageQueue.take());
            Thread.sleep(ThreadLocalRandom.current().nextInt(100, 1000));
        }
    }

}
