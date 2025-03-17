package org.MessageBroker;

import java.util.List;

public interface PartitionLoadBalanceStrategy {
    Partition loadBalance(List<Partition> partitionList);

}
