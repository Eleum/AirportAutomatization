import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Arrays;

public class SignUpView extends JFrame implements ActionListener {
    public JPanel rootPanel;
    public JTextField loginField;
    public JPasswordField passwordField;
    public JPasswordField reenteredPasswordField;
    public SignUpController controller;
    private JButton signUpButton;
    private JButton backButton;

    public SignUpView(String title) {
        super(title);
        setContentPane(rootPanel);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        signUpButton.addActionListener(this);
        signUpButton.setActionCommand("signup");
        backButton.addActionListener(this);
        backButton.setActionCommand("back");

        highlightOnFocus(loginField);
        highlightOnFocus(passwordField);
        highlightOnFocus(reenteredPasswordField);

        super.getRootPane().setDefaultButton(signUpButton);

        //RED FIELD OF DEATH
        reenteredPasswordField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                changed();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changed();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                changed();
            }

            public void changed() {
                if(!Arrays.equals(passwordField.getPassword(), reenteredPasswordField.getPassword())) {
                    reenteredPasswordField.setBackground(new Color(199, 131, 117));
                    return;
                }
                reenteredPasswordField.setBackground(new Color(60, 63, 65));
            }
        });

        super.addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e) {
                loginField.requestFocus();
            }
            public void windowClosing(WindowEvent e) {
                try {
                    controller.back(null);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
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

    public void actionPerformed (ActionEvent e) {
        String cmd = e.getActionCommand();
        try {
            switch (cmd) {
                case "signup": {
                    //appending chars in arrays
                    StringBuilder sb = new StringBuilder();
                    String password;

                    for(char c : passwordField.getPassword()) {
                        sb.append(c);
                    }

                    password = sb.toString();

                    sb.delete(0, sb.length());
                    for(char c : reenteredPasswordField.getPassword()) {
                        sb.append(c);
                    }

                    controller.signUp(loginField.getText(), password,
                            sb.toString());
                    break;
                }
                case "back": {
                    controller.back(null);
                }
            }
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }
}
