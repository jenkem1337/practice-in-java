package org.RingBuffer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ArrayQueueTest {

    RingBuffer<String> buffer;
    @BeforeEach
    void setUp(){
        buffer = new ArrayQueue<>(3);
    }
    @Test
    void offer(){
        boolean response = buffer.offer("Hello");
        assertThat(response).isTrue();
    }

    @Test
    void offer_ReturnFalseWhenBufferOverflow(){
        buffer.offer("Hello");
        buffer.offer("World");
        boolean response = buffer.offer("Merhaba");
        assertThat(response).isFalse();
    }

    @Test
    void poll(){
        buffer.offer("Hello");
        String response = buffer.poll();
        assertThat(response).isNotNull();
        assertThat(response).isEqualTo("Hello");
    }

    @Test
    void poll_ReturnNullWhenQueueIsEmpty(){
        String response = buffer.poll();
        assertThat(response).isNull();
    }
}