package org.MessageBroker;

public interface PartitionFactory {
    Partition create(int size);
}
