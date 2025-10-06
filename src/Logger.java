import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    // Logs events with timestamp for monitoring and debugging
    public static void log(String event) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println("[" + timestamp + "] " + event);
    }
}
