import java.io.*;
import java.net.*;

public class Main {
    public static void main(String[] args) throws Exception {
        // Create servers and monitor
        PrimaryServer primary = new PrimaryServer(1, 6000);
        BackupServer backup = new BackupServer(2, 6001);
        Monitor monitor = new Monitor();
        monitor.startMonitorService(7100);

        // Start Admin Interface
        new Thread(() -> {
            AdminInterface admin = new AdminInterface("localhost", 7100);
            admin.start();
        }).start();

        // Register servers
        monitor.registerServer(primary);
        monitor.registerServer(backup);

        // Start heartbeats
        new Thread(new HeartbeatSender(primary, monitor)).start();
        new Thread(new HeartbeatSender(backup, monitor)).start();

        // Start servers in separate threads
        new Thread(() -> {
            try { primary.start(); } catch(Exception e){ e.printStackTrace(); }
        }).start();

        new Thread(() -> {
            try { backup.start(); } catch(Exception e){ e.printStackTrace(); }
        }).start();

        // Monitor thread: detect primary failure and promote backup automatically
        new Thread(() -> {
            while (true) {
                try {
                    monitor.detectFailure(); // monitor handles failover internally
                    Thread.sleep(1500);     // Check every 1.5 seconds
                } catch (InterruptedException e) {
                    break;
                }
            }
        }).start();

        // Simulate primary server failure after 4 seconds
        new Thread(() -> {
            try {
                Thread.sleep(4000);
                System.out.println("[TEST] Stopping primary server to simulate failure.");
                primary.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // Client loop: query monitor and connect to current primary
        while (true) {
            String primaryHost;
            int primaryPort;

            // Get the current primary from monitor
            try (
                Socket monitorSocket = new Socket("localhost", 7100);
                PrintWriter out = new PrintWriter(monitorSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(monitorSocket.getInputStream()))
            ) {
                out.println("GET_PRIMARY");
                String response = in.readLine();
                if (response != null && response.contains(":")) {
                    String[] parts = response.split(":");
                    primaryHost = parts[0];
                    primaryPort = Integer.parseInt(parts[1]);
                } else {
                    System.out.println("[CLIENT] Monitor did not return a valid primary. Retrying...");
                    Thread.sleep(2000);
                    continue;
                }
            } catch (Exception e) {
                System.out.println("[CLIENT] Failed to query monitor: " + e.getMessage());
                Thread.sleep(2000);
                continue;
            }

            // Try to connect to primary until success
            boolean success = false;
            while (!success) {
                try {
                    Client client = new Client(primaryHost, primaryPort);
                    String reply = client.sendRequest("PROCESS");
                    System.out.println("Client received: " + reply);
                    success = true;
                } catch (IOException e) {
                    System.out.println("[CLIENT] Failed to connect to server at " + primaryHost + ":" + primaryPort + ". Retrying in 2s...");
                    Thread.sleep(2000); // Retry delay
                    break; // Re-query monitor for updated primary
                }
            }

            Thread.sleep(2000); // Optional pause before next request
        }
    }
}
