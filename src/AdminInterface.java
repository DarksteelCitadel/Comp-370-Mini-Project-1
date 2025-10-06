import java.util.Scanner;


public class AdminInterface {
    private final Monitor monitor;

    public AdminInterface(Monitor monitor) {
        this.monitor = monitor;
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        Logger.log("Admin interface started. Commands: status, failover, exit");

        while (true) {
            System.out.print("> ");
            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "status":
                    monitor.printStatus();
                    break;
                case "failover":
                    monitor.triggerFailover();
                    break;
                case "exit":
                    Logger.log("Admin interface exiting.");
                    return;
                default:
                    System.out.println("Unknown command. Commands: status, failover, exit");
            }
        }
    }

    public static void main(String[] args) {
        Monitor monitor = new Monitor();
        AdminInterface admin = new AdminInterface(monitor);
        admin.start();
    }
}
