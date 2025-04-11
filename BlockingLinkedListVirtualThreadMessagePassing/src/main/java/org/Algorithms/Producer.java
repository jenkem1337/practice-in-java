package org.Algorithms;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public interface Producer {
    CompletableFuture<ResponseMessage> sendMessage(Object obj) throws InterruptedException;
    static Producer of(Consumer consumer) {
        return new ProducerReference(consumer);
    };
}
