

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public abstract class ServerProcess {
    protected int id; // unique server ID
    protected volatile boolean running = true; // true while server is running
    protected ServerSocket serverSocket; // socket to accept incoming connections
    protected ExecutorService threadPool = Executors.newCachedThreadPool(); // thread pool for handling clients
    protected int port; // port number to listen on
    protected int heartbeatIntervalMs = 2000; // interval for sending heartbeats (ms)

    
    public ServerProcess(int id, int port) { // Constructor sets server ID and port
        this.id = id;
        this.port = port;
    }

    
    public void start() throws IOException { // Start the main server loop
        serverSocket = new ServerSocket(port);
        System.out.println("Server " + id + " started on port " + port);

        
        while (running) { // Loop to accept incoming connections
            Socket s = serverSocket.accept(); // wait for client or other server connection
            threadPool.submit(new ConnectionHandler(s, this)); // handle connection in a new thread
        }
    }

   
    public void stop() throws IOException {  // Stop the server safely
        running = false; // stop the loop
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close(); // close socket
        }
        threadPool.shutdownNow(); // stop all running threads
        System.out.println("Server " + id + " stopped.");
    }

    
    public abstract void sendHeartbeat(); // Abstract method to send heartbeat; implemented in PrimaryServer and BackupServer

    
    public abstract void receiveHeartbeat(); // Abstract method to receive heartbeat; implemented in PrimaryServer and BackupServer
}

