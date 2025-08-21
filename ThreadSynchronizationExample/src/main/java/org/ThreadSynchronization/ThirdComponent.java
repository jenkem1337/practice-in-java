package org.ThreadSynchronization;

public class ThirdComponent implements SynchronizationComponent {
    private final Object mutex = new Object();
    private volatile boolean isExecutable = false;
    @Override
    public void execute() {
        while(!isExecutable){
            synchronized (mutex){
                try {
                    mutex.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        System.out.println("Third component executing !!");
    }


    @Override
    public void notifyMutex() {
        synchronized (mutex) {
            isExecutable = true;
            mutex.notify();
        }
    }
}
