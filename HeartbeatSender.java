public class HeartbeatSender implements Runnable {
    private final ServerProcess server;
    private final Monitor monitor;

    public HeartbeatSender(ServerProcess server, Monitor monitor) {
        this.server = server;
        this.monitor = monitor;
    }

    @Override
    public void run() {
        try {
            while (server.running) {
                server.sendHeartbeat();
                Logger.log("Server " + server.id + " sent heartbeat."); // NEW LOG
                server.receiveHeartbeat();
                Logger.log("Server " + server.id + " received heartbeat."); // NEW LOG
                monitor.recordHeartbeat(server.id);
                Thread.sleep(server.heartbeatIntervalMs);
            }
        } catch (InterruptedException e) {
            Logger.log("Heartbeat sender stopped for server " + server.id);
        }
    }
}
