package org.ThreadSynchronization;

public class SecondComponent implements SynchronizationComponent {
    private final Object mutex = new Object();
    private final SynchronizationComponent thirdComponent;
    private volatile boolean isExecutable = false;

    public SecondComponent(SynchronizationComponent thirdComponent) {
        this.thirdComponent = thirdComponent;
    }

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
        System.out.println("Second component executing !!");
        thirdComponent.notifyMutex();
    }

    @Override
    public void notifyMutex() {
        synchronized (mutex) {
            isExecutable = true;
            mutex.notify();
        }
    }
}
