import javax.swing.*;

public class UpdateThreadSwing extends SwingWorker<Void, Void> {
    private boolean isRunning;
    private JFrame frame;

    public UpdateThreadSwing(JFrame frame) {
        this.frame = frame;
        this.isRunning = true;
    }

    @Override
    public Void doInBackground() {
        final UpdateThread thread = new UpdateThread(frame);
        while(isRunning) {
            thread.run();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void setRunning(boolean state) {
        isRunning = state;
    }
}
