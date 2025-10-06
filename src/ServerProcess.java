import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class ServerProcess {
    protected int id;                       // Unique server identifier
    protected volatile boolean running = true; // Server running flag
    protected ServerSocket serverSocket;    
    protected ExecutorService threadPool = Executors.newCachedThreadPool(); // Thread pool for client handling
    protected int port;                     // Server port
    protected int heartbeatIntervalMs = 2000; // Interval to send heartbeat

    public ServerProcess(int id, int port) {
        this.id = id;
        this.port = port;
    }

    // Start the server and accept incoming connections
    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        Logger.log("Server " + id + " started on port " + port);

        while (running) {
            try {
                Socket s = serverSocket.accept(); // Wait for client connection
                threadPool.submit(new ConnectionHandler(s, this)); // Handle client in separate thread
            } catch (IOException e) {
                if (!running) break; // Exit if server is stopped
                e.printStackTrace();
            }
        }
    }

    // Stop server and cleanup resources
    public void stop() throws IOException {
        running = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
        threadPool.shutdownNow();
        Logger.log("Server " + id + " stopped.");
    }

    // Abstract methods for heartbeat logic (to be implemented in subclasses)
    public abstract void sendHeartbeat();
    public abstract void receiveHeartbeat();
}

