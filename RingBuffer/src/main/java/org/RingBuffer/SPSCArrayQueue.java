package org.RingBuffer;

import jdk.internal.vm.annotation.Contended;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.LockSupport;

public class SPSCArrayQueue<E> implements BlockingRingBuffer<E>{
    @Contended
    private volatile long writeIndex;
    @Contended
    private volatile long readIndex;

    private final E[] buffer;

    private final int capacity;
    private final int mask;

    private static final VarHandle ARRAY_VH;
    private static final VarHandle WRITE_VH;
    private static final VarHandle READ_VH;

    static {
        try {
            ARRAY_VH = MethodHandles.arrayElementVarHandle(Object[].class);
            WRITE_VH = MethodHandles.lookup().findVarHandle(SPSCArrayQueue.class, "writeIndex", long.class);
            READ_VH = MethodHandles.lookup().findVarHandle(SPSCArrayQueue.class, "readIndex", long.class);
        } catch (ReflectiveOperationException ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    @SuppressWarnings("unchecked")
    public SPSCArrayQueue(int capacity) {
        if (Integer.bitCount(capacity) != 1) {
            throw new IllegalArgumentException("Capacity must be a power of 2");
        }

        this.buffer = (E[]) new Object[capacity];
        this.writeIndex = 0;
        this.readIndex = 0;
        this.capacity = capacity;
        this.mask = this.capacity - 1;

    }

    @Override
    public boolean offer(E e) {
        return true;
    }
    @Override
    public E poll(){
        return null;
    }
    @Override
    public boolean isEmpty() {
        return readIndex==writeIndex;
    }

    @Override
    public boolean isFull() {
        return ((writeIndex + 1) & mask) == readIndex;
    }

    @Override
    public RingBufferState state() {
        return new RingBufferState((int) writeIndex, (int) readIndex, capacity, isFull(), isEmpty());
    }


    @Override
    public Iterator<E> iterator() {
        return new SPSCArrayQueueIterator();
    }

    @Override
    public void put(E element) {
//        while (isFull()) {
//            Thread.onSpinWait();
//            LockSupport.parkNanos(1);
//        }
//        buffer[(int) writeIndex & mask] = element;
//        writeIndex = (writeIndex + 1) & mask;
        // local cache of readSeq to avoid volatile reads every iteration
        long rCache = (long) READ_VH.getAcquire(this);
        while (true) {
            long w = (long) WRITE_VH.getOpaque(this); // cheap read of write sequence
            if (w - rCache < capacity) {
                int idx = (int) (w & mask);
                // publish element with release semantics
                ARRAY_VH.setRelease(buffer, idx, element);
                // then advance write sequence with release
                WRITE_VH.setRelease(this, w + 1);
                return;
            }
            // buffer full: refresh remote and backoff
            rCache = (long) READ_VH.getAcquire(this); // refresh remote readSeq
            // light spin then park if still full
            Thread.onSpinWait();
            LockSupport.parkNanos(1L);
        }

    }

    @Override
    public E take() {
//        while (isEmpty()) {
//            Thread.onSpinWait();
//            LockSupport.parkNanos(1);
//        }
//        E element = buffer[(int) readIndex & mask];
//        readIndex = (readIndex + 1) & mask;
//        return element;
        long wCache = (long) WRITE_VH.getAcquire(this);
        while (true) {
            long r = (long) READ_VH.getOpaque(this);
            if (wCache > r) {
                int idx = (int) (r & mask);
                // read element with acquire semantics
                E e = (E) ARRAY_VH.getAcquire(buffer, idx);
                // help GC: clear slot (optional)
//                ARRAY_VH.setRelease(buffer, idx, null);
                // advance read seq
                READ_VH.setRelease(this, r + 1);
                return e;
            }
            // empty -> refresh and backoff
            wCache = (long) WRITE_VH.getAcquire(this);
            Thread.onSpinWait();
//            LockSupport.parkNanos(1L);
        }

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

    private static void throughput() throws InterruptedException {
        var buffer = new SPSCArrayQueue<Integer>((int)Math.pow(2,16));
        var latch = new CountDownLatch(2);
        final var total = 500_000_000; // Increased for better measurement
        long[] producerTime = new long[1];
        long[] consumerTime = new long[1];

        Thread producer = new Thread(() -> {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

            long start = System.nanoTime();
            for (int i = 0; i < total; i++) {
                buffer.put(i);
            }
            long end = System.nanoTime();
            producerTime[0] = end - start;
            latch.countDown();
        });

        Thread consumer = new Thread(() -> {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

            long start = System.nanoTime();
            for (int i = 0; i < total; i++) {
                buffer.take();
            }
            long end = System.nanoTime();
            consumerTime[0] = end - start;
            latch.countDown();
        });

        long globalStart = System.nanoTime();
        producer.start();
        consumer.start();
        latch.await();
        long globalEnd = System.nanoTime();

        long producerMs = producerTime[0] / 1_000_000;
        long consumerMs = consumerTime[0] / 1_000_000;
        long totalMs = (globalEnd - globalStart) / 1_000_000;

        double producerThroughput = total / (producerTime[0] / 1_000_000_000.0);
        double consumerThroughput = total / (consumerTime[0] / 1_000_000_000.0);
        double systemThroughput = total / (totalMs / 1000.0);

        System.out.println("=== Throughput Test Results ===");
        System.out.println("Producer time (ms): " + producerMs);
        System.out.println("Consumer time (ms): " + consumerMs);
        System.out.println("Total time (ms): " + totalMs);
        System.out.printf("Producer throughput: %.2f ops/sec%n", producerThroughput);
        System.out.printf("Consumer throughput: %.2f ops/sec%n", consumerThroughput);
        System.out.printf("System throughput: %.2f ops/sec%n", systemThroughput);

    }

    private static void latencyTest() throws InterruptedException {
        var queue = new SPSCArrayQueue<Long>((int) Math.pow(2, 16));
        final int warmupRounds = 100_000;
        final int testRounds = 50_000_000;

//         Warmup phase to eliminate JIT compilation effects
//        System.out.println("Warming up...");
//        for (int w = 0; w < 10; w++) {
//            runLatencyRound(queue, warmupRounds);
//        }

        System.out.println("Running latency test...");
        long[] latencies = runLatencyRound(queue, testRounds);

        // Calculate statistics
        Arrays.sort(latencies);
        long min = latencies[0];
        long max = latencies[latencies.length - 1];
        double avg = Arrays.stream(latencies).average().orElse(0);
        long p50 = latencies[latencies.length / 2];
        long p90 = latencies[(int) (latencies.length * 0.9)];
        long p99 = latencies[(int) (latencies.length * 0.99)];
        long p999 = latencies[(int) (latencies.length * 0.999)];

        System.out.printf("Latency Results (nanoseconds):%n");
        System.out.printf("  Min: %d ns (%.2f μs)%n", min, min / 1000.0);
        System.out.printf("  Avg: %.2f ns (%.2f μs)%n", avg, avg / 1000.0);
        System.out.printf("  P50: %d ns (%.2f μs)%n", p50, p50 / 1000.0);
        System.out.printf("  P90: %d ns (%.2f μs)%n", p90, p90 / 1000.0);
        System.out.printf("  P99: %d ns (%.2f μs)%n", p99, p99 / 1000.0);
        System.out.printf("  P99.9: %d ns (%.2f μs)%n", p999, p999 / 1000.0);
        System.out.printf("  Max: %d ns (%.2f μs)%n", max, max / 1000.0);
    }

    private static long[] runLatencyRound(SPSCArrayQueue<Long> queue, int rounds) throws InterruptedException {
        long[] latencies = new long[rounds];
        CountDownLatch latch = new CountDownLatch(2);

        Thread producer = new Thread(() -> {
            try {
                // Pin to CPU core for better performance
                Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

                long next = System.nanoTime();
                for (int i = 0; i < rounds; i++) {
                    long timestamp = System.nanoTime();
                    queue.put(timestamp);

                    // 1 µs pacing
                    next += 1000; // nanoseconds
                    while ((System.nanoTime()) < next) {
                        Thread.onSpinWait(); // aktif bekleme
                    }
                }

            } finally {
                latch.countDown();
            }
        });

        Thread consumer = new Thread(() -> {
            try {
                // Pin to CPU core for better performance
                Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

                for (int i = 0; i < rounds; i++) {
                    long timestamp = queue.take();
                    long now = System.nanoTime();
                    latencies[i] = now - timestamp;
                }
            } finally {
                latch.countDown();
            }
        });

        producer.start();
        consumer.start();
        latch.await();

        return latencies;
    }
    public static void main(String[] args) throws InterruptedException {
        throughput();

//        latencyTest();
    }
}
