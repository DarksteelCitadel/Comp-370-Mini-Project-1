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
}
