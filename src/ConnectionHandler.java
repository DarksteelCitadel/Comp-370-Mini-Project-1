import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ConnectionHandler implements Runnable {
    private final Socket socket;        // The socket between client and server
    private final ServerProcess server; // The server that accepted this connection

    public ConnectionHandler(Socket socket, ServerProcess server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Read client messages
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true) // Send response
        ) {
            String request = in.readLine(); // Read request

            if (server instanceof PrimaryServer) {
                String response = ((PrimaryServer) server).handleRequest(request);
                out.println(response);
            } else if (server instanceof BackupServer && ((BackupServer) server).isPromoted) {
                String response = "[PROMOTED] " + ((BackupServer) server).handleRequest(request);
                out.println(response);
            } else {
                out.println("Backup server cannot process requests.");
            }
        } catch (IOException e) {
            e.printStackTrace(); // Log errors
        }
    }
}

