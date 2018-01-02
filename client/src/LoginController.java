//uncomment if wanna use Please wait window
/*public int waitWindow(JTextField loginField, JPasswordField passwordField) {
        Window win = SwingUtilities.getWindowAncestor(view);
        final JDialog dialog = new JDialog(win, "Dialog", Dialog.ModalityType.APPLICATION_MODAL);

        Thread thread = new Thread(() -> {
            try {
                synchronized (lockObject) {
                    try {
                        lockObject.wait(10000);
                    } catch (InterruptedException ex) {}
                }
                dialog.dispose();
            } catch (Exception ex) {}
        });

        StringBuilder sb = new StringBuilder();
        for (char c : passwordField.getPassword()) {
        sb.append(c);
        }
        try {
        login(loginField.getText(), sb.toString());
        } catch (Exception e) {}

        //appending chars in array to each other
        SwingWorker<Void, Void> sw = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                thread.start();
                login(loginField.getText(), sb.toString());
                return null;
            }
        };
        sw.execute();

        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(progressBar, BorderLayout.CENTER);
        panel.add(new JLabel("  Подождите, пожалуйста... "), BorderLayout.PAGE_START);
        dialog.setUndecorated(true);
        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(win);
        dialog.setVisible(true);
        return 0;
        }*/

import javax.swing.*;
import java.io.IOException;

public class LoginController extends Controller {
    private LoginView view;
    //public final Object lockObject = new Object();

    public LoginController(){}

    @Override
    public void setController(JFrame view) {
        this.view = (LoginView)view;
    }

    public int waitWindow(JTextField loginField, JPasswordField passwordField) {

        StringBuilder sb = new StringBuilder();
        for (char c : passwordField.getPassword()) {
            sb.append(c);
        }
        try {
            login(loginField.getText(), sb.toString());
        } catch (Exception e) {e.printStackTrace();}

        return 0;
    }

    public void login(String login, String password) throws IOException {
        Model model = new Model();
        if(!checkConnection()) {
            /*synchronized (lockObject) {
                lockObject.notify();
            }*/
            return;
        }
        if(login.length() == 0 ||
                password.length() == 0) {
            JOptionPane.showMessageDialog(view, "Пожалуйста, заполните все поля",
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Account account = new Account(login, password);

        String type;
        type = model.logIn(account);

        /*synchronized (lockObject) {
            lockObject.notify();
        }*/

        if(type.equals("error")) {
            JOptionPane.showMessageDialog(view, "Не удается войти в систему.\n" +
                            "Пожалуйста, проверьте правильность написания логина и пароля.",
                    "Очень важная информация", JOptionPane.WARNING_MESSAGE);
            /*synchronized (lockObject) {
                lockObject.notify();
            }*/
            return;
        }

        switch (type) {
            case "admin" : {
                view.dispose();
                AdminView.showView(account);
                break;
            }
            case "admin+dispatcher": {
                int dialogButton = JOptionPane.YES_NO_OPTION;
                Object[] options = {"Админ", "Диспетчер"};
                int result = JOptionPane.showOptionDialog (view,
                        "Выберите систему для входа:","Вход", dialogButton,
                        JOptionPane.PLAIN_MESSAGE, null, options, null);
                view.dispose();
                if(result == JOptionPane.YES_OPTION) {
                    AdminView.showView(account);
                } else if(result != -1){
                    DispatcherView.showView();
                } else {
                    LoginView.showView();
                }
                break;
            }
            case "dispatcher": {
                view.dispose();
                DispatcherView.showView();
                break;
            }
            case "user": {
                view.dispose();
                UserBoardView.showView();
            }
        }
    }

    public void openRegistration() {
        view.dispose();

        SignUpView sv = new SignUpView("Регистрация");
        sv.controller = new SignUpController();
        sv.controller.setController(sv);
        sv.setSize(264, 297);
        sv.setResizable(false);
        sv.setLocationRelativeTo(null);
    }

    @Override
    public void back(TimerThread timerThread) {}
}
