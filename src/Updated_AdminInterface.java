import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class AdminInterface {
    private final Monitor monitor;
    private final List<String> recentLogs;
    private static final int MAX_LOG_ENTRIES = 50;

    public AdminInterface(Monitor monitor) {
        this.monitor = monitor;
        this.recentLogs = Collections.synchronizedList(new ArrayList<>());
    }

    public void start() {
        try (Scanner scanner = new Scanner(System.in)) {
            Logger.log("Admin interface started. Commands: status, health, logs, failover, restart, help, exit");
            printHelp();

            while (true) {
                System.out.print("> ");
                String command = scanner.nextLine().trim().toLowerCase();

                switch (command) {
                    case "status":
                        showStatus();
                        break;
                    case "health":
                        checkHealth();
                        break;
                    case "logs":
                        showLogs();
                        break;
                    case "failover":
                        doFailover();
                        break;
                    case "restart":
                        restartServers();
                        break;
                    case "help":
                        printHelp();
                        break;
                    case "exit":
                        Logger.log("Admin interface exiting.");
                        return;
                    default:
                        System.out.println("Unknown command. Type 'help' for available commands.");
                }
            }
        }
    }

    private void printHelp() {
        System.out.println("\nAvailable commands:");
        System.out.println("status    - Show server status");
        System.out.println("health    - Check if servers are working");
        System.out.println("logs      - Show recent logs");
        System.out.println("failover  - Switch to backup server");
        System.out.println("restart   - Restart servers");
        System.out.println("help      - Show this help");
        System.out.println("exit      - Exit admin interface");
        System.out.println();
    }

    private void showStatus() {
        System.out.println("\nServer Status:");
        monitor.printStatus();
        
        System.out.println("\nSystem Info:");
        System.out.println("Primary Server: localhost:6000");
        System.out.println("Backup Server: localhost:6001");
        System.out.println("Monitor: localhost:7100");
        System.out.println("Heartbeat: every 2 seconds");
        System.out.println("Timeout: 5 seconds");
        System.out.println();
    }

    private void checkHealth() {
        System.out.println("\nHealth Check:");
        
        boolean primaryOK = checkServer("localhost", 6000);
        boolean backupOK = checkServer("localhost", 6001);
        boolean monitorOK = checkServer("localhost", 7100);
        
        System.out.println("Primary Server (6000): " + (primaryOK ? "OK" : "NOT WORKING"));
        System.out.println("Backup Server (6001): " + (backupOK ? "OK" : "NOT WORKING"));
        System.out.println("Monitor (7100): " + (monitorOK ? "OK" : "NOT WORKING"));
        
        if (primaryOK && monitorOK) {
            System.out.println("\nSystem Status: WORKING");
        } else if (backupOK && monitorOK) {
            System.out.println("\nSystem Status: BACKUP ACTIVE");
        } else {
            System.out.println("\nSystem Status: PROBLEMS");
        }
        System.out.println();
    }

    private boolean checkServer(String host, int port) {
        try {
            java.net.Socket socket = new java.net.Socket(host, port);
            socket.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void showLogs() {
        System.out.println("\nRecent Logs:");
        if (recentLogs.isEmpty()) {
            System.out.println("No logs available.");
        } else {
            int start = Math.max(0, recentLogs.size() - 20);
            for (int i = start; i < recentLogs.size(); i++) {
                System.out.println(recentLogs.get(i));
            }
        }
        System.out.println();
    }

    private void doFailover() {
        System.out.println("\nStarting failover...");
        monitor.triggerFailover();
        System.out.println("Failover done.");
        System.out.println();
    }

    private void restartServers() {
        System.out.println("\nRestart Servers:");
        System.out.println("In a real system, this would:");
        System.out.println("1. Stop all servers");
        System.out.println("2. Clear state");
        System.out.println("3. Start servers again");
        System.out.println("4. Check connections");
        System.out.println("5. Test everything");
        System.out.println("\nFor this demo, restart the application manually.");
        System.out.println();
    }

    public void addLog(String logEntry) {
        recentLogs.add("[" + java.time.LocalTime.now() + "] " + logEntry);
        if (recentLogs.size() > MAX_LOG_ENTRIES) {
            recentLogs.remove(0);
        }
    }
}
