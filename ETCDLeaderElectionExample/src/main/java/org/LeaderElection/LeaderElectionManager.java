package org.LeaderElection;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.Election;
import io.etcd.jetcd.Lease;
import io.etcd.jetcd.election.LeaderKey;
import io.etcd.jetcd.election.LeaderResponse;

import java.nio.charset.StandardCharsets;
import java.sql.Time;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class LeaderElectionManager{
    private final String electionName;
    private final Client etcdClient;
    private final static int leaseTTL = 10;
    private final String nodeId;
    private final Election electionClient;
    private final Lease leaseClient;
    private final ScheduledExecutorService electionScheduler;

    private final AtomicReference<String> leaderNode;
    private final AtomicReference<LeaderKey> leaderKey;
    private final AtomicBoolean isLeader;
    private final AtomicBoolean isShutdown;
    private final AtomicBoolean isElectionRunning;

    public LeaderElectionManager(Client etcdClient , String electionName, String nodeId) throws ExecutionException, InterruptedException {
        this.electionName = electionName;
        this.etcdClient = etcdClient;
        this.nodeId = nodeId;

        electionScheduler = Executors.newSingleThreadScheduledExecutor( (runnable) -> {
            Thread backgroundElectionThread = new Thread(runnable, "LeaderElection - " + nodeId);
            backgroundElectionThread.setDaemon(true);
            return backgroundElectionThread;
        });

        electionClient = this.etcdClient.getElectionClient();
        leaseClient = this.etcdClient.getLeaseClient();

        isLeader = new AtomicBoolean(false);
        isShutdown = new AtomicBoolean(false);
        isElectionRunning = new AtomicBoolean(false);
        leaderKey = new AtomicReference<>();
        leaderNode = new AtomicReference<>();

        observe();

    }

    public void startElection() throws ExecutionException, InterruptedException {
        if(isShutdown.get()) {
            throw new IllegalStateException("Leader election manager is already shutdown");
        }
        if(!isElectionRunning.compareAndSet(false, true)) {
            System.out.println("Election is already running for : " + nodeId);
            return;
        }

        electionScheduler.scheduleAtFixedRate(() -> {
                try {
                    LeaderKey currentLeaderKey = leaderKey.get();
                    if(isLeader.get() && currentLeaderKey != null) {
                        renewLease(currentLeaderKey);
                    } else {
                        attemptToBecomeLeader();
                    }
                } catch (ExecutionException | InterruptedException e) {
                    System.err.println("Election task error: " + e.getMessage());
                    handleElectionError();
                }
            },0,5,TimeUnit.SECONDS);

    }

    private void renewLease(LeaderKey currentLeader) {
        try {
            leaseClient.keepAliveOnce(currentLeader.getLease()).get(3, TimeUnit.SECONDS);
            System.out.println("Lease renewed for leader: " + nodeId);

        } catch (Exception e) {
            System.err.println("Failed to renew lease: " + e.getMessage());
            handleElectionError();
        }
    }

    private void handleElectionError() {
        isLeader.set(false);
        leaderKey.set(null);
        leaderNode.set(null);
    }
    private void attemptToBecomeLeader() throws ExecutionException, InterruptedException {
        try {
            var leaseId = leaseClient.grant(leaseTTL)
                    .get(5, TimeUnit.SECONDS).getID();

            var campaignResponse =electionClient.campaign(
                    ByteSequence.from(electionName, StandardCharsets.UTF_8),
                    leaseId,
                    ByteSequence.from(nodeId, StandardCharsets.UTF_8))
                    .get(10, TimeUnit.SECONDS);

            LeaderKey newLeaderKey = campaignResponse.getLeader();
            leaderKey.set(newLeaderKey);

            System.out.println("Successfully became leader: " + nodeId);
        } catch (TimeoutException e) {
            System.err.println("Timeout during campaign attempt: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Failed to become leader: " + e.getMessage());
        }

    }

    private void observe() {
        electionClient.observe(ByteSequence.from(electionName, StandardCharsets.UTF_8), new Election.Listener() {
            @Override
            public void onNext(LeaderResponse leaderResponse) {
                try {
                    var newLeaderNode = leaderResponse.getKv().getValue().toString(StandardCharsets.UTF_8);
                    var previousLeaderNode = leaderNode.getAndSet(newLeaderNode);

                    boolean nowIsLeader = Objects.equals(newLeaderNode, nodeId);
                    isLeader.set(nowIsLeader);
                    if(!Objects.equals(previousLeaderNode, newLeaderNode)){
                        if(previousLeaderNode == null) {
                            System.out.println("Leader node is " + newLeaderNode);
                            return;
                        }
                        System.out.println("Leadership changed from : " + previousLeaderNode + " to : " + newLeaderNode);
                    }
                } catch (Exception e) {
                    System.out.println("onNext Error : " + e.getMessage());
                    handleElectionError();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                System.err.println("Observer onError : " +throwable.getMessage());
                handleElectionError();
            }

            @Override
            public void onCompleted() {
                System.out.println("Observer onCompleted");
            }
        });
    }
    public boolean isLeader() {
        var nowLeaderNode = leaderNode.get();
        return isLeader.get() && Objects.equals(nowLeaderNode, nodeId);
    }
    public void resign() throws ExecutionException, InterruptedException {
        LeaderKey currentLeaderKey = leaderKey.get();
        if(isLeader.get() && currentLeaderKey != null) {
            try {
                electionClient.resign(currentLeaderKey).get(5, TimeUnit.SECONDS);
                System.out.println("Successfully resigned from leadership: " + nodeId);

                isLeader.set(false);
                leaderKey.set(null);

            }catch (Exception e){
                System.out.println("Resign Error : "+ e.getMessage());
            }

        }
    }

    public void shutdown() throws ExecutionException, InterruptedException {
        if(!isShutdown.compareAndSet(false, true)) {
            System.out.println("Leader manager is already shutdown");
            return;
        }
        isElectionRunning.set(false);
        resign();

        try {
            if (!electionScheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                electionScheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            electionScheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }

        System.out.println("LeaderElectionManager shutdown completed for: " + nodeId);
    }
}
