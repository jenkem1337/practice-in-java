package org.MessageBroker;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Topic {
    private final String identifier;
    private final List<Partition> partitionList;
    private final List<Consumer> consumers;
    private final ConcurrentMap<String, PartitionFactory> partitionFactory;

    private PartitionLoadBalanceStrategy loadBalanceStrategy;

    public Topic(String identifier) {
        this.identifier = identifier;
        this.partitionFactory = new ConcurrentHashMap<>();
        this.partitionList = new CopyOnWriteArrayList<>();
        this.consumers = new CopyOnWriteArrayList<>();

        partitionFactory.put(NonBlockingRingBuffer.class.getSimpleName(), new NonBlockingRingBufferFactory());
    }

    public String getIdentifier() {
        return identifier;
    }
    void appendPartition(String partitionType, int partitionCapacity){
        if (!partitionFactory.containsKey(partitionType)) throw new RuntimeException("This partition does not exist !");
        partitionList.add(partitionFactory.get(partitionType).create(partitionCapacity));
        if (!consumers.isEmpty()){
            rebalanceConsumersToPartitions();
        }

    }
    void subscribe(Consumer consumer) {
        consumers.add(consumer);
        rebalanceConsumersToPartitions();
    }

    void unsubscribe(Consumer consumer) {
        consumers.remove(consumer);
        rebalanceConsumersToPartitions();
    }
    void setPartitionLoadBalanceStrategy(PartitionLoadBalanceStrategy strategy){
        loadBalanceStrategy = strategy;
    }

    private void rebalanceConsumersToPartitions(){
        if (consumers.isEmpty()){
             return;
        }

        int index = 0;
        for (Partition partition : partitionList) {
            Consumer consumer = consumers.get(index % consumers.size());
            partition.setConsumer(consumer);
            index++;
        }

    }

    void put(Message message){
        var partition = loadBalanceStrategy.loadBalance(partitionList);
        var consumer = partition.getConsumer();
        partition.put(message);
        consumer.onMessageWritten(partition);
    }
}
