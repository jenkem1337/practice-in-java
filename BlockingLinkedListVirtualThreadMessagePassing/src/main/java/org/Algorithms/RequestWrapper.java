package org.Algorithms;

import java.util.concurrent.CompletableFuture;

public record RequestWrapper(
        RequestMessage request,
        CompletableFuture<ResponseMessage> future
) {
    public RequestWrapper(RequestMessage request) {
        this(request, new CompletableFuture<>());
    }

    public void complete(ResponseMessage request) {
        future.complete(request);
    }
}
