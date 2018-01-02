import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Model {
    private static boolean isConnected;

    public Model() {
        //suda
        isConnected = Connection.socket == null;
    }

    public static boolean checkConnection() {
        return !isConnected;
    }

    public boolean signUp(Account account) throws IOException {
        return Connection.signUp(account);
    }

    public String logIn(Account account) throws IOException {
        String string = Connection.logIn(account);
        String id = string.split("\\|")[0];
        if (!id.equals("error")) {
            account.setId(Integer.parseInt(id));
            String type = string.split("\\|")[1];
            return type;
        }
        return id;
    }

    public int addRoute(String id, String source, String destination,
                        String departureTime, String arriveTime, String days, String planeType, String gate) throws IOException {
        if (id.equals("") || source.equals("") || destination.equals("") || departureTime.equals("") ||
                gate.equals("") || planeType.equals("")) {
            return 2;
        }

        Route route = new Route(id, source, destination, departureTime, arriveTime, days, planeType, gate);

        if (Connection.addRoute(route)) {
            return 0;
        }
        return 1;
    }

    public List<Route> getRoutes(int day, String columnName, String time) throws IOException, ClassNotFoundException {
        return Connection.getRoutes(day, columnName, time);
    }

    public List<Route> getRoutes(String route, int type) throws IOException, ClassNotFoundException {
        if (type == 0)
            return Connection.getRoutes(route, 0);
        else
            return Connection.getRoutes(route, 1);
    }

    public String deleteRoute(String routeID, String source, String departureTime, String days) throws IOException {
        Route route = new Route(routeID, source, departureTime, days);
        return Connection.deleteRoute(route);
    }

    public List<Account> getUsers() throws IOException, ClassNotFoundException {
        return Connection.getUsers();
    }

    public int deleteUser(int id, int currentID) throws IOException {
        boolean deleting = Connection.deleteUser(id);
        if (!deleting) {
            return 2;
        }
        if (currentID == id) {
            return 1;
        }
        return 0;
    }

    public void editUserType(int id, boolean[] types) throws IOException {
        String type;
        String isAdmin = "";
        if (types[0]) {
            isAdmin = "admin+";
        }
        type = isAdmin;

        if (types[1]) {
            type += "dispatcher";
        }

        if (type.equals("")) {
            type = "user";
        }

        if (type.endsWith("+")) {
            type = type.replaceFirst("[+]", "");
        }

        Connection.editUserType(id, type);
    }

    public void updateStatus(String id, String source, String time, String status) throws IOException {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);

        int tempDay = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (tempDay == 0) {
            tempDay = 7;
        }

        Connection.updateStatus(id, source, time, tempDay, status);
    }

    public static String checkTaskStatus() throws IOException {
        return Connection.checkTaskStatus();
    }

    public int updateRoute(String id, String source, String destination,
                           String departureTime, String arriveTime,
                           String days, String planeType, String gate, Route previousRoute) throws IOException {
        if (id.equals("") || source.equals("") || destination.equals("") || departureTime.equals("") ||
                gate.equals("") || planeType.equals("")) {
            return 1;
        }
        Route route = new Route(id, source, destination, departureTime, arriveTime, days, planeType, gate);

        Connection.updateRoute(route, previousRoute);
        return 0;
    }

    private String translate(String text) {
        switch (text) {
            case "Минск": {
                text = "MINSK";
                break;
            }
            case "Баку": {
                text = "BAKU";
                break;
            }
            case "Ашхабад": {
                text = "ASHHABAD";
                break;
            }
            case "Алматы": {
                text = "ALMATY";
                break;
            }
            case "Астана": {
                text = "ASTANA";
                break;
            }
            case "Будапешт": {
                text = "BUDAPESHT";
                break;
            }
            case "Киев (Борисполь)": {
                text = "KIEV(BORISPIL)";
                break;
            }
            case "Амстердам": {
                text = "AMSTERDAM";
                break;
            }
            case "Барселона": {
                text = "BARSELONA";
                break;
            }
            case "Милан": {
                text = "MILAN";
                break;
            }
            case "Рим": {
                text = "ROME";
                break;
            }
            case "Варшава": {
                text = "VARSHAVA";
                break;
            }
            case "Санкт-Петербург": {
                text = "ST-PETERSBURG";
                break;
            }
            case "Калининград": {
                text = "KALININGRAD";
                break;
            }
            case "Москва (Домодедово)": {
                text = "MOSCOW(DOM)";
                break;
            }
            case "Абу-Даби": {
                text = "ABU-DABI";
                break;
            }
            case "Багдад": {
                text = "BAGDAD";
                break;
            }
            case "Москва (Шереметьево)": {
                text = "MOSCOW(SHER)";
                break;
            }
            case "Москва (Внуково)": {
                text = "MOSCOW(VNY)";
                break;
            }
            case "Екатеринбург": {
                text = "EKATERINBURG";
                break;
            }
        }
        return text;
    }

    public int getGraphInfo(String day) throws IOException, ClassNotFoundException {
        return Connection.getGraphInfo(day);
    }

    public void saveToFile(int type, String searchString, List<Route> adminRoutes) throws IOException, ClassNotFoundException {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy"),
                sdfWithTime = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        Date date = calendar.getTime();

        String s, fileName = "Отчет по рейсам ";

        if (type == 0) {
            fileName += "(администратор).txt";
        } else {
            fileName += sdf.format(date) + ".txt";
        }

        FileWriter file = new FileWriter(fileName, false);

        List<Route> routes;

        if (type != 0) {
            int day = calendar.get(Calendar.DAY_OF_WEEK) - 1;
            if (day == 0) {
                day = 7;
            }
            routes = Connection.getRoutes(day, "", "00:00");

            file.write("Время создания: " + sdfWithTime.format(date) + "\n");
            file.write("----------------------------------------------------" +
                    "----------------------------------------------------------------------------------\n");
        } else {
            routes = adminRoutes;
            file.write("Отчет по рейсам\n\nСтрока поиска: " + searchString +
                    "\n\nДата создания отчета: " + sdfWithTime.format(date) +
                    "\n----------------------------------------------------" +
                    "----------------------------------------------------------------------------------\n");
        }

        s = String.format("%-13s%2s%-13s%2s%-19s%2s%-19s%2s%-18s%2s%-15s%2s%-8s%2s%-6s%2s%-7s%n", " Номер рейса", "| ",
                "Тип самолета", "| ", "Пункт отправления", "| ", "Пункт прибытия", "| ",
                "Время отправления", "| ", "Время прибытия", "| ", "Дни", "| ", "Выход", "| ", "Статус");
        file.write(s);
        file.write("----------------------------------------------------" +
                "----------------------------------------------------------------------------------\n");

        for (Route route : routes) {
            s = String.format("%-13s%2s%-13s%2s%-19s%2s%-19s%2s%-18s%2s%-15s%2s%-8s%2s%-6s%2s%-7s%n", " " + route.getId(), "| ", route.getPlaneType(), "| ",
                    route.getSource(), "| ", route.getDestination(), "| ",
                    route.getDepartureTime(), "| ", route.getArrivalTime(), "| ",
                    route.getDays(), "| ", route.getGate(), "| ", route.getStatus());
            file.write(s);
        }
        file.close();
    }

    /*public static void sort(JTable table, int col) {
        DefaultTableModel tableModel = (DefaultTableModel)table.getModel();
        List<Route> list = new ArrayList<>();
        Vector data = tableModel.getDataVector();

        //todo записать время в поле отправления, забить на поле прибытие, сделать конструктор с этими полями, чекнуть, какие индексы что делают
        //todo перенести голос в модельку
        //todo выжить.
        for (int i = 0; i < table.getRowCount(); i++) {
            Vector row = (Vector) data.elementAt(i);
            list.add(new Route((String)row.get(0), (String)row.get(1), (String)row.get(2),
                    (String)row.get(3), (String) row.get(4), (String)row.get(6), (String)row.get(7), (String)row.get(5)));
        }

        Collections.sort(list, new MyComparator(col));

        tableModel.setRowCount(0);

        for(Route route : list) {
            tableModel.addRow(new Object[]{souvenir.getName(), souvenir.getManufacturer(), souvenir.getCountry(),
                    souvenir.getDate(), souvenir.getCost()});
        }
    }*/

    /*class MyComparator implements Comparator<Route> {
        int type = 0;
        MyComparator(int type) {
            this.type = type;
        }

        @Override
        public int compare(Route s1, Route s2) {
            switch (type) {
                case 0: {
                    return s1.getId().compareTo(s2.getId());
                }
                case 1: {
                    return s1.getPlaneType().compareTo(s2.getPlaneType());
                }
                case 2: {
                    return s1.getSource().compareTo(s2.getSource());
                }
                case 3: {
                    return s1.getDestination().compareTo(s2.getDestination());
                }
                case 4: {
                    return s1.().compareTo(s2.getDestination());
                }
            }
            return s1.getCost() < s2.getCost() ? -1 : 1;
        }
    }*/

    /*public void saveToFileAdmin(String searchString, List<Route> routes) throws IOException, ClassNotFoundException {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat sdfWithTime = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        Date date = calendar.getTime();
        String fileName = "Отчет по рейсам " + sdf.format(date) + " (администратор).txt";

        FileWriter file = new FileWriter(fileName, false);
        String s;



        s = String.format("%-13s%2s%-13s%2s%-19s%2s%-19s%2s%-18s%2s%-15s%2s%-6s%2s%-7s%n", " Номер рейса", "| ",
                "Тип самолета", "| ", "Пункт отправления", "| ", "Пункт прибытия", "| ",
                "Время отправления", "| ", "Время прибытия", "| ", "Выход", "| ", "Статус");
        file.write(s);
        file.write("--------------------------------------------\n");

        for(Route route : routes) {
            s = String.format("%-18s%2s%-15s%2s%-6s%2s%n",
                    route.getDepartureTime(), "| ", route.getArrivalTime(), "| ", route.getGate(), "| ");
            file.write(s);
        }
        file.close();
    }*/

    public PlaneInformation getPlaneInformation(String plane) throws IOException, ClassNotFoundException {
        return Connection.getPlaneInformation(plane);
    }
}
