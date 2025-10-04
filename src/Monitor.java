

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;


public class Monitor {
    private List<ServerProcess> servers = new ArrayList<>(); // list of all servers being monitored
    private Map<Integer, Long> serverHeartbeats = new HashMap<>(); //Hash map to track last time heartbeat arrived from each server (Int:id, Long:timestamp)
    private int timeoutThresholdMs = 5000; // how long to wait before deciding a server failed
    private int currentPrimaryId = 1; //assume server 1 starts as primary
    
    public void registerServer(ServerProcess server) { // Add a server to monitor
        servers.add(server);
        serverHeartbeats.put(server.id, System.currentTimeMillis()); //initialize heartbeat tracking
        System.out.println("Monitor registered server " + server.id);
    }

    //method for servers to report a heartbeat to monitor
    public void receiveHeartbeat(int serverId) {
        serverHeartbeats.put(serverId, System.currentTimeMillis());
        System.out.println("Monitor received heartbeat from server " + serverId);
    }

 
    public void detectFailure() {    // Check all servers to see if they are running
        long now = System.currentTimeMillis();
        Long lastHeartbeat = serverHeartbeats.get(currentPrimaryId);

        //if last heartbeat was longer than 5 seconds ago, promote backup server
        if(lastHeartbeat == null || (now - lastHeartbeat) > timeoutThresholdMs) {
            System.out.println("Monitor detected failure of primary " + currentPrimaryId);
            triggerFailover();
        }
    }

   //UPDATED: added choosing server of lowest id, then promoting it to primary
    public void triggerFailover() {  // Promote a backup server to primary if a failure is detected
        long now = System.currentTimeMillis();
        ServerProcess bestCandidate = null;

        for (ServerProcess server : servers) {
            /*
            if (server instanceof BackupServer) {
                ((BackupServer) server).promote(); // promote backup server
                System.out.println("Monitor triggered failover. Server " + server.id + " is now primary.");
                break; // only promote the first backup found
            }
            */
            if(server.id == currentPrimaryId) continue; //skip current primary

            //check if server is alive
            Long lastHeartbeat = serverHeartbeats.get(server.id);
            if (lastHeartbeat == null) continue;

            //if sever of lower id is found, swap
            if((now - lastHeartbeat) < timeoutThresholdMs) {
                if (bestCandidate == null || server.id < bestCandidate.id) {
                    bestCandidate = server;
                }
            }

            //promote backup server to primary after finding alive server of lowest id
            if (bestCandidate != null && bestCandidate instanceof BackupServer) {
                ((BackupServer) bestCandidate).promote();
                currentPrimaryId = bestCandidate.id;
                System.out.println("Monitor promoted server " + bestCandidate.id + " to PRIMARY.");
            } else {
                System.out.println("No backup servers available to promote!");
            }
        }
    }


    public void logEvent(String event) {     // Log any important events
        System.out.println("Monitor log: " + event);
    }
}
