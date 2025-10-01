

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
}

