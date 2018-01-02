import res.speech.synthesiser.SynthesiserV2;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import yandextranslator.language.Language;
import yandextranslator.translate.Translate;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class DispatcherController extends Controller implements Speakable {
    private static DispatcherView view;
    private int day = 0;
    private List<Route> routes = null;

    public DispatcherController(){}

    @Override
    public void setController(JFrame view) {
        DispatcherController.view = (DispatcherView) view;
    }

    @Override
    public void speak(String input) {
        SynthesiserV2 synthesizer = new SynthesiserV2("AIzaSyBOti4mM-6x9WDnZIjIeyEU21OpBXqWBgw");
        try {
            AdvancedPlayer player = new AdvancedPlayer(synthesizer.getMP3Data(input));
            player.play();
        } catch (IOException | JavaLayerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void activateMic(String type, String routeID, String source, String destination, String gates, String time) throws IOException {
        try {
            Translate.setKey("trnsl.1.1.20171129T214153Z.d9cede56556fb5e9.c6efd20d2b9ac3af60b78d418cd213ed24c6673a");
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (type) {
            case "warn": {
                String message = "Вниманию пассажиров. Регистрация на рейс " + parseRouteID(routeID) + ".| Пункт назначения - "
                        + parseRoutePoint(destination, false) +
                        ". Время отправления " + parseTime(time) + " заканчивается через 10 минут. " +
                        " Пожалуйста, пройдите к стойкам регистрации. " +
                        " Или воспользуйтесь киосками самостоятельной регистрации, находящимися в здании аэропорта.";
                speak(message);
                speak("Attention, passengers. Registration for flight number " + parseRouteID(routeID) + "|" +
                        parseRoutePoint(source, true) + "|" + parseRoutePoint(destination, true) +
                        "| will be finished in 10 minutes. Please, address to the registration desks");
                break;
            }
            case "arrive": {
                String fromMinskStr = " время отправления "  + parseTime(time);
                boolean isFromMinsk = source.equals("Минск");

                String message = "Вниманию пасажиров. Самолёт рейса|номер " + parseRouteID(routeID) + "|"
                        + parseRoutePoint(source, false) + "|" + parseRoutePoint(destination, false) + "|";
                if(isFromMinsk) {
                    message += fromMinskStr;
                }

                message += " прибыл. Пожалуйста, пройдите к выходу|" + gates.substring(0, 1) + "|" + gates.substring(1);
                speak(message);
                speak("Attention, passengers. Flight plane|number " + parseRouteID(routeID) + "|" +
                        parseRoutePoint(source, true) + "|" + parseRoutePoint(destination, true) +
                        "| have arrived. Please, proceed to the gates " + gates);
                break;
            }
            case "registration": {
                String message = "Начинается регистрация на рейс " + parseRouteID(routeID) + ".| Пункт назначения - "
                        + parseRoutePoint(destination, false) +
                        ". Время отправления " + parseTime(time) + ". Пожалуйста, пройдите к стойкам регистрации. " +
                        "Или воспользуйтесь киосками самостоятельной регистрации, находящимися в здании аэропорта.";
                speak(message);
                speak("Attention, passengers. Registration for flight number " + parseRouteID(routeID) + "|" +
                        parseRoutePoint(source, true) + "|" + parseRoutePoint(destination, true) +
                        "| has started. Please, address to the registration desks");
                break;
            }
            case "boarding": {
                String message = "Вниманию пасажиров. Производится посадка на самолёт рейса|номер " + parseRouteID(routeID) + "|"
                        + parseRoutePoint(source, false) + "|" + parseRoutePoint(destination, false) +
                        "| время отправления "  + parseTime(time) +
                        ". Пожалуйста, приготовьте свои посадочные талоны|и|паспорта| и пройдите к выходу|"
                        + gates.substring(0, 1) + "|" + gates.substring(1);
                speak(message);
                speak("Attention, please. All passengers for flight number " + parseRouteID(routeID) + "|" +
                        parseRoutePoint(source, true) + "|" + parseRoutePoint(destination, true)
                        + "| please have your boarding passes and passports ready for boarding." +
                        " Flight " + parseRouteID(routeID)  + ". " +
                        parseRoutePoint(source, true) + "|" + parseRoutePoint(destination, true) +
                        "|now boarding at gate " + gates);
                break;
            }
            case "delayed": {
                String _time = "| время";
                if(source.equals("Минск")) {
                    _time += " отправления";
                } else {
                    _time += " прибытия ";
                }
                _time += parseTime(time);

                String message = "Вниманию пасажиров. Рейс|номер " + parseRouteID(routeID) + "|"
                        + parseRoutePoint(source, false) + "|" + parseRoutePoint(destination, false) + _time +
                        " отложен на неопределённое время. Пожалуйста, ожидайте дальнейшей информации.|" +
                        " Аэропорт приносит извинения за возникшие неудобства.";
                speak(message);
                speak("Attention, passengers. Flight number" + parseRouteID(routeID) + "|"
                        + parseRoutePoint(source, true) + parseRoutePoint(destination, true) +
                        ", has been delayed. Please, stand by. We are apologizing for inconvenience.");
                break;
            }
        }
    }

    public static void information(JTabbedPane tabs, int row) {
        tabs.setSelectedIndex(1);

        String source = (String)view.routesTable.getValueAt(row, 2);

        //filling in the fields
        view.flightIDField.setText((String)view.routesTable.getValueAt(row, 0));
        view.sourceField.setText(source);
        view.destinationField.setText((String)view.routesTable.getValueAt(row, 3));

        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        Date time = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        view.dateField.setText(dateFormat.format(time));

        String departure, arrival;

        if(source.equals("Минск")) {
            departure = (String)view.routesTable.getModel().getValueAt(row, 4);
            arrival = (String)view.routesTable.getModel().getValueAt(row, 5);
        } else {
            departure = (String)view.routesTable.getModel().getValueAt(row, 5);
            arrival = (String)view.routesTable.getModel().getValueAt(row, 4);
        }
        view.timeDepartureField.setText(departure);
        view.timeArrivalField.setText(arrival);

        long elapsedTime = timeElapsed(departure, arrival);
        long minute = (elapsedTime / (1000 * 60)) % 60;
        long hour = (elapsedTime / (1000 * 60 * 60)) % 24;

        String flyTime = String.format("%02d:%02d", hour, minute);
        view.flightTimeField.setText(flyTime);

        String plane = (String)view.routesTable.getValueAt(row, 1);
        view.planeTypeField.setText(plane);

        Model model = new Model();
        PlaneInformation planeInformation = null;

        try {
            planeInformation = model.getPlaneInformation(plane);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Произошла ошибка считывания информации по рейсу.",
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
            tabs.setSelectedIndex(0);
        }

        view.planeNameField.setText(planeInformation.getName());
        view.maxTakeOffField.setText(Integer.toString(planeInformation.getMaxTakeOff()));
        view.maxLandingField.setText(Integer.toString(planeInformation.getMaxLanding()));
        view.maxLoad.setText(Integer.toString(planeInformation.getMaxLoad()));
        view.capacityField.setText(Integer.toString(planeInformation.getCapacity()));
        view.flyHeightField.setText(Integer.toString(planeInformation.getFlyHeight()));
        view.fuelField.setText(Integer.toString(planeInformation.getFuel()));
    }

    public void search(String route) {
        Model model = new Model();
        if(!checkConnection()) {
            /*synchronized (lockObject) {
                lockObject.notify();
            }*/
            return;
        }
        try {
            List<Route> routes = model.getRoutes(route, 1);
            ((DefaultTableModel)view.routesTable.getModel()).setRowCount(0);

            drawTable(1);
            addTableRow(routes, (DefaultTableModel) view.routesTable.getModel());
            view.showRouteTableButton.setVisible(true);
            refreshValues();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addRoutes(boolean isInitializing) {
        DefaultTableModel tableModelRoutes = (DefaultTableModel)view.routesTable.getModel();
        int dayToAdd;

        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);

        //add to table all today's routes
        if(!isInitializing) {
            day++;
        }

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        Date currentTime = calendar.getTime();
        String clock = timeFormat.format(currentTime);

        try {
            if(day != 0) clock = "00:00";
            routes = getRoutesArray(day, clock);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(routes.isEmpty())
            return;

        dayToAdd = day;
        calendar.add(Calendar.DATE, dayToAdd);

        if(day != 0) {
            //placing Day+1 date
            pasteDate(calendar, tableModelRoutes);
            view.setNoCopiesRow(view.routesTable.getRowCount()-1);
            //center align for date row
            view.routesTable.getColumn("Отправление").setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    DefaultTableCellRenderer renderer = (DefaultTableCellRenderer) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                    renderer.setHorizontalAlignment(SwingConstants.LEFT);

                    String date = (String)value;

                    if (date.contains(".")) {
                        renderer.setHorizontalAlignment(SwingConstants.CENTER);
                    }
                    return renderer;
                }
            });
        }

        calendar.add(Calendar.DATE, -dayToAdd);

        addTableRow(routes, tableModelRoutes);
    }

    private void addTableRow(List<Route> routes,
                             DefaultTableModel tableModelRoutes) {
        Iterator<Route> it = routes.iterator();
        while(it.hasNext()) {
            Route route = it.next();

            String source = route.getSource(), destination = route.getDestination();
            String objectToAdd;

            if(view.searchCalled) {
               objectToAdd = route.getDays();
            } else {
                objectToAdd = route.getStatus();
            }

            if(route.getSource().equals("Минск")) {
                tableModelRoutes.addRow(new Object[]{route.getId(), route.getPlaneType(), source, destination, route.getDepartureTime(),
                        route.getArrivalTime(),
                        route.getGate(), objectToAdd, "Информация"});
            } else {
                tableModelRoutes.addRow(new Object[]{route.getId(), route.getPlaneType(), source, destination, route.getArrivalTime(),
                        route.getDepartureTime(),
                        route.getGate(), objectToAdd, "Информация"});
            }
            it.remove();
        }
    }

    public void refreshTable() throws IOException, ClassNotFoundException {
        refreshValues();

        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);

        int tempDay = calendar.get(Calendar.DAY_OF_WEEK)-1;
        if(tempDay == 0) {
            tempDay = 7;
        }
        tempDay--;
        if(tempDay == 0) {
            tempDay = 7;
        }

        Connection.refreshStatus(Integer.toString(tempDay));
        view.isDisabled = true;

        DefaultTableModel model = (DefaultTableModel)view.routesTable.getModel();
        model.setRowCount(0);

        addRoutes(true);
        addRoutes(false);

        view.isDisabled = false;
    }

    public void removeTimeRow(String value, DefaultTableModel tableModel) {
        if(!view.searchCalled) {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String tableValue = (String) tableModel.getValueAt(i, 4),
                        status = (String) tableModel.getValueAt(i, 6);
                float timeElapsed = timeElapsed(tableValue, value);
                if (tableValue.equals(value) || timeElapsed > 0) {
                    if (status == null || !status.equals("ОТЛОЖЕН")) {
                        int x = view.labelClear.getX(),
                                y = view.labelClear.getY();

                        view.routesTable.removeRowSelectionInterval(0, i);
                        view.routesTable.changeSelection(i, 6, false, false);

                        //clearing current flight status
                        MouseEvent me = new MouseEvent(view.labelClear, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(),
                                0, x, y, 1, false);
                        view.labelClear.dispatchEvent(me);

                        tableModel.removeRow(i);
                        i--;
                    }
                }
                if (tableValue.equals("")) break;
            }
        }
    }

    public void callRegistration(String value, DefaultTableModel tableModel) {
        for(int i = 0; i < tableModel.getRowCount(); i++) {
            String id = (String) tableModel.getValueAt(i, 0),
                    time = (String) tableModel.getValueAt(i, 4),
                    status = (String) tableModel.getValueAt(i, 7),
                    source = (String) tableModel.getValueAt(i, 2),
                    destination = (String) tableModel.getValueAt(i, 3);

            float timeElapsed = timeElapsed(time, value);

            //start flight registration 2 hours before the actual departure event
            if (timeElapsed == -7200000.0 && !status.equals("ОТЛОЖЕН") && source.equals("Минск")) {
                int x = view.labelYellow.getX(),
                        y = view.labelYellow.getY();

                view.routesTable.removeRowSelectionInterval(0, i);
                view.routesTable.changeSelection(i, 0, false, false);

                MouseEvent me = new MouseEvent(view.labelYellow, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(),
                        0, x, y, 1, false);
                view.labelYellow.dispatchEvent(me);
            }

            //warn about registration ending in 10 mins
            if (timeElapsed == -3000000.0 && status.equals("РЕГИСТРАЦИЯ") && source.equals("Минск")) {
                try {
                    activateMic("warn", id, source, destination, null, time);
                } catch (Exception e){}
            }

            //end flight registration in 40 minutes before the actual departure event
            if ((timeElapsed >= -2400000.0 && timeElapsed <= 0) && status.equals("РЕГИСТРАЦИЯ") && source.equals("Минск")) {
                int x = view.labelClear.getX(),
                        y = view.labelClear.getY();

                view.routesTable.removeRowSelectionInterval(0, i);
                view.routesTable.changeSelection(i, 0, false, false);

                MouseEvent me = new MouseEvent(view.labelClear, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(),
                        0, x, y, 1, false);
                view.labelClear.dispatchEvent(me);
            }
            if (time.equals("")) break;
        }
    }

    public void updateStatus(String id, String source, String time, String status) {
        Model model = new Model();
        if(!checkConnection()) {
            view.dispose();
            return;
        }
        try {
            model.updateStatus(id, source, time, status);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<Route> getRoutesArray(int day, String clock) throws IOException, ClassNotFoundException {
        Model model = new Model();
        if(!checkConnection()) {
            view.dispose();
            return null;
        }
        List<Route> routes;
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);

        calendar.add(Calendar.DATE, day);
        int tempDay = calendar.get(Calendar.DAY_OF_WEEK)-1;
        if(tempDay == 0) {
            tempDay = 7;
        }
        routes = model.getRoutes(tempDay, "currentAll", clock);
        calendar.add(Calendar.DATE, -day);

        return routes;
    }

    private void pasteDate(Calendar calendar, DefaultTableModel tableModel) {
        Date time = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        tableModel.addRow(new Object[]{"", dateFormat.format(time), "", "", "", "", "", ""});
    }

    private void refreshValues() {
        day = 0;
        routes.clear();
        view.setNoCopiesRow(0);
    }

    public void drawTable(int type) {
        DefaultTableModel model = (DefaultTableModel)view.routesTable.getModel();
        String columnToAdd;
        model.setColumnCount(9);
        model.setRowCount(0);

        if(type == 1) {
            columnToAdd = "Дни";
        } else {
            columnToAdd = "Статус";
        }

        String[] columnNames = {"Номер рейса", "Тип самолета", "Отправление", "Прибытие",
                "Время", "другое время", "Выход", columnToAdd, " "};
        int[] columnWidth = {220, 180, 245, 240, 110, 110, 120, 180, 270};

        for (int i = 0; i < model.getColumnCount(); i++) {
            TableColumn col = view.routesTable.getColumnModel().getColumn(i);
            col.setPreferredWidth(columnWidth[i]);
            col.setHeaderValue(columnNames[i]);
            col.setIdentifier(columnNames[i]);
            if (i == 8) {
                col.setIdentifier("Button");
                view.routesTable.getColumn("Button").setCellRenderer(new ButtonRenderer());
                view.routesTable.getColumn("Button").setCellEditor(
                        new ButtonEditor(new JCheckBox(), view.tabs));
            }
        }

        TableColumnModel tcm = view.routesTable.getColumnModel();
        tcm.removeColumn(tcm.getColumn(5));
    }

    private String parseRouteID(String id) {
        char[] parts = new char[id.length()-2];
        id = id.replaceAll("[-/]", "");
        id.getChars(0, id.length(), parts, 0);

        //String[] parts = id.split("\\|");
        StringBuilder sb = new StringBuilder();

        String delimiter = "|";

        for(char c : parts) {
            sb.append(c);
            sb.append(delimiter);
        }

        return sb.toString();
    }

    private String parseRoutePoint(String dest, boolean isEnglish) {
        String destinationToTranslate = dest.replaceAll("[()]", " ");
        try {
            if(isEnglish) {
                destinationToTranslate = Translate.execute(destinationToTranslate, Language.RUSSIAN, Language.ENGLISH);
            } else {
                destinationToTranslate = dest.toLowerCase();
            }
        } catch (Exception e) {}
        switch (destinationToTranslate) {
            case "Киев Борисполь": {
                return "Киев, аэропорт Борисполь.";
            }
            case "Москва Дом": {
                return "Москва, аэропорт Домодедово.";
            }
            case "Москва Вну ": {
                return "Москва, аэропорт Внуково.";
            }
            case "Москва Шер": {
                return "Москва, аэропорт Шереметьево.";
            }
        }
        return destinationToTranslate;
    }

    private String parseTime(String time) {
        String[] splitTime = time.split(":");
        String returnTime = splitTime[0] + ",час";

        switch (splitTime[0]) {
            case "02": case "03": case "04": case "22": case "23": {
                returnTime += "а";
                break;
            }
            default: {
                returnTime += "ов";
            }
        }

        returnTime += " " + splitTime[1] + " минут";

        switch (splitTime[1]) {
            case "01": case "21": case "31": case "41": {
                returnTime += "а";
                break;
            }
            case "02": case "03": case "04":
            case "22": case "23": case "24":
            case "32": case "33": case "34":
            case "42": case "43": case "44": {
                returnTime += "ы";
            }
        }
        return returnTime;
    }

    /*private String translate(String text) {
        switch (text) {
            case "MINSK": {
                text = "Минск";
                break;
            }
            case "BAKU": {
                text = "Баку";
                break;
            }
            case "ASHHABAD": {
                text = "Ашхабад";
                break;
            }
            case "ALMATY": {
                text = "Алматы";
                break;
            }
            case "ASTANA": {
                text = "Астана";
                break;
            }
            case "BUDAPESHT": {
                text = "Будапешт";
                break;
            }
            case "KIEV(BORISPIL)": {
                text = "Киев (Борисполь)";
                break;
            }
            case "AMSTERDAM": {
                text = "Амстердам";
                break;
            }
            case "BARSELONA": {
                text = "Барселона";
                break;
            }
            case "MILAN": {
                text = "Милан";
                break;
            }
            case "ROME": {
                text = "Рим";
                break;
            }
            case "VARSHAVA": {
                text = "Варшава";
                break;
            }
            case "ST-PETERSBURG": {
                text = "Санкт-Петербург";
                break;
            }
            case "KALININGRAD": {
                text = "Калининград";
                break;
            }
            case "MOSCOW(DOM)": {
                text = "Москва (Домодедово)";
                break;
            }
            case "ABU-DABI": {
                text = "Абу-Даби";
                break;
            }
            case "BAGDAD": {
                text = "Багдад";
                break;
            }
            case "MOSCOW(SHER)": {
                text = "Москва (Шереметьево)";
                break;
            }
            case "MOSCOW(VNY)": {
                text = "Москва (Внуково)";
                break;
            }
            case "EKATERINBURG": {
                text = "Екатеринбург";
                break;
            }
        }
        return text;
    }*/

    public void sort(JTable table, int col) {
        //Model.sort(table, col);
    }

    public void saveToFile(){
        Model model = new Model();
        try {
            model.saveToFile(1, null, null);
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
            if(view.timeField != null) {
                view.timeField.removePropertyChangeListener(view.listener);
            }
            view.dispose();
            LoginView.showView();
        }
    }
}