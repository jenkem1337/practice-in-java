package org.RingBuffer;

import java.util.Iterator;

public class SPSCArrayQueue<E> implements RingBuffer<E>{
    private final E[] buffer;
    private volatile int writeIndex;
    private volatile int readIndex;
    private final int capacity;

    @SuppressWarnings("unchecked")
    public SPSCArrayQueue(int capacity) {
        this.buffer = (E[]) new Object[capacity];
        this.writeIndex = 0;
        this.readIndex = 0;
        this.capacity = capacity;
    }

    @Override
    public boolean offer(E element) {
        if (isFull()) {

            return false;
        }
        buffer[writeIndex] = element;
        writeIndex = (writeIndex + 1) % capacity;
        return true;
    }

    @Override
    public E poll() {
        if (isEmpty()) {
            Thread.onSpinWait();
        }
        E element = buffer[readIndex];
        readIndex = (readIndex + 1) % capacity;
        return element;

    }

    @Override
    public boolean isEmpty() {
        return readIndex==writeIndex;
    }

    @Override
    public boolean isFull() {
        return (writeIndex + 1) % capacity == readIndex;
    }

    @Override
    public RingBufferState state() {
        return new RingBufferState(writeIndex, readIndex, capacity, isFull(), isEmpty());
    }


    @Override
    public Iterator<E> iterator() {
        return new SPSCArrayQueueIterator();
    }
    private final class SPSCArrayQueueIterator implements Iterator<E> {
        private int cursor = 0;
        @Override
        public boolean hasNext() {
            return cursor < capacity;
        }

        @Override
        public E next() {
            return buffer[cursor++];
        }

    }

}
