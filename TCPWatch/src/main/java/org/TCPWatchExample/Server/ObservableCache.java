package org.TCPWatchExample.Server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ObservableCache {

    private final ConcurrentNavigableMap<String, String> cache;
    private final ConcurrentMap<String ,List<ClientSession>> observers;
    public ObservableCache(ConcurrentNavigableMap<String, String> cache, ConcurrentMap<String ,List<ClientSession>> watchKeyList){
        this.cache = cache;
        this.observers = watchKeyList;
    }
    public void addObserver(String key, ClientSession session) throws IOException {
        observers.compute(key, (k,v) -> {
            if(v == null){
                List<ClientSession> list = new CopyOnWriteArrayList<>();
                list.add(session);
                return list;
            }
            v.add(session);
            return v;
        });
    }
    private void notifyObservers(List<ClientSession> cs, Consumer<ClientSession> cb ) {
        cs.forEach(cb);
    }
    public String get(String key) {
        return cache.get(key);
    }

    public String put(String key, String val) {


        String updatedItem = cache.put(key, val);
        if(observers.containsKey(key)) {
            var observerList = observers.get(key);

            notifyObservers(observerList,(socket) -> {
                socket.send(key + " updated with "+ val);
            });
        }
        return updatedItem;
    }

    public String remove(String key) {
        String deletedItem =  cache.remove(key);
        if(observers.containsKey(key)) {
            var observerList = observers.get(key);

            notifyObservers(observerList,(socket) -> {
                socket.send(key + " deleted");
            });
        }
        return deletedItem;
    }
}
