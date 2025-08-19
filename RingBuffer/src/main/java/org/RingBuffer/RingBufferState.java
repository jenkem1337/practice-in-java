package org.RingBuffer;

public record RingBufferState(
        int writeIndex,
        int readIndex,
        int capacity,
        boolean isFull,
        boolean isEmpty) {
}
