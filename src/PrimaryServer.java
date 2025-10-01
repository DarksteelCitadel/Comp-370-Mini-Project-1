


public class PrimaryServer extends ServerProcess {

  
    public PrimaryServer(int id, int port) {   // Constructor sets the server ID and port
        super(id, port);
    }

    
    @Override
    public void sendHeartbeat() { // Send heartbeat to let Monitor and backups know this server is alive
        System.out.println("PrimaryServer " + id + " sending heartbeat.");
        
    }

    
    @Override
    public void receiveHeartbeat() { 
     
    }

 
    public String handleRequest(String request) {    // Handle a client request and return a response
        System.out.println("PrimaryServer " + id + " handling request: " + request);
        return "Response from PrimaryServer " + id + ": " + request;
    }
}