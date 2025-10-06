import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class ConnectionHandler implements Runnable {
    private final Socket socket;        // the socket between client and server
    private final ServerProcess server; // the server that accepted this connection

    
    public ConnectionHandler(Socket socket, ServerProcess server) { // Constructor: when a new client connects, create a ConnectionHandler for it
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Input stream: read messages from client
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true) // Output stream: send messages back to client
        ) {
            String request = in.readLine(); // Read the request message sent by the client

            if (server instanceof PrimaryServer) {
                String response = ((PrimaryServer) server).handleRequest(request);
                out.println(response); // send response back to client
            } else if (server instanceof BackupServer && ((BackupServer) server).isPromoted) {
                // Promoted backup acts as primary
                String response = "[PROMOTED] " + ((BackupServer) server).handleRequest(request);
                out.println(response);
            } else {
                out.println("Backup server cannot process requests.");
            }
        } catch (IOException e) {
            e.printStackTrace(); // show error if something goes wrong
        }
    }
