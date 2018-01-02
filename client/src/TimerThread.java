import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimerThread extends Thread {
    private boolean isRunning;

    private JLabel dateLabel;
    private JLabel timeLabel;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    public TimerThread(JLabel dateLabel, JLabel timeLabel) {
        this.dateLabel = dateLabel;
        this.timeLabel = timeLabel;
        this.isRunning = true;
    }

    @Override
    public void run() {
        while (isRunning) {
            SwingUtilities.invokeLater(() -> {
                Calendar currentCalendar = Calendar.getInstance();
                Date currentTime = currentCalendar.getTime();
                if (dateLabel != null) {
                    dateLabel.setText(dateFormat.format(currentTime));
                }
                timeLabel.setText(timeFormat.format(currentTime));
            });

            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

}