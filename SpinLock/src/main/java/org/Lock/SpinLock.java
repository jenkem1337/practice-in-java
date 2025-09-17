package org.Lock;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
public class SpinLock implements Lock{
    private volatile int flag;
    private static final VarHandle VH_FLAG;

    static {
        try {
            VH_FLAG = MethodHandles.lookup().findVarHandle(SpinLock.class, "flag", int.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void lock() {
        while((int) VH_FLAG.compareAndExchangeAcquire(this, 0, 1) == 1);
    }

    @Override
    public void unlock() {
        VH_FLAG.setRelease(this, 0);
    }
}
