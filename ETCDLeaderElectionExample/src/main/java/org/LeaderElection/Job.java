package org.LeaderElection;

public interface Job<K,L>{
    public K handle(L request);
}
