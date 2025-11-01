package org.Clock;


public class LamportClock {
    private long p1,p2,p3,p4,p5,p6,p7;
    private long version = 0;
    private long p8,p9,p10,p11,p12,p13,p14,p15;

    public void increment(){
            version++;
    }

    public long incrementAndGet() {
            version += 1;
            return version;
    }

    public void receive(long otherNodeVersion) {
            version = Math.max(version, otherNodeVersion) + 1;
    }

    public long version() {return version;}

}
