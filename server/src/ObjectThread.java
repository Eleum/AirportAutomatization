public class ObjectThread extends Thread {
    public static final Object lock = new Object();
    public static boolean task;

    public ObjectThread() {}

    @Override
    public void run() {

    }

    private void end() {
        this.interrupt();
    }
}
