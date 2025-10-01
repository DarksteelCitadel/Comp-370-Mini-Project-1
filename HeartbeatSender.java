

public class HeartbeatSender implements Runnable {
    private final ServerProcess server; // the server that owns this heartbeat

   
    public HeartbeatSender(ServerProcess server) {  // Constructor: link this heartbeat to a specific server
        this.server = server;
    }

    @Override
    public void run() {
        try {
            
            while (true) { // Loop forever (until interrupted)
               
                server.sendHeartbeat();  // Tell the monitor "I’m alive"

               
                Thread.sleep(server.heartbeatIntervalMs); // Wait for the heartbeat interval before sending again
            }
        } catch (InterruptedException e) {
           
            System.out.println("Heartbeat sender stopped for server " + server.id);  // This happens when we stop the server or shut down the heartbeat thread
        }
    }
}