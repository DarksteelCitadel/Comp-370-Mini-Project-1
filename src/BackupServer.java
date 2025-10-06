public class BackupServer extends ServerProcess {

    public boolean isPromoted = false; // This tells us if the backup has turned into a primary server yet

    // Allow promoted backup to handle client requests
    public String handleRequest(String request) {
        if (isPromoted) {
            System.out.println("BackupServer " + id + " (PROMOTED) handling request: " + request);
            return "Response from BackupServer " + id + " (PROMOTED): " + request;
        } else {
            return "Backup server cannot process requests.";
        }
    }

    public BackupServer(int id, int port) { // When we make a BackupServer, we give it an id and a port number
        super(id, port);   // Call the parent (ServerProcess) to set these values
    }

    public void start() {
        System.out.println("BackupServer " + id + " started on port " + port);
        // Your server start logic here...
    }

    public void stop() {
        System.out.println("BackupServer " + id + " stopped.");
        // Your server stop logic here...
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

    public static void main(String[] args) {
        int id = 2;
        int port = 6001;
        if (args.length >= 2) {
            try {
                id = Integer.parseInt(args[0]);
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid arguments, using defaults id=2 port=6001");
            }
        }
        BackupServer server = new BackupServer(id, port);
        try {
            server.start();
            // Keep running
            while (true) {
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

