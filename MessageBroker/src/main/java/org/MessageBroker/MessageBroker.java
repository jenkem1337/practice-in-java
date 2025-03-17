package org.MessageBroker;

import java.util.concurrent.*;

public class MessageBroker {
    private ConcurrentMap<String, Topic> topics = new ConcurrentHashMap<>();
    private ConcurrentMap<String, ExecutorService> singleThreadedTopicExecutors = new ConcurrentHashMap<>();
    private static class MessageBrokerStaticHolder {
        private static final MessageBroker INSTANCE = new MessageBroker();
    }

    public static MessageBroker getInstance(){
        return MessageBrokerStaticHolder.INSTANCE;
    }



    public void createTopic(String topicId, String partitionType, int partitionCapacity, int partitionNumber, PartitionLoadBalanceStrategy loadBalanceStrategy ) throws Exception {
        if (topics.containsKey(topicId)) throw new Exception("This topic already exist !");
        Topic topic = new Topic(topicId);
        topic.setPartitionLoadBalanceStrategy(loadBalanceStrategy);
        for(int i = 0; i < partitionNumber; i++) {
            topic.appendPartition(partitionType, partitionCapacity);
        }
        topics.put(topicId, topic);
        singleThreadedTopicExecutors.put(topicId, Executors.newSingleThreadExecutor());
    }

    public void subscribeToTopic(String topicId, Consumer consumer) throws Exception {
        if (!topics.containsKey(topicId)) throw new Exception("This topic already exist !");
        Topic topic = topics.get(topicId);
        topic.subscribe(consumer);
    }
    public void unSubscribeToTopic(String topicId, Consumer consumer) throws Exception {
        if (!topics.containsKey(topicId)) throw new Exception("This topic already exist !");
        Topic topic = topics.get(topicId);
        topic.unsubscribe(consumer);
    }

    public void produceMessage(String topicId, Message message) throws Exception {
        if (!topics.containsKey(topicId)) throw new Exception("This topic already exist !");
        Topic topic = topics.get(topicId);
        ExecutorService executorService = singleThreadedTopicExecutors.get(topicId);
        executorService.execute(() -> topic.put(message));
    }

    public void terminateExecutors(){
        for(ExecutorService executor : singleThreadedTopicExecutors.values()){
            executor.shutdown();
            try {
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
        }
    }
}