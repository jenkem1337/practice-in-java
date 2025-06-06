package org.ApplicationContext.Server;

public class PlatformThreadStrategy implements ConcurrencyStrategy{

    @Override
    public void execute(Runnable runnable) {
        new Thread(runnable).start();
    }
}
