package org.EventEmitter;


import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class EventEmitter {
    private static class SingletonHolder {
        private static final EventEmitter INSTANCE = new EventEmitter();
    }

    private final Map<String, List<Consumer<Object>>> callbackHashMap =  new ConcurrentHashMap<>();
    private  ExecutorService executorService;
    private EventEmitter() {
        executorService = Executors.newVirtualThreadPerTaskExecutor();
    }


    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public <T> void saveEvent(String key, Consumer<T> callback) {
        if (callbackHashMap.containsKey(key)) {
            List<Consumer<Object>> callbacks = callbackHashMap.get(key);

            callbacks.add((Consumer<Object>) callback);
            callbackHashMap.put(key, callbacks);
            return;
        }

        List<Consumer<Object>> recordList = new CopyOnWriteArrayList<>();
        recordList.add((Consumer<Object>) callback);
        callbackHashMap.put(key, recordList);
    }
    public void emit(String key, Object callbackCommand) {
        if(!callbackHashMap.containsKey(key)) throw new RuntimeException("Callback key does not exist : " + key);

        callbackHashMap.get(key).forEach(consumer -> {
            executorService.submit(() -> consumer.accept(callbackCommand));
        });
    }
    public int eventSize(){
        return callbackHashMap.size();
    }
    public static EventEmitter getInstance(){
        return SingletonHolder.INSTANCE;
    }

    public void shutdownEventEmitter() {
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
