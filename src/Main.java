
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

// import java.io.BufferedReader;
// import java.io.InputStreamReader;
// import java.io.PrintWriter;
// import java.net.Socket;

public class Main {
    public static void main(String[] args) throws Exception {
        // Create servers with new ports
        PrimaryServer primary = new PrimaryServer(1, 6000);   // changed from 5000
        BackupServer backup = new BackupServer(2, 6001);      // changed from 5001
        Monitor monitor = new Monitor();
        monitor.startMonitorService(7100);
        
 new Thread(() -> {
            AdminInterface admin = new AdminInterface("localhost", 7100);
            admin.start();
        }).start();
        
        monitor.registerServer(primary);
        monitor.registerServer(backup);

        // Start heartbeats
        new Thread(new HeartbeatSender(primary, monitor)).start();
        new Thread(new HeartbeatSender(backup, monitor)).start();

        // Start servers
        new Thread(() -> {
            try { primary.start(); } catch(Exception e){ e.printStackTrace(); }
        }).start();

        new Thread(() -> {
            try { backup.start(); } catch(Exception e){ e.printStackTrace(); }
        }).start();

        // Periodically check for primary failure
        new Thread(() -> {
            while (true) {
                try {
                    monitor.detectFailure();
                    Thread.sleep(1500); // Check every second (reduced interval)
                } catch (InterruptedException e) {
                    break;
                }
            }
        }).start();

        // Test client connects to primary server's port
        // ...existing code...

        // Simulate primary server failure after 10 seconds
        new Thread(() -> {
            try {
                Thread.sleep(4000); // Wait 10 seconds
                System.out.println("[TEST] Stopping primary server to simulate failure.");
                primary.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // Client loop: repeatedly query monitor and try to connect to primary
        while (true) {
            String primaryHost = "localhost";
            int primaryPort = 6000;

            // Query monitor for primary server info over the network
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
                    System.out.println("Monitor did not return a valid primary address.");
                    Thread.sleep(2000);
                    continue;
                }
            } catch (Exception e) {
                System.out.println("Failed to query monitor: " + e.getMessage());
                Thread.sleep(2000);
                continue;
            }

            boolean success = false;
            while (!success) {
                try {
                    Client client = new Client(primaryHost, primaryPort);
                    client.sendRequest("PROCESS");
                    success = true; // If no exception, connection succeeded
                } catch (Exception e) {
                    System.out.println("Connection failed, retrying...");
                    // Sleep before retrying and re-query monitor for new primary info
                    try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
                    break; // break inner loop to re-query monitor
                }
            }
            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
        }
    }
}
