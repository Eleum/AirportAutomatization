import javax.swing.*;

public class UpdateThread extends Thread {
    private JFrame frame;

    public UpdateThread(JFrame frame) {
        this.frame = frame;
    }


    @Override
    public void run() {
        try {
            SwingUtilities.invokeAndWait(() -> {
                try {
                    ((UserBoardView) frame).controller.refreshTable(true);
                    frame.revalidate();
                    frame.repaint();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {}
    }
}
