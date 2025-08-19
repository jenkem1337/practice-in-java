package org.WakeUp;

import java.util.concurrent.CompletableFuture;

public record Message(String msg, CompletableFuture<String> future) {
    public void complete(String completedMessage){
        future.complete(completedMessage);
    }
}
