import javax.swing.*;
import static java.lang.System.exit;

public class Main {

    public static void main(String[] args) {
        try {
            Controller.runApplication();
            Connection.start();
        } catch(Exception e) {
            JOptionPane.showMessageDialog(null, "Не удаётся подключиться к серверу. Пожалуйста, повторите попытку позже.",
                    "Очень важная информация", JOptionPane.ERROR_MESSAGE);
            //exit(666);
        }
    }
}
