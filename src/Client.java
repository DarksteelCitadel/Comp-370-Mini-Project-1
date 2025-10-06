import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private String host;
    private int port;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    // Now returns the server response or throws an IOException
    public String sendRequest(String request) throws IOException {
        try (
            Socket socket = new Socket(host, port);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            out.println(request); // send request
            String response = in.readLine(); // read response
            return response;
        } catch (IOException e) {
            throw new IOException("Failed to connect to server at " + host + ":" + port, e);
        }
    }
}


