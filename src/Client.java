

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class Client {
    private String host; // the server's address
    private int port;    // the port number the server is listening on

    
    public Client(String host, int port) { // When we make a Client, we give it a host (IP) and a port
        this.host = host;
        this.port = port;
    }

    
    public void sendRequest(String request) { // This method lets the client send a request to the server
        try (
           
            Socket socket = new Socket(host, port);  // Open a socket to the server

          
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);  // Send data to the server

            
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())) // Read data from the server
        ) {
            
            out.println(request); // Send the request text to the server

            
            String response = in.readLine(); // Wait and read the response from the server
            System.out.println("Client received: " + response);

        } catch (IOException e) {
            
            System.out.println("Failed to connect to server at " + host + ":" + port + ". Trying to reconnect..."); // If the connection fails, print this message
        }
    }
}

