import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class ServerProcess {
    protected int id;
    protected volatile boolean running = true;
    protected ServerSocket serverSocket;
    protected ExecutorService threadPool = Executors.newCachedThreadPool();
    protected int port;
    protected int heartbeatIntervalMs = 2000;

    public ServerProcess(int id, int port) {
        this.id = id;
        this.port = port;
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        Logger.log("Server " + id + " started on port " + port);

        while (running) {
            try {
                Socket s = serverSocket.accept();
                threadPool.submit(new ConnectionHandler(s, this));
            } catch (IOException e) {
                if (!running) break;
                e.printStackTrace();
            }
        }
    }

    public void stop() throws IOException {
        running = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
        threadPool.shutdownNow();
        Logger.log("Server " + id + " stopped.");
    }

    public abstract void sendHeartbeat();
    public abstract void receiveHeartbeat();
}
