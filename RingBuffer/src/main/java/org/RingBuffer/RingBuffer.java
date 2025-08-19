package org.RingBuffer;

public interface RingBuffer<E> extends Iterable<E> {
    boolean offer(E element);
    E poll();
    boolean isEmpty();
    boolean isFull();
    RingBufferState state();
}
