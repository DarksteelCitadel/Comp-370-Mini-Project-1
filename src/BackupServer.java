

public class BackupServer extends ServerProcess {

    private boolean isPromoted = false; // This tells us if the backup has turned into a primary server yet

    
    public BackupServer(int id, int port) { // When we make a BackupServer, we give it an id and a port number
      
        super(id, port);   // Call the parent (ServerProcess) to set these values
    }

 
    @Override
    public void sendHeartbeat() {    // This is the heartbeat the backup sends out
        System.out.println("BackupServer " + id + " sending heartbeat.");
    }


    @Override
    public void receiveHeartbeat() {    // This is what happens when the backup receives a heartbeat
        System.out.println("BackupServer " + id + " received heartbeat.");
    }

  
    public void monitorPrimary() {  // Backup keeps an eye on the primary to make sure it’s alive
        System.out.println("BackupServer " + id + " monitoring primary...");
    }

   
    public void promote() {  // If the primary goes down, backup promotes itself to primary
        isPromoted = true;
        System.out.println("BackupServer " + id + " promoted to primary!");
    }

    //handle client requests only if promoted
    public String handleRequest(String request) {
        if (isPromoted) {
            System.out.println("BackupServer " + id + " (now primary) handling request: " + request);
            return "Response from promoted BackupServer " + id + ": " + request;
        } else {
            System.out.println("BackupServer " + id + " ignoring request (still backup).");
            return null; // or some message that it’s not primary
        }
    }
}

