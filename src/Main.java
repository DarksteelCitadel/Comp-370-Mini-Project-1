public class Main {
    public static void main(String[] args) throws Exception {
        // Create servers with new ports
        PrimaryServer primary = new PrimaryServer(1, 6000);   // changed from 5000( 5000 was giving me an error for some reason)
        BackupServer backup = new BackupServer(2, 6001);      // changed from 5001 ( 5001 was giving me an error for some reason)
        Monitor monitor = new Monitor();

        monitor.registerServer(primary);
        monitor.registerServer(backup);

        // Start heartbeats
        new Thread(new HeartbeatSender(primary)).start();
        new Thread(new HeartbeatSender(backup)).start();

        // Start servers
        new Thread(() -> {
            try { primary.start(); } catch(Exception e){ e.printStackTrace(); }
        }).start();

        new Thread(() -> {
            try { backup.start(); } catch(Exception e){ e.printStackTrace(); }
        }).start();

        // Test client connects to primary server's port
        Client client = new Client("localhost", 6000);
        client.sendRequest("Hello Server!");
    }
}
