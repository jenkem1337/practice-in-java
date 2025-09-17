package org.Lock;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Counter {
    private int count = 0;
    private final Lock lock;
    public Counter(Lock lock) {
        this.lock = lock;
    }

    public void increment() {
        count++;
    }

    public void incrementSync() {
        lock.lock();
        try {
            count++;
        } finally {
            lock.unlock();
        }
    }
    public int get(){
        return count;
    }
}
