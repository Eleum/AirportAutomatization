import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AdminViewController extends Controller {
    private AdminView view;
    private int pages;
    private int currentPage;
    private String searchString;
    private List<Route> routes = new ArrayList<>();

    public AdminViewController() {}

    @Override
    public void setController(JFrame view) {
        this.view = (AdminView) view;
    }

    public void addRoute(String id, String source, String destination,
                         String departureTime, String arriveTime, String days, String planeType, String gate) throws IOException {
        Model model = new Model();
        if(!checkConnection()) {
            view.dispose();
            return;
        }
        switch (model.addRoute(id, source, destination, departureTime, arriveTime, days, planeType, gate)) {
            case 0: {
                JOptionPane.showMessageDialog(view,
                        "Рейс успешно добавлен в базу данных.",
                        "Очень важная информация", JOptionPane.INFORMATION_MESSAGE);
                break;
            }
            case 1: {
                JOptionPane.showMessageDialog(view,
                        "Такой рейс уже находится в базе данных.",
                        "Очень важная информация", JOptionPane.WARNING_MESSAGE);
                break;
            }
            case 2: {
                JOptionPane.showMessageDialog(view,
                        "Пожалуйста, заполните все поля.",
                        "Очень важная информация", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void addUsers(List<Account> accounts, JTable table) {
        DefaultTableModel tableModel = (DefaultTableModel)table.getModel();
        for(Account account : accounts) {
            String[] types = {"admin", "dispatcher"};
            String[] typesAccount = account.getType().split("\\+");

            boolean[] typesTable = new boolean[]{false, false};

            int count = typesAccount.length;
            for(int i = 0; i < count; i++) {
                if(typesAccount[i].equals(types[0])) {
                    typesTable[0] = true;
                } else if(typesAccount[i].equals(types[1])) {
                    typesTable[1] = true;
                }
            }
            tableModel.addRow(new Object[]{account.getId(), typesTable[1], typesTable[0]});
        }

        //TODO JUST FOR TEST
        /*for(int i = 0; i < 20; i++) {
            tableModel.addRow(new Object[]{i, false, false});
        }*/
    }

    public void findUser(String userID, JTable table) {
        if(userID.equals("")) {
            JOptionPane.showMessageDialog(view, "Пожалуйста, заполните поле поиска.",
                    "Очень важная информация", JOptionPane.WARNING_MESSAGE);
            return;
        }
        DefaultTableModel tableModel = (DefaultTableModel)table.getModel();
        int rows = tableModel.getRowCount();
        table.removeRowSelectionInterval(0, rows-1);
        for(int i = 0; i < rows; i++) {
            if(tableModel.getValueAt(i, 0).equals(Integer.parseInt(userID))) {
                table.changeSelection(i, 0, false, false);
                return;
            }
        }
        JOptionPane.showMessageDialog(view, "Такого пользователя не найдено.\nПроверьте правильность " +
                "ввода или обновите таблицу и попробуйте снова.",
                "Очень важная информация", JOptionPane.WARNING_MESSAGE);
    }

    public int deleteUser(int id, int currentID) throws IOException {
        Model model = new Model();
        if(!checkConnection()) {
            view.dispose();
            return 666;
        }
        switch (model.deleteUser(id, currentID)) {
            case 0: {
                JOptionPane.showMessageDialog(this.view, "Пользователь " + id + " был успешно удалён.",
                        "Очень важная информация", JOptionPane.INFORMATION_MESSAGE);
                return 0;
            }
            case 1: {
                JOptionPane.showMessageDialog(this.view, "Пользователь " + id + " был успешно удалён.",
                        "Очень важная информация", JOptionPane.INFORMATION_MESSAGE);
                this.view.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                this.view.dispose();
                LoginView.showView();
                return 1;
            }
            case 2: {
                JOptionPane.showMessageDialog(this.view, "Пользователь " + id + "не был удалён.",
                        "Очень важная информация", JOptionPane.ERROR_MESSAGE);
            }
        }
        return 2;
    }

    public void editUserType(int id, boolean[] types) {
        Model model = new Model();
        if(!checkConnection()) {
            view.dispose();
            return;
        }
        try {
            model.editUserType(id, types);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void findRoute(String routeID) {
        Model model = new Model();
        if(!checkConnection()) {
            view.dispose();
            return;
        }
        if(routeID.equals("")) {
            JOptionPane.showMessageDialog(view, "Пожалуйста, заполните поле поиска.",
                    "Очень важная информация", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            routes = model.getRoutes(routeID, 0);
            searchString = routeID;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(routes.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Таких рейсов не найдено.\nПроверьте правильность " +
                            "ввода.",
                    "Очень важная информация", JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(view, "Найдено " + routes.size() + " рейс(ов).",
                    "Очень важная информация", JOptionPane.INFORMATION_MESSAGE);
            view.editPanel.setVisible(true);
            view.setSize(view.getWidth(), view.getHeight() + 55);
            pages = routes.size();
            currentPage = 0;
            renderPage(currentPage);
            makeEditable(false);
        }
    }

    public void deleteRoute(String routeID, String source, String departureTime, String days) {
        Model model = new Model();
        if(!checkConnection()) {
            view.dispose();
            return;
        }
        int dialogButton = JOptionPane.YES_NO_OPTION;
        Object[] options = {"Да", "Нет"};
        int result = JOptionPane.showOptionDialog (view,
                "Вы действительно хотите удалить выбранный рейс из базы данных?","Удаление", dialogButton,
                JOptionPane.WARNING_MESSAGE, null, options, null);
        if(result == JOptionPane.YES_OPTION) {
            try {
                if (model.deleteRoute(routeID, source, departureTime, days).equals("error")) {
                    JOptionPane.showMessageDialog(view,
                            "Не удалось удалить рейс.",
                            "Очень важная информация", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            JOptionPane.showMessageDialog(view,
                    "Рейс успешно удален из базы данных.",
                    "Очень важная информация", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void makeEditable(boolean state) {
        view.idField.setEditable(state);
        view.planeTypeField.setEditable(state);
        view.sourceField.setEditable(state);
        view.destinationField.setEditable(state);
        view.departureTimeField.setEditable(state);
        view.arriveTimeField.setEditable(state);
        view.daysField.setEditable(state);
        view.gateField.setEditable(state);
    }

    private void renderPage(int page) {
        Route route = routes.get(page);
        view.idField.setText(route.getId());
        view.planeTypeField.setText(route.getPlaneType());
        view.sourceField.setText(route.getSource());
        view.destinationField.setText(route.getDestination());
        view.departureTimeField.setText(route.getDepartureTime());
        view.arriveTimeField.setText(route.getArrivalTime());
        view.daysField.setText(route.getDays());
        view.gateField.setText(route.getGate());
    }

    public void previousPage() {
        if(currentPage == 0) {
            currentPage = pages-1;
        } else {
            currentPage--;
        }
        renderPage(currentPage);
        makeEditable(false);
    }

    public void nextPage() {
        if(currentPage == pages-1) {
            currentPage = 0;
        } else {
            currentPage++;
        }
        renderPage(currentPage);
        makeEditable(false);
    }

    public void refreshTable(JTable table) throws IOException, ClassNotFoundException {
        Model model = new Model();
        if(!checkConnection()) {
            view.dispose();
            return;
        }
        DefaultTableModel tableModel = (DefaultTableModel)table.getModel();
        tableModel.setRowCount(0);
        addUsers(model.getUsers(), table);
    }

    public int warn() {
        int dialogButton = JOptionPane.YES_NO_OPTION;
        Object[] options = {"Да", "Нет"};
        int result = JOptionPane.showOptionDialog (view,
                "Удалив модификатор доступа 'Администратор' текущего аккаунта вы больше не сможете\n" +
                        "получить доступ к данному функционалу. Вы действительно хотите это сделать?",
                "Очень важная информация", dialogButton, JOptionPane.WARNING_MESSAGE,
                null, options, null);
        if(result == JOptionPane.YES_OPTION) {
            return 0;
        }
        return 1;
    }

    public void updateRoute(String id, String source, String destination,
                              String departureTime, String arriveTime, String days, String planeType, String gate) {
        Model model = new Model();
        if(!checkConnection()) {
            view.dispose();
            return;
        }
        Route previousRoute = routes.get(currentPage);
        try {
            switch (model.updateRoute(id, source, destination, departureTime, arriveTime, days, planeType, gate, previousRoute)) {
                case 1: {
                    JOptionPane.showMessageDialog(view,
                            "Пожалуйста, заполните все поля.",
                            "Очень важная информация", JOptionPane.WARNING_MESSAGE);
                    break;
                }
                default: {
                    Route currentRoute = routes.get(currentPage);
                    currentRoute.setId(id);
                    currentRoute.setPlaneType(planeType);
                    currentRoute.setSource(source);
                    currentRoute.setDestination(destination);
                    currentRoute.setDepartureTime(departureTime);
                    currentRoute.setArrivalTime(arriveTime);
                    currentRoute.setDays(days);
                    currentRoute.setGate(gate);
                    JOptionPane.showMessageDialog(view, "Рейс успешно обновлен",
                            "Обновление рейса", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getGraphInfo(String day) throws IOException, ClassNotFoundException {
        Model model = new Model();
        return model.getGraphInfo(day);
    }

    public void saveToFile(){
        Model model = new Model();
        try {
            model.saveToFile(0, searchString, routes);
            JOptionPane.showMessageDialog(view, "Успешно записано в файл",
                    "Очень важная информация", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Ошибка сохранения в файл",
                    "Очень важная информация", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void back(TimerThread timerThread) {
        int dialogButton = JOptionPane.YES_NO_OPTION;
        Object[] options = {"Да", "Нет"};
        int result = JOptionPane.showOptionDialog (view,
                "Вы действительно хотите выйти в главное меню?","Выход", dialogButton,
                JOptionPane.PLAIN_MESSAGE, null, options, null);
        if(result == JOptionPane.YES_OPTION) {
            view.dispose();
            LoginView.showView();
        }
    }
}
