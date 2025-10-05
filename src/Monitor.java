import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.*;

public class Monitor {
    private List<ServerProcess> servers = new ArrayList<>();
    private int timeoutThresholdMs = 5000;
    private Map<Integer, Long> heartbeatTimestamps = new HashMap<>();
    private Integer currentPrimaryId = null;
    private boolean promotedMessagePrinted = false;

    public void registerServer(ServerProcess server) {
        servers.add(server);
        Logger.log("Monitor registered server " + server.id);
        heartbeatTimestamps.put(server.id, System.currentTimeMillis());
        if (server instanceof PrimaryServer && currentPrimaryId == null) {
            currentPrimaryId = server.id;
        }
    }

    public void startMonitorService(int monitorPort) {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(monitorPort)) {
                Logger.log("Monitor service started on port " + monitorPort);
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    String request = in.readLine();
                    if ("GET_PRIMARY".equals(request)) {
                        ServerProcess primary = null;
                        for (ServerProcess server : servers) {
                            if (server.id == currentPrimaryId) primary = server;
                        }
                        if (primary != null) {
                            out.println("localhost:" + primary.port);
                        } else {
                            out.println("NONE");
                        }
                    }
                    clientSocket.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public synchronized void recordHeartbeat(int serverId) {
        heartbeatTimestamps.put(serverId, System.currentTimeMillis());
        Logger.log("Monitor received heartbeat from server " + serverId);
    }

    public void detectFailure() {
        if (currentPrimaryId != null) {
            Long lastPrimaryHeartbeat = heartbeatTimestamps.get(currentPrimaryId);
            long now = System.currentTimeMillis();
            ServerProcess primary = null;
            for (ServerProcess server : servers) if (server.id == currentPrimaryId) primary = server;

            boolean isBackupPromoted = (primary instanceof BackupServer) && ((BackupServer) primary).isPromoted;
            if (isBackupPromoted) {
                if (!promotedMessagePrinted) {
                    Logger.log("Backup server " + currentPrimaryId + " is now acting as primary.");
                    promotedMessagePrinted = true;
                }
                return;
            }

            if (lastPrimaryHeartbeat == null || now - lastPrimaryHeartbeat > timeoutThresholdMs) {
                logEvent("Primary server " + currentPrimaryId + " failed. Initiating failover.");
                triggerFailover();
            }
        }
    }

    public void triggerFailover() {
        long now = System.currentTimeMillis();
        BackupServer selectedBackup = null;
        for (ServerProcess server : servers) {
            if (server instanceof BackupServer) {
                Long ts = heartbeatTimestamps.get(server.id);
                if (ts != null && now - ts <= timeoutThresholdMs && !((BackupServer) server).isPromoted) {
                    BackupServer backup = (BackupServer) server;
                    if (selectedBackup == null || backup.id < selectedBackup.id) {
                        selectedBackup = backup;
                    }
                }
            }
        }

        if (selectedBackup != null) {
            selectedBackup.promote();
            currentPrimaryId = selectedBackup.id;
            promotedMessagePrinted = false;
            logEvent("Monitor triggered failover. Server " + selectedBackup.id + " is now primary.");
            System.out.println("[VISUALIZE] Backup server " + selectedBackup.id + " is now handling client requests on port " + selectedBackup.port);

            final BackupServer backupToPromote = selectedBackup;
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    Client client = new Client("localhost", backupToPromote.port);
                    client.sendRequest("Hello Backup (now Primary)!");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            logEvent("No alive backup servers available for failover.");
        }
    }

    public void logEvent(String event) {
        Logger.log("Monitor log: " + event);
    }

    public void printStatus() {
        Logger.log("=== Server Status ===");
        for (ServerProcess server : servers) {
            String status = server.running ? "RUNNING" : "STOPPED";
            if (server instanceof BackupServer && ((BackupServer) server).isPromoted) status += " (PROMOTED)";
            Logger.log("Server " + server.id + ": " + status);
        }
    }
}

