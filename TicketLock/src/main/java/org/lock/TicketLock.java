package org.lock;

import java.util.concurrent.atomic.AtomicLong;

public class TicketLock implements Lock{
    private final AtomicLong ticket = new AtomicLong();
    private volatile long turn = 0;
    @Override
    public void lock() {
        long _ticket = ticket.getAndIncrement();
        while(turn != _ticket);
    }

    @Override
    public void unlock() {
        turn = turn + 1;
    }
}
