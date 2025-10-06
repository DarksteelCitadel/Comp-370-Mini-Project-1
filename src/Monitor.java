import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.*;

public class Monitor {

    // List of all registered servers (primary and backups)
    private List<ServerProcess> servers = new ArrayList<>();
    
    // Heartbeat timeout in milliseconds; if primary misses this, failover triggers
    private int timeoutThresholdMs = 5000;
    
    // Map of server ID to last heartbeat timestamp
    private Map<Integer, Long> heartbeatTimestamps = new HashMap<>();
    
    // ID of the currently active primary server
    private Integer currentPrimaryId = null;
    
    // Flag to prevent printing promotion log multiple times
    private boolean promotedMessagePrinted = false;

    // Register a new server with the monitor
    public void registerServer(ServerProcess server) {
        servers.add(server);
        Logger.log("Monitor registered server " + server.id);
        heartbeatTimestamps.put(server.id, System.currentTimeMillis());

        // If this is the first primary server, set as current primary
        if (server instanceof PrimaryServer && currentPrimaryId == null) {
            currentPrimaryId = server.id;
        }
    }

    // Start a service to accept client/admin/server requests on a given port
    public void startMonitorService(int monitorPort) {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(monitorPort)) {
                Logger.log("Monitor service started on port " + monitorPort);
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    String request = in.readLine();

                    if (request == null) continue;

                    // ---------------------------
                    // Handle server registration
                    // Format: REGISTER <id> <port> <type>
                    // ---------------------------
                    if (request.startsWith("REGISTER")) {
                        String[] parts = request.split(" ");
                        int serverId = Integer.parseInt(parts[1]);
                        int serverPort = Integer.parseInt(parts[2]);
                        String type = parts[3];

                        ServerProcess server;
                        if ("PRIMARY".equalsIgnoreCase(type)) {
                            server = new PrimaryServer(serverId, serverPort);
                        } else {
                            server = new BackupServer(serverId, serverPort);
                        }

                        registerServer(server);
                        out.println("Registered server " + serverId);
                    } 
                    // ---------------------------

                    // Handle heartbeat messages from servers
                    else if (request.startsWith("HEARTBEAT")) {
                        int serverId = Integer.parseInt(request.split(" ")[1]);
                        recordHeartbeat(serverId); // update timestamp
                    } 
                    // Return the status of all servers
                    else if ("STATUS".equalsIgnoreCase(request)) {
                        StringBuilder sb = new StringBuilder();
                        for (ServerProcess server : servers) {
                            String status = server.running ? "RUNNING" : "STOPPED";
                            if (server instanceof BackupServer && ((BackupServer) server).isPromoted) {
                                status += " (PROMOTED)";
                            }
                            sb.append("Server ").append(server.id).append(": ").append(status).append("\n");
                        }
                        out.println(sb.toString());
                    } 
                    // Trigger manual failover
                    else if ("FAILOVER".equalsIgnoreCase(request)) {
                        triggerFailover();
                        out.println("Failover attempted.");
                    } 
                    // Return current primary server info
                    else if ("GET_PRIMARY".equalsIgnoreCase(request)) {
                        ServerProcess primary = null;
                        for (ServerProcess server : servers) if (server.id == currentPrimaryId) primary = server;
                        out.println(primary != null ? "localhost:" + primary.port : "NONE");
                    }

                    clientSocket.close(); // close connection
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Record a heartbeat received from a server
    public synchronized void recordHeartbeat(int serverId) {
        heartbeatTimestamps.put(serverId, System.currentTimeMillis());
        Logger.log("Monitor received heartbeat from server " + serverId);
    }

    // Check if primary has failed, trigger failover if needed
    public void detectFailure() {
        if (currentPrimaryId != null) {
            Long lastPrimaryHeartbeat = heartbeatTimestamps.get(currentPrimaryId);
            long now = System.currentTimeMillis();

            // Find current primary server object
            ServerProcess primary = null;
            for (ServerProcess server : servers) if (server.id == currentPrimaryId) primary = server;

            // If backup is already promoted, log once
            boolean isBackupPromoted = (primary instanceof BackupServer) && ((BackupServer) primary).isPromoted;
            if (isBackupPromoted) {
                if (!promotedMessagePrinted) {
                    Logger.log("Backup server " + currentPrimaryId + " is now acting as primary.");
                    promotedMessagePrinted = true;
                }
                return;
            }

            // If heartbeat timeout exceeded, initiate failover
            if (lastPrimaryHeartbeat == null || now - lastPrimaryHeartbeat > timeoutThresholdMs) {
                logEvent("Primary server " + currentPrimaryId + " failed. Initiating failover.");
                triggerFailover();
            }
        }
    }

    // Promote a backup server to primary deterministically (lowest ID first)
    public void triggerFailover() {
        long now = System.currentTimeMillis();
        BackupServer selectedBackup = null;

        for (ServerProcess server : servers) {
            if (server instanceof BackupServer) {
                Long ts = heartbeatTimestamps.get(server.id);
                // Check server is alive and not already promoted
                if (ts != null && now - ts <= timeoutThresholdMs && !((BackupServer) server).isPromoted) {
                    BackupServer backup = (BackupServer) server;
                    if (selectedBackup == null || backup.id < selectedBackup.id) {
                        selectedBackup = backup;
                    }
                }
            }
        }

        if (selectedBackup != null) {
            selectedBackup.promote(); // promote to primary
            currentPrimaryId = selectedBackup.id;
            promotedMessagePrinted = false;
            logEvent("Monitor triggered failover. Server " + selectedBackup.id + " is now primary.");

            System.out.println("[VISUALIZE] Backup server " + selectedBackup.id + " is now handling client requests on port " + selectedBackup.port);

            // Optional: send test request to newly promoted backup
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

    // Helper to log events from monitor
    public void logEvent(String event) {
        Logger.log("Monitor log: " + event);
    }

    // Print current server status
    public void printStatus() {
        Logger.log("=== Server Status ===");
        for (ServerProcess server : servers) {
            String status = server.running ? "RUNNING" : "STOPPED";
            if (server instanceof BackupServer && ((BackupServer) server).isPromoted) status += " (PROMOTED)";
            Logger.log("Server " + server.id + ": " + status);
        }
    }

    // Main method to start monitor service
    public static void main(String[] args) {
        int monitorPort = 7100; // default port
        if (args.length > 0) {
            try {
                monitorPort = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid port argument, using default 7100");
            }
        }

        Monitor monitor = new Monitor();
        monitor.startMonitorService(monitorPort);
        System.out.println("Monitor service started on port " + monitorPort);

        // Keep the main thread alive
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}









