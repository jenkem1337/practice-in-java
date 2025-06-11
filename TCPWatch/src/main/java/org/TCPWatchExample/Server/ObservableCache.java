package org.TCPWatchExample.Server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ObservableCache {

    private final ConcurrentNavigableMap<Integer, String> cache;
    private final List<ClientSession> observers;
    public ObservableCache(ConcurrentNavigableMap<Integer, String> cache){
        this.cache = cache;
        this.observers = new CopyOnWriteArrayList<>();
    }
    public void addObserver(ClientSession session) throws IOException {
        observers.add(session);
    }
    private void notifyObserversWhenItemDeleted(Integer key, String value) {
        observers.forEach((socket) -> {
            socket.send("KEY : " + key + " -- VALUE : " + value + " deleted !!");
        });
    }
    public String get(Integer key) {
        return cache.get(key);
    }

    public String put(Integer key, String val) {

        return cache.put(key, val);
    }

    public String remove(Integer key) {
        String deletedItem =  cache.remove(key);
        notifyObserversWhenItemDeleted(key, deletedItem);
        return deletedItem;
    }
}
