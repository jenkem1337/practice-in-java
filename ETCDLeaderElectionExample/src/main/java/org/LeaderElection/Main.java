package org.LeaderElection;
import io.etcd.jetcd.Client;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final String etcdEndpoint = System.getenv("ETCD_URL");
        System.out.println(etcdEndpoint);
        final Client etcdClient = Client.builder().
                target(etcdEndpoint)
                .build();
        var election = new LeaderElectionManager(
                etcdClient,
                System.getenv("LEADER_ELECTION_KEY"),
                UUID.randomUUID().toString());
        election.startElection();
        Job<Void, Void> helloJob = new HelloWorldJob(election);
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            helloJob.handle(null);
        }, 0, 5, TimeUnit.SECONDS);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutdown Hook running !!");
            try {
                election.shutdown();
                etcdClient.close();
            } catch (Exception e) {
                System.out.println("Shutdown Hook Error : " + e.getMessage());
            }
            System.out.println("Cleanup completed !!");
        }));
    }
}