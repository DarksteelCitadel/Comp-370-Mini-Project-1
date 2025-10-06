public class HeartbeatSender implements Runnable {
    private final ServerProcess server; // Server sending heartbeats
    private final Monitor monitor;      // Monitor that records heartbeats

    public HeartbeatSender(ServerProcess server, Monitor monitor) {
        this.server = server;
        this.monitor = monitor;
    }

    @Override
    public void run() {
        try {
            while (server.running) {
                // Send heartbeat from server
                server.sendHeartbeat();
                Logger.log("Server " + server.id + " sent heartbeat."); // Log event

                // Simulate server receiving heartbeat (if needed)
                server.receiveHeartbeat();
                Logger.log("Server " + server.id + " received heartbeat."); // Log event

                // Record heartbeat at monitor
                monitor.recordHeartbeat(server.id);

                // Wait before sending next heartbeat
                Thread.sleep(server.heartbeatIntervalMs);
            }
        } catch (InterruptedException e) {
            Logger.log("Heartbeat sender stopped for server " + server.id);
        }
    }
}

