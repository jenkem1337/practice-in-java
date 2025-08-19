package org.RingBuffer;

public interface BlockingRingBuffer<E> extends RingBuffer<E> {
    boolean put(E element);
    E take();
}
