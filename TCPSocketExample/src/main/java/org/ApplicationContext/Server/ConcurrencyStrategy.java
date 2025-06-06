package org.ApplicationContext.Server;

public interface ConcurrencyStrategy {
    public void execute(Runnable runnable);
}
