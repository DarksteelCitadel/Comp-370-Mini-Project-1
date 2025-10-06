import java.io.*;
import java.net.*;

public class Main {
    public static void main(String[] args) throws Exception {
        // ----------------------------
        // Create servers and monitor
        // ----------------------------
        PrimaryServer primary = new PrimaryServer(1, 6000);  // Primary server with ID 1
        BackupServer backup = new BackupServer(2, 6001);     // Backup server with ID 2
        Monitor monitor = new Monitor();                     // Monitor to track servers and handle failover
        monitor.startMonitorService(7100);                  // Start monitor service on port 7100

        // ----------------------------
        // Start Admin Interface in a separate thread
        // ----------------------------
        new Thread(() -> {
            AdminInterface admin = new AdminInterface("localhost", 7100); // Connects to monitor
            admin.start(); // Starts admin interface loop
        }).start();

        // ----------------------------
        // Register servers with the monitor
        // ----------------------------
        monitor.registerServer(primary);
        monitor.registerServer(backup);

        // ----------------------------
        // Start heartbeat threads for servers
        // ----------------------------
        new Thread(new HeartbeatSender(primary, monitor)).start(); // Primary heartbeat
        new Thread(new HeartbeatSender(backup, monitor)).start();  // Backup heartbeat

        // ----------------------------
        // Start server threads to handle client requests
        // ----------------------------
        new Thread(() -> {
            try { primary.start(); } catch(Exception e){ e.printStackTrace(); }
        }).start();

        new Thread(() -> {
            try { backup.start(); } catch(Exception e){ e.printStackTrace(); }
        }).start();

        // ----------------------------
        // Monitor thread: continuously detect primary failure and promote backup
        // ----------------------------
        new Thread(() -> {
            while (true) {
                try {
                    monitor.detectFailure(); // Monitor checks heartbeat and triggers failover if needed
                    Thread.sleep(1500);     // Check every 1.5 seconds
                } catch (InterruptedException e) {
                    break;
                }
            }
        }).start();

        // ----------------------------
        // Simulate primary server failure after 4 seconds
        // ----------------------------
        new Thread(() -> {
            try {
                Thread.sleep(4000); // Wait 4 seconds
                System.out.println("[TEST] Stopping primary server to simulate failure.");
                primary.stop(); // Stop primary to test failover
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // ----------------------------
        // Client loop: continuously query monitor for current primary and send requests
        // ----------------------------
        while (true) {
            String primaryHost;
            int primaryPort;

            // Query monitor to get current primary server
            try (
                Socket monitorSocket = new Socket("localhost", 7100);
                PrintWriter out = new PrintWriter(monitorSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(monitorSocket.getInputStream()))
            ) {
                out.println("GET_PRIMARY");            // Ask monitor for primary
                String response = in.readLine();       // Read response
                if (response != null && response.contains(":")) {
                    String[] parts = response.split(":");
                    primaryHost = parts[0];
                    primaryPort = Integer.parseInt(parts[1]);
                } else {
                    System.out.println("[CLIENT] Monitor did not return a valid primary. Retrying...");
                    Thread.sleep(2000);                // Wait before retry
                    continue;
                }
            } catch (Exception e) {
                System.out.println("[CLIENT] Failed to query monitor: " + e.getMessage());
                Thread.sleep(2000);                    // Retry delay
                continue;
            }

            // Attempt to connect to the primary server until successful
            boolean success = false;
            while (!success) {
                try {
                    Client client = new Client(primaryHost, primaryPort); // Create client to primary
                    String reply = client.sendRequest("PROCESS");         // Send request
                    System.out.println("Client received: " + reply);      // Print response
                    success = true;
                } catch (IOException e) {
                    System.out.println("[CLIENT] Failed to connect to server at " + primaryHost + ":" + primaryPort + ". Retrying in 2s...");
                    Thread.sleep(2000); // Wait before retry
                    break;             // Break to re-query monitor for updated primary
                }
            }

            Thread.sleep(2000); // Optional pause before next request cycle
        }
    }
}

