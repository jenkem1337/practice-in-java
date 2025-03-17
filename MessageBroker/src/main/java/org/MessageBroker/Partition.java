package org.MessageBroker;

public interface Partition extends  ConsumablePartition {
    int put(Message item);
    Consumer getConsumer();
    void setConsumer(Consumer consumer);
}
