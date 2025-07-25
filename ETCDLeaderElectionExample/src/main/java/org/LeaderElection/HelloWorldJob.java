package org.LeaderElection;

public class HelloWorldJob implements Job<Void, Void>{
    private final LeaderElectionManager leaderElectionManager;

    public HelloWorldJob(LeaderElectionManager leaderElectionManager) {
        this.leaderElectionManager = leaderElectionManager;
    }
    @Override
    public Void handle(Void request) {
        if(leaderElectionManager.isLeader()) {
            System.out.println("Hello world as leader !! ");
        } else {
            System.out.println("I am not leader :( ");
        }
        return null;
    }
}
