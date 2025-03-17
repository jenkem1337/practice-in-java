package org.MessageBroker;

public interface Consumer {
    void onMessageWritten(ConsumablePartition consumablePartition);

}
