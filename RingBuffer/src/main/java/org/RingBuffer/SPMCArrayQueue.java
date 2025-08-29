package org.RingBuffer;

import java.util.Iterator;

public class SPMCArrayQueue<E> implements BlockingRingBuffer<E>{
    private final E[] buffer;
    private volatile int writeIndex;
    private volatile int readIndex;
    //private final AtomicInteger readIndex;
    private final int capacity;
    private final Object takeLock = new Object();
    private final Object isNotEmpty = new Object();
    private volatile boolean closed = false;


    @SuppressWarnings("unchecked")
    public SPMCArrayQueue(int capacity) {
        this.buffer = (E[]) new Object[capacity];
        this.writeIndex = 0;
        this.readIndex = 0;
        //this.readIndex = new AtomicInteger(0);
        this.capacity = capacity;
    }

    @Override
    public void put(E element) {
        while(isFull()) {
            Thread.onSpinWait();
        }
        buffer[writeIndex] = element;
        writeIndex = (writeIndex + 1) % capacity;
        synchronized (isNotEmpty) {
            isNotEmpty.notifyAll();
        }

    }

    @Override
    public  E take() {
        synchronized (takeLock) {
            while (isEmpty() && !closed) {
                synchronized (isNotEmpty){
                    try {
                        isNotEmpty.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return null;
                    }
                }
            }
            //int currentReadIndex = readIndex.getAndUpdate(i -> (i + 1) % capacity);
            //E element = buffer[currentReadIndex];
            E element = buffer[readIndex];
            buffer[readIndex] = null;
            readIndex = (readIndex + 1) % capacity;
            return element;
        }
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
        return readIndex == writeIndex;
        //return readIndex.get() == writeIndex;
    }

    @Override
    public boolean isFull() {
        //return (writeIndex + 1) % capacity == readIndex.get();
        return (writeIndex + 1) % capacity == readIndex;

    }

    @Override
    public RingBufferState state() {
        return new RingBufferState(writeIndex, readIndex, capacity, isFull(), isEmpty());
    }

    @Override
    public Iterator<E> iterator() {
        return new SPMCArrayQueueIterator();
    }
    private final class SPMCArrayQueueIterator implements Iterator<E> {
        private int cursor = 0;
        @Override
        public boolean hasNext() {
            return cursor < capacity ;
        }

        @Override
        public E next() {
            return buffer[cursor++];
        }
    }
}
