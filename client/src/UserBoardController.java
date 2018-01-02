import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class UserBoardController extends Controller {
    private UserBoardView view;
    private int dayDepartures = 0;
    private int dayArrivals = 0;
    private int day = 0;
    private List<Route> routesDestination = null;
    private List<Route> routesArrivals = null;
    private List<Route> routes = null;

    public UserBoardController() {}

    @Override
    public void setController(JFrame view) {
        this.view = (UserBoardView)view;
    }

    public static void addColumns(JTable table, String columnName) {
        DefaultTableModel model = (DefaultTableModel)table.getModel();
        model.setRowCount(0);
        model.setColumnCount(5);

        String[] columnNames = {"Flight No.", "", "Time", "Gate", "Status"};
        int[] columnWidth = {100, 135, 45, 35, 150};

        for(int i = 0; i < 5; i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            columnNames[1] = columnName;
            column.setPreferredWidth(columnWidth[i]);
            column.setHeaderValue(columnNames[i]);
            column.setIdentifier(columnNames[i]);
            if(i == 2 || i == 3) {
                //setting alignment of the headers and the cells
                DefaultTableCellRenderer center = new DefaultTableCellRenderer();
                DefaultTableCellRenderer header = (DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer();
                center.setHorizontalAlignment(SwingConstants.CENTER);
                header.setHorizontalAlignment(SwingConstants.LEFT);
                column.setCellRenderer(center);
                column.setHeaderRenderer(header);
            }
        }
        table.getTableHeader().setResizingAllowed(false);
        table.getTableHeader().setReorderingAllowed(false);
    }

    public void addRoutes(boolean isInitializing, int type) throws IOException, ClassNotFoundException {
        DefaultTableModel tableModelDepartures = (DefaultTableModel)view.departuresTable.getModel();
        DefaultTableModel tableModelArrivals = (DefaultTableModel)view.arrivalsTable.getModel();
        int dayToAdd = 0;

        //if this func called not for the first time
        if(!isInitializing) {
            if(!routes.isEmpty())
                addTableRow(routes, tableModelDepartures, tableModelArrivals);
            if(!routes.isEmpty()) return;
            switch (type) {
                case 1: {
                    if (view.departuresTable.getRowCount() < 15 && routesDestination != null) {
                        addTableRow(routesDestination, tableModelDepartures, tableModelArrivals);
                        if(!routesDestination.isEmpty()) return;
                    }
                    break;
                }
                case 2: {
                    if (view.arrivalsTable.getRowCount() < 15 && routesArrivals != null) {
                        addTableRow(routesArrivals, tableModelDepartures, tableModelArrivals);
                        if(!routesArrivals.isEmpty()) return;
                    }
                    break;
                }
            }
        }

        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);

        //add to table all routes >= current time and if it's a new day, then all of them
        switch (type) {
            case 0: {
                if(!isInitializing) {
                    day++;
                    dayDepartures++;
                    dayArrivals++;
                }
                String clock = day != 0 ? "00:00" : view.timeLabel.getText();

                if(isInitializing) {
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                    Date currentTime = calendar.getTime();
                    clock = timeFormat.format(currentTime);
                }

                routes = getRoutesArray(day, "", clock);
                dayToAdd = day;
                if(routes.isEmpty()) return;
                break;
            }
            case 1: {
                if(!isInitializing) {
                    dayDepartures++;
                }
                String clock = dayDepartures != 0 ? "00:00" : view.timeLabel.getText();
                routesDestination = getRoutesArray(dayDepartures, "Destination", clock);
                dayToAdd = dayDepartures;
                if(routesDestination.isEmpty()) return;
                break;
            }
            case 2: {
                if(!isInitializing) {
                    dayArrivals++;
                }
                String clock = dayArrivals != 0 ? "00:00" : view.timeLabel.getText();
                routesArrivals = getRoutesArray(dayArrivals, "Source", clock);
                dayToAdd = dayArrivals;
                if(routesArrivals.isEmpty()) return;
            }
        }

        calendar.add(Calendar.DATE, dayToAdd);

        if((type == 1 && dayDepartures != 0) ||
                type == 0 && day != 0) {

            //placing Day+1 date
            pasteDate(calendar, tableModelDepartures);

            if(view.getNoCopiesRowDepartures() == 0) {
                view.setNoCopiesRowDepartures(view.departuresTable.getRowCount() - 1);
            }

            //center align for date row
            view.departuresTable.getColumn("Destination").setCellRenderer(new DefaultTableCellRenderer() {

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

        if((type == 2 && dayArrivals != 0) ||
                type == 0 && day != 0) {

            pasteDate(calendar, tableModelArrivals);

            if(view.getNoCopiesRowArrivals() == 0) {
                view.setNoCopiesRowArrivals(view.arrivalsTable.getRowCount() - 1);
            }

            view.arrivalsTable.getColumn("Source").setCellRenderer(new DefaultTableCellRenderer() {
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

        switch (type) {
            case 0: {
                addTableRow(translate(routes), tableModelDepartures, tableModelArrivals);
                break;
            }
            case 1: {
                addTableRow(translate(routesDestination), tableModelDepartures, tableModelArrivals);
                break;
            }
            case 2: {
                addTableRow(translate(routesArrivals), tableModelDepartures, tableModelArrivals);
                break;
            }
        }
    }

    private void addTableRow(List<Route> routes,
                            DefaultTableModel tableModelDepartures, DefaultTableModel tableModelArrivals) {
        Iterator<Route> it = routes.iterator();
        while(it.hasNext()) {
            Route route = it.next();
            if(route.getSource().equals("Минск")) {
                if(tableModelDepartures.getRowCount() >= 15)
                    continue;
                tableModelDepartures.addRow(new Object[]{route.getId(), route.getDestination(), route.getDepartureTime(),
                        route.getGate(), route.getStatus()});
            } else {
                if(tableModelArrivals.getRowCount() >= 15)
                    continue;
                tableModelArrivals.addRow(new Object[]{route.getId(), route.getSource(), route.getArrivalTime(),
                        route.getGate(), route.getStatus()});
            }
            it.remove();
        }
    }

    private List<Route> getRoutesArray(int day, String type, String clock) throws IOException, ClassNotFoundException {
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
        routes = model.getRoutes(tempDay, type, clock);
        calendar.add(Calendar.DATE, -day);

        return routes;
    }

    private void pasteDate(Calendar calendar, DefaultTableModel tableModel) {
        Date time = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        tableModel.addRow(new Object[]{"", dateFormat.format(time), "", "", ""});
    }

    private void refreshValues() {
        day = 0;
        dayDepartures = 0;
        dayArrivals = 0;
        routes.clear();
        if(routesDestination != null && routesArrivals != null) {
            routesDestination.clear();
            routesArrivals.clear();
        }
    }

    private List<Route> translate(List<Route> routes) {
        String text = "";
        for(Route route : routes) {
            if (route.getSource().equals("Минск")) {
                text = route.getDestination();
            } else {
                text = route.getSource();
            }
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
                case "Киев(Борисполь)": {
                    text = "KIEV(BORISPIL)";
                    break;
                }
                case "Амстердам": {
                    text = "AMSTERDAM";
                    break;
                }
                case "Барселона": {
                    text = "BARCELONA";
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
                case "Москва(Домодедово)": {
                    text = "MOSCOW(DOM)";
                    break;
                }
                case "Абу-Даби": {
                    text = "ABU-DABI";
                    break;
                }
                case "Багдад": {
                    text = "BAGHDAD";
                    break;
                }
                case "Москва(Шереметьево)": {
                    text = "MOSCOW(SHER)";
                    break;
                }
                case "Москва(Внуково)": {
                    text = "MOSCOW(VNY)";
                    break;
                }
                case "Екатеринбург": {
                    text = "EKATERINBURG";
                    break;
                }
            }
            if (route.getSource().equals("Минск")) {
                route.setDestination(text);
            } else {
                route.setSource(text);
            }
        }
        return routes;
    }

    public void refreshTable(boolean flag) throws IOException, ClassNotFoundException {
        if(flag) {
            if(!Model.checkTaskStatus().equals("active")) {
                return;
            }
        }
        refreshValues();
        view.isDisabled = true;
        DefaultTableModel model = (DefaultTableModel) view.departuresTable.getModel();
        model.setRowCount(0);
        model = (DefaultTableModel) view.arrivalsTable.getModel();
        model.setRowCount(0);
        addRoutes(true, 0);
        while(view.departuresTable.getRowCount() < 15) {
            addRoutes(false, 1);
        }
        while(view.arrivalsTable.getRowCount() < 15) {
            addRoutes(false, 2);
        }
        view.isDisabled = false;
    }

    public void removeTimeRow(String value, DefaultTableModel tableModel) {
        for(int i = 0; i < tableModel.getRowCount(); i++) {
            String tableValue = (String)tableModel.getValueAt(i, 2),
                    status = (String)tableModel.getValueAt(i, 4);
            if(tableValue.equals(value) || timeElapsed(tableValue, value) > 0) {
                if(status == null || !status.equals("DELAYED")) {
                    tableModel.removeRow(i);
                    i--;
                }
            }
            if(tableValue.equals("")) break;
        }
    }

    @Override
    public void back(TimerThread timerThread) {
        if(UserBoardView.updateThreadSwing != null) {
            UserBoardView.updateThreadSwing.setRunning(false);
        }
        int dialogButton = JOptionPane.YES_NO_OPTION;
        Object[] options = {"Да", "Нет"};
        int result = JOptionPane.showOptionDialog (view,
                "Вы действительно хотите выйти в главное меню?","Выход", dialogButton,
                JOptionPane.PLAIN_MESSAGE, null, options, null);
        if(result == JOptionPane.YES_OPTION) {
            view.timeLabel.removePropertyChangeListener(view.listener);
            view.dispose();
            LoginView.showView();
        }
        UserBoardView.updateThreadSwing = new UpdateThreadSwing(this.view);
        UserBoardView.updateThreadSwing.execute();
    }
}
