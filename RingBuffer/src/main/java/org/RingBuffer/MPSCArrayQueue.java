package org.RingBuffer;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

public class MPSCArrayQueue<E> implements BlockingRingBuffer<E>{

    private final E[] buffer;
    private volatile int writeIndex;
    private final AtomicInteger readIndex;
    private final int capacity;
    private final Object writeLock = new Object();
    private final Object notFull = new Object();
    private volatile boolean closed = false;


    @SuppressWarnings("unchecked")
    public MPSCArrayQueue(int capacity) {
        this.buffer = (E[]) new Object[capacity];
        this.writeIndex = 0;
        this.readIndex = new AtomicInteger(0);
        this.capacity = capacity;
    }

    @Override
    public boolean put(E element) {
        synchronized (writeLock){
            while(isFull() && !closed) {
                synchronized (notFull) {
                    try {
                        notFull.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return false;
                    }
                }
            }
            buffer[writeIndex] = element;
            writeIndex = (writeIndex + 1) % capacity;
            return true;
        }
    }

    @Override
    public E take() {
        while(isEmpty()) {
            Thread.onSpinWait();
        }
        int currentIndex = readIndex.getAndUpdate(i -> (i + 1) % capacity);
        E element = buffer[currentIndex];
        buffer[currentIndex] = null;
        //readIndex = (readIndex + 1) % capacity;
        synchronized (notFull) {
            notFull.notify();
        }
        return element;
    }

    @Override
    public boolean offer(E element) {
        return false;
    }

    @Override
    public E poll() {
        return null;
    }

    @Override
    public boolean isEmpty() {
        return writeIndex == readIndex.get();
    }

    @Override
    public boolean isFull() {
        return (writeIndex + 1) % capacity == readIndex.get();
    }

    @Override
    public RingBufferState state() {
        return null;
    }

    @Override
    public Iterator<E> iterator() {
        return null;
    }
}
