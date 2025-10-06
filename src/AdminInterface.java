import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class AdminInterface {
    private final String monitorHost; // Monitor host to connect
    private final int monitorPort;    // Monitor port to connect

    public AdminInterface(String host, int port) {
        this.monitorHost = host;
        this.monitorPort = port;
    }

    // Send command to monitor and return response
    private String sendCommandToMonitor(String command) {
        try (Socket socket = new Socket(monitorHost, monitorPort);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(command); // send command to monitor
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line).append("\n");
            }
            return response.toString();

        } catch (Exception e) {
            return "Failed to connect to Monitor: " + e.getMessage();
        }
    }

    // Start command-line interface
    public void start() {
        Scanner scanner = new Scanner(System.in);
        Logger.log("Admin interface started. Commands: status, failover, exit");

        while (true) {
            System.out.print("> ");
            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "status":
                    System.out.println(sendCommandToMonitor("STATUS")); // Get server status
                    break;
                case "failover":
                    System.out.println(sendCommandToMonitor("FAILOVER")); // Trigger manual failover
                    break;
                case "exit":
                    Logger.log("Admin interface exiting."); // Log exit
                    return;
                default:
                    System.out.println("Unknown command. Commands: status, failover, exit");
            }
        }
    }

    public static void main(String[] args) {
        String host = "localhost";
        int port = 7100; // default monitor port
        if (args.length >= 2) {
            host = args[0];
            port = Integer.parseInt(args[1]);
        }

        AdminInterface admin = new AdminInterface(host, port);
        admin.start(); // Start interface
    }
}

