

import java.util.List;
import java.util.ArrayList;


public class Monitor {
    private List<ServerProcess> servers = new ArrayList<>(); // list of all servers being monitored
    private int timeoutThresholdMs = 5000; // how long to wait before deciding a server failed

    
    public void registerServer(ServerProcess server) { // Add a server to monitor
        servers.add(server);
        System.out.println("Monitor registered server " + server.id);
    }

 
    public void detectFailure() {    // Check all servers to see if they are running
        for (ServerProcess server : servers) {

            System.out.println("Monitor checking server " + server.id + " status: " + server.running);   
        }
    }

   
    public void triggerFailover() {  // Promote a backup server to primary if a failure is detected
        for (ServerProcess server : servers) {
            if (server instanceof BackupServer) {
                ((BackupServer) server).promote(); // promote backup server
                System.out.println("Monitor triggered failover. Server " + server.id + " is now primary.");
                break; // only promote the first backup found
            }
        }
    }


    public void logEvent(String event) {     // Log any important events
        System.out.println("Monitor log: " + event);
    }
}
