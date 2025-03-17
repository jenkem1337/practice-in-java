package org.MessageBroker;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinLoadBalanceStrategy implements PartitionLoadBalanceStrategy{
    private final AtomicInteger counter = new AtomicInteger(0);


    @Override
    public Partition loadBalance(List<Partition> partitionList) {
        var partition = partitionList.get( counter.get() % partitionList.size());
        counter.incrementAndGet();
        return partition;
    }
}
