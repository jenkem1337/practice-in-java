package org.ThreadSynchronization;

public class FirstComponent implements SynchronizationComponent{
    private final SynchronizationComponent secondComponent;

    public FirstComponent(SynchronizationComponent secondComponent) {
        this.secondComponent = secondComponent;
    }

    @Override
    public void execute() {
        System.out.println("First component executing !!");
        secondComponent.notifyMutex();

    }


    @Override
    public void notifyMutex() {

    }
}
