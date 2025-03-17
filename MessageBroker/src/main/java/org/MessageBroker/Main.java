package org.MessageBroker;

public class Main {
    public record Data(String data){}
    public static void main(String[] args) throws Exception {
        MessageBroker messageBroker = MessageBroker.getInstance();
        messageBroker.createTopic("topic-merhaba", "NonBlockingRingBuffer", 10, 3, new RoundRobinLoadBalanceStrategy());

        messageBroker.createTopic("topic-hello", "NonBlockingRingBuffer", 10, 3, new RoundRobinLoadBalanceStrategy());

        Consumer consumer = (partition) -> System.out.println("Merhaab ben 1. consumer ve gelen mesaj -> " + partition.get());
        Consumer consumer2 = (partition) -> System.out.println("Merhaab ben 2. consumer ve gelen mesaj -> " + partition.get());

        messageBroker.subscribeToTopic("topic-merhaba", consumer);
        messageBroker.subscribeToTopic("topic-merhaba", consumer2);

        messageBroker.subscribeToTopic("topic-hello", consumer);

        messageBroker.produceMessage("topic-merhaba", new Message("Hello world !!"));
        messageBroker.produceMessage("topic-merhaba", new Message("Merhaba dunya !!"));
        messageBroker.produceMessage("topic-hello", new Message("Hello Topic message !!"));

        messageBroker.produceMessage("topic-merhaba", new Message("Hola Mundo !!"));

        messageBroker.produceMessage("topic-hello", new Message("Hello Topic message !!"));

        System.out.println("asdnfajndasjka nmasdjkasndasjkd");
        messageBroker.terminateExecutors();
    }
}