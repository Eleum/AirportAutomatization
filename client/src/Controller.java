import javax.swing.*;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class Controller {
    public static void runApplication() throws IOException {
        LoginView.showView();
    }

    protected abstract void setController(JFrame view);

    protected boolean checkConnection() {
        if(!Model.checkConnection()) {
            try {
                Connection.start();
            } catch (Exception e) {
                if (!Model.checkConnection()) {
                    JOptionPane.showMessageDialog(null,
                            "Нет соединения с сервером.", "Подключение",
                            JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
        }
        return true;
    }

    protected static void close() {
        try {
            Connection.end();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    protected abstract void back(TimerThread timerThread);

    protected static long timeElapsed(String startTime, String endTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        long elapsed = 0;
        if(!startTime.equals("")) {
            try {
                Date d1 = dateFormat.parse(startTime);
                Date d2 = dateFormat.parse(endTime);
                elapsed = d2.getTime() - d1.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return elapsed;
    }
}
