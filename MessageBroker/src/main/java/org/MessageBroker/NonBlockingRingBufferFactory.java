package org.MessageBroker;

public class NonBlockingRingBufferFactory implements PartitionFactory{
    @Override
    public Partition create(int size){
        return new NonBlockingRingBuffer(size);
    }
}
