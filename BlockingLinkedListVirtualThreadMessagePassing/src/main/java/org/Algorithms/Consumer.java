package org.Algorithms;

public interface Consumer {
    ResponseMessage onMessage(RequestMessage type) throws InterruptedException;
}
