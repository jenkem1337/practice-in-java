package org.RingBuffer;

public interface BlockingRingBuffer<E> extends RingBuffer<E> {
    void put(E element);
    E take();
}
