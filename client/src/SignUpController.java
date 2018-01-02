import javax.swing.*;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpController extends Controller {
    private SignUpView view;

    public SignUpController() {}

    @Override
    public void setController(JFrame view) {
        this.view = (SignUpView)view;
    }

    public void signUp(String login, String password, String reentered) throws IOException {
        Model model = new Model();
        if(!checkConnection()) {
            view.dispose();
            return;
        }
        if (login.length() == 0) {
            JOptionPane.showMessageDialog(view, "Заполните поле логин",
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
            view.loginField.requestFocus();
            return;
        }
        Pattern special = Pattern.compile ("[~`!@#$%&*()_+=|/<>{}?'\\[\\]-]");
        Matcher hasSpecial = special.matcher(login);
        if(hasSpecial.find()) {
            JOptionPane.showMessageDialog(view, "Логин содержит запрещенные символы.",
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
            view.loginField.requestFocus();
            return;
        }
        if(password.length() == 0 || !password.equals(reentered)) {
            JOptionPane.showMessageDialog(view, "Введенные пароли не совпадают",
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
            view.passwordField.requestFocus();
            return;
        }

        Account account = new Account(login, password);

        if(model.signUp(account)) {
            JOptionPane.showMessageDialog(view,
                    "Регистрация прошла успешно! Для входа в систему используйте свой логин и пароль.",
                    "Очень важная информация", JOptionPane.INFORMATION_MESSAGE);
            back(null);
        } else {
            JOptionPane.showMessageDialog(view,
                    "Такой логин уже существует. Пожалуйста, выберите новый.",
                    "Очень важная информация", JOptionPane.WARNING_MESSAGE);
        }
    }

    @Override
    public void back(TimerThread timerThread) {
        this.view.dispose();
        LoginView.showView();
    }
}
