package org.MessageBroker;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class NonBlockingRingBuffer implements Partition{
    private final AtomicReferenceArray<Message> buffer;
    private final AtomicInteger writeIndex;
    private final AtomicInteger readIndex;
    private final int capacity;
    private final AtomicReference<Consumer> consumer;

    public NonBlockingRingBuffer(int capacity) {
        if (capacity < 0 ) throw new RuntimeException("capacity must greater than zero");
        this.buffer =  new AtomicReferenceArray<>(capacity);
        this.writeIndex = new AtomicInteger(0);
        this.readIndex = new AtomicInteger(0);
        this.consumer = new AtomicReference<>(null);
        this.capacity = capacity;
    }

    @Override
    public int put(Message item) {
        boolean isSuccess = false;
        int currentWriter = 0;
        while(!isSuccess) {
            currentWriter = writeIndex.get();
            if ((currentWriter + 1) % capacity == readIndex.get()) {
                return 0;
            }
            isSuccess = writeIndex.compareAndSet(currentWriter,(currentWriter + 1) % capacity);
        }
        buffer.set(currentWriter, item);
        return 1;
    }

    @Override
    public Message get() {
        boolean isSuccess = false;
        Message message = null;
        while(!isSuccess) {
            var currentReader = readIndex.get();
            if (!(writeIndex.get() ==  currentReader)) {
                message = buffer.get(currentReader);
                isSuccess = readIndex.compareAndSet(currentReader, (currentReader + 1) % capacity);
            }
        }
        return message;
    }

    @Override
    public void setConsumer(Consumer consumer){
        this.consumer.set(consumer);
    }

    @Override
    public Consumer getConsumer(){
        return consumer.get();
    }
}
