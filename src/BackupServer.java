import java.io.*;
import java.net.*;

public class BackupServer extends ServerProcess {

    public boolean isPromoted = false;
    private ServerSocket serverSocket;

    public BackupServer(int id, int port) {
        super(id, port); // call parent constructor
    }
    public String handleRequest(String request) {
        Logger.log("BackupServer " + id + " (PROMOTED) handling request: " + request);
        return "Response from BackupServer " + id + " (PROMOTED): " + request;
    }
    @Override
    public void start() {
        try {
            // Bind socket early so clients can connect
            serverSocket = new ServerSocket(port);
            System.out.println("BackupServer " + id + " started on port " + port + " (monitoring only).");

            // Thread to accept client connections
            new Thread(() -> {
                try {
                    while (true) {
                        Socket client = serverSocket.accept();
                        if (isPromoted) {
                            handleClient(client);
                        } else {
                            // Optionally: reject connection until promoted
                            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                            out.println("Backup server not ready yet.");
                            client.close();
                        }
                    }
                } catch (IOException e) {
                    System.out.println("BackupServer " + id + " stopped accepting connections.");
                }
            }).start();

            // Heartbeat thread
            new Thread(() -> {
                try {
                    while (true) {
                        try (Socket monitorSocket = new Socket("localhost", 7100);
                             PrintWriter out = new PrintWriter(monitorSocket.getOutputStream(), true)) {
                            out.println("HEARTBEAT " + id);
                        } catch (Exception e) {
                            System.out.println("BackupServer " + id + " failed to send heartbeat, retrying...");
                        }
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException ignored) {}
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void promote() {
        isPromoted = true;
        System.out.println("BackupServer " + id + " promoted to primary!");
    }

    private void handleClient(Socket client) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
             PrintWriter out = new PrintWriter(client.getOutputStream(), true)) {

            String request = in.readLine();
            System.out.println("BackupServer " + id + " (PROMOTED) handling request: " + request);
            out.println("Response from BackupServer " + id + " (PROMOTED): " + request);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendHeartbeat() {
        // optional override
    }

    @Override
    public void receiveHeartbeat() {
        // optional override
    }
}


