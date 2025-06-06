package org.ApplicationContext.Server;

public class VirtualThreadStrategy implements ConcurrencyStrategy{

    @Override
    public void execute(Runnable runnable) {
        Thread.ofVirtual().start(runnable);
    }
}
