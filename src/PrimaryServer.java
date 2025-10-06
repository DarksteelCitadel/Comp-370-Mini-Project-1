public class PrimaryServer extends ServerProcess {

    public PrimaryServer(int id, int port) {
        super(id, port);
    }

    @Override
    public void sendHeartbeat() {
        Logger.log("PrimaryServer " + id + " sending heartbeat.");
    }

    @Override
    public void receiveHeartbeat() {
        Logger.log("PrimaryServer " + id + " received heartbeat.");
    }

    public String handleRequest(String request) {
        Logger.log("PrimaryServer " + id + " handling request: " + request);
        return "Response from PrimaryServer " + id + ": " + request;
    }

    public static void main(String[] args) {
        int id = 1;
        int port = 6000;
        if (args.length >= 2) {
            try {
                id = Integer.parseInt(args[0]);
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid arguments, using defaults id=1 port=6000");
            }
        }
        PrimaryServer server = new PrimaryServer(id, port);
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
