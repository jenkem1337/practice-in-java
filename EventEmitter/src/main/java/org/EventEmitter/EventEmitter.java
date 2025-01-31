package org.EventEmitter;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class EventEmitter {
    private static class SingletonHolder {
        private static final EventEmitter INSTANCE = new EventEmitter();
    }
    private  final ConcurrentLinkedQueue<Runnable> dispatchQueue;
    private  final Map<String, List<Consumer<Object>>> callbackHashMap;
    private  final ExecutorService executorService;
    private  volatile boolean  isRunning;

    private EventEmitter() {
        executorService = Executors.newSingleThreadExecutor();
        callbackHashMap =  new ConcurrentHashMap<>();
        dispatchQueue = new ConcurrentLinkedQueue<>();
        isRunning = true;
        executorService.submit(this::processDispatchQueue);
    }

    public <T> void saveEvent(String key, Consumer<T> callback) {
        callbackHashMap.computeIfAbsent(key, k -> new ArrayList<>()).add((Consumer<Object>)callback);
    }
    public void emit(String key, Object callbackCommand) {
        if(!callbackHashMap.containsKey(key)) throw new RuntimeException("Callback key does not exist : " + key);

        callbackHashMap.get(key).forEach(consumer -> {
            dispatchQueue.offer(() -> consumer.accept(callbackCommand));
        });
    }
    public int eventSize(){
        return callbackHashMap.size();
    }
    public static EventEmitter getInstance(){
        return SingletonHolder.INSTANCE;
    }

    private void processDispatchQueue() {
        while (isRunning) {
            Runnable eventTask = dispatchQueue.poll();
            if (eventTask != null) {
                eventTask.run();
            }
        }

    }

    public void shutdownEventEmitter() {
        isRunning = false;

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }

    }
}
