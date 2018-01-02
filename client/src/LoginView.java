import javax.swing.*;
import java.awt.event.*;

public class LoginView extends JFrame implements ActionListener {
    public JPanel rootPanel;
    public JTextField loginField;
    public JPasswordField passwordField;
    public LoginController controller;
    private JButton loginButton;
    private JButton signUpButton;

    public LoginView(String title) {
        super(title);
        setContentPane(rootPanel);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        loginButton.addActionListener(this);
        signUpButton.addActionListener(this);
        loginButton.setActionCommand("login");
        signUpButton.setActionCommand("signup");

        highlightOnFocus(loginField);
        highlightOnFocus(passwordField);

        super.getRootPane().setDefaultButton(loginButton);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Controller.close();
            }
        });
    }

    private void highlightOnFocus(JTextField textField) {
        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                textField.selectAll();
            }

            @Override
            public void focusLost(FocusEvent e) {
                textField.select(0,0);
            }
        });
    }

    public static void showView() {
        //Windows style
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        LoginView loginView = new LoginView("Вход");
        loginView.controller = new LoginController();
        loginView.controller.setController(loginView);
        loginView.setSize(203, 311);
        loginView.setResizable(false);
        loginView.setLocationRelativeTo(null);
    }

    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        switch(cmd) {
            case "login": {
                controller.waitWindow(loginField, passwordField);
                break;
            }
            case "signup": {
                controller.openRegistration();
            }
        }
    }
}
