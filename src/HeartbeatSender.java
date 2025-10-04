

public class HeartbeatSender implements Runnable {
    private final ServerProcess server; // the server that owns this heartbeat
    private Monitor monitor;
   
    public HeartbeatSender(ServerProcess server, Monitor monitor) {  // Constructor: link this heartbeat to a specific server
        this.monitor = monitor;
        this.server = server;
    }

    @Override
    //Instead if just printing messages, actually update the monitor
    public void run() {
        try {
            
            while (server.running) { // Loop forever (until interrupted)
                monitor.receiveHeartbeat(server.id); //update monitor
                Thread.sleep(2000); //send heartbeat every 2s
            }
        } catch (InterruptedException e) {
           
            System.out.println("Heartbeat sender stopped for server " + server.id);  // This happens when we stop the server or shut down the heartbeat thread
        }
    }
}