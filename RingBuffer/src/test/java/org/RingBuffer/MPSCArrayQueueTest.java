package org.RingBuffer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

class MPSCArrayQueueTest {
    BlockingRingBuffer<Integer> queue;
    final static int CAPACITY = 10;
    @BeforeEach
    void setUp(){
        queue = new MPSCArrayQueue<>(CAPACITY);
    }
    @Test
    @Timeout(10)
    void testMultipleProducers() throws InterruptedException {
        final int NUM_PRODUCERS = 5;
        final int ELEMENTS_PER_PRODUCER = 20;
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch doneLatch = new CountDownLatch(NUM_PRODUCERS + 1);

        final List<Integer> producedElements = Collections.synchronizedList(new ArrayList<>());
        final List<Integer> consumedElements = Collections.synchronizedList(new ArrayList<>());

        // Start producers
        for (int p = 0; p < NUM_PRODUCERS; p++) {
            final int producerId = p;
            new Thread(() -> {
                try {
                    startLatch.await();
                    for (int i = 0; i < ELEMENTS_PER_PRODUCER; i++) {
                        int element = producerId * 1000 + i; // Unique elements per producer
                        queue.put(element);
                        producedElements.add(element);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            }).start();
        }

        // Start consumer
        new Thread(() -> {
            try {
                try {
                    startLatch.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                for (int i = 0; i < NUM_PRODUCERS * ELEMENTS_PER_PRODUCER; i++) {
                    Integer element = queue.take();
                    consumedElements.add(element);
                }
            } finally {
                doneLatch.countDown();
            }
        }).start();

        startLatch.countDown(); // Start all threads
        doneLatch.await(); // Wait for completion

        // Verify all elements were consumed
        assertEquals(NUM_PRODUCERS * ELEMENTS_PER_PRODUCER, consumedElements.size());

        // Sort both lists and compare
        Collections.sort(producedElements);
        Collections.sort(consumedElements);
        assertEquals(producedElements, consumedElements);
    }

}