import java.io.IOException;

public interface Speakable {
    void speak(String input);
    void activateMic(String type, String routeID, String source, String destination, String gates, String time) throws IOException;
}
