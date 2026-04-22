package org.lock;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

public class TicketLock implements Lock{
    private long p1, p2, p3, p4, p5, p6, p7;
    private volatile long ticket = 0;
    private long p8, p9, p10, p11, p12, p13, p14;
    private volatile long  turn = 0;
    private long p15, p16, p17, p18, p19, p20, p21;

    private static final VarHandle VH_TICKET;

    static {
        try {
            VH_TICKET = MethodHandles.lookup().findVarHandle(TicketLock.class, "ticket", long.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }
    @Override
    public void lock() {
        long _ticket = (long) VH_TICKET.getAndAdd(this, 1L);
        while(turn != _ticket);
    }

    @Override
    public void unlock() {
        turn = turn + 1;
    }
}
