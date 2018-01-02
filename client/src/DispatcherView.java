import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.table.*;
import java.awt.*;
import java.awt.Color;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class DispatcherView extends JFrame implements ActionListener, MouseListener {
    public JPanel rootPanel;
    private JPanel panelGreen;
    private JPanel panelYellow;
    private JPanel panelRed;
    private JLabel labelGreen;
    public JLabel labelYellow;
    private JLabel labelRed;
    private JLabel labelGreen2;
    public JLabel labelClear;
    private DispatcherController controller;
    public static boolean infoCalled;
    public JButton backButton;
    public JTextField searchField;
    public JTabbedPane tabs;
    public JTable routesTable;
    public JButton searchButton;
    public JTextField destinationField;
    public JTextField dateField;
    public JTextField timeField;
    public JTextField sourceField;
    private JLabel dateLabel;
    private JLabel timeLabel;
    public JTextField flightIDField;
    public JTextField timeDepartureField;
    public JTextField timeArrivalField;
    public JTextField flightTimeField;
    public JTextField planeTypeField;
    public JTextField planeNameField;
    public JTextField flyHeightField;
    public JTextField fuelField;
    public JTextField maxTakeOffField;
    public JTextField maxLandingField;
    public JTextField capacityField;
    public JTextField maxLoad;
    private JButton saveToFile;
    public JButton showRouteTableButton;
    private JPanel statusPanel;

    private TimerThread timerThread;
    private Thread thread;

    public boolean isDisabled;
    public boolean searchCalled = false;
    private int noCopiesRow = 0;
    public PropertyChangeListener listener;

    public DispatcherView(String title) {
        super(title);
        setContentPane(rootPanel);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        timerThread = new TimerThread(dateLabel, timeLabel);
        timerThread.start();

        infoCalled = false;

        routesTable.getTableHeader().setResizingAllowed(false);
        routesTable.getTableHeader().setReorderingAllowed(false);

        routesTable.getModel().addTableModelListener((TableModelEvent e) -> {
            if (e.getType() == TableModelEvent.DELETE) {
                if (!isDisabled && !searchCalled) {
                    try {
                        if (routesTable.getRowCount() < 17)
                            controller.addRoutes(false);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        routesTable.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = routesTable.columnAtPoint(e.getPoint());
                controller.sort(routesTable, col);
            }
        });

        panelGreen.setBackground(Color.green);
        panelYellow.setBackground(Color.yellow);
        panelRed.setBackground(Color.red);

        JLabel[] labels = {labelGreen, labelRed, labelYellow, labelGreen2, labelClear};

        //setting listener and new cursor on labels
        for(JLabel label : labels) {
            label.addMouseListener(this);
            label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        backButton.addActionListener(this);
        backButton.setActionCommand("back");
        searchButton.addActionListener(this);
        searchButton.setActionCommand("search");
        saveToFile.addActionListener(this);
        saveToFile.setActionCommand("save");
        showRouteTableButton.addActionListener(this);
        showRouteTableButton.setActionCommand("showTable");

        tabs.addChangeListener((ChangeEvent e) -> {
            if (tabs.getSelectedIndex() == 0) searchField.grabFocus();

            //no reason to go in route tab without
            //clicking the button
            if (tabs.getSelectedIndex() == 1 &&
                    !infoCalled) {
                tabs.setSelectedIndex(0);
                JOptionPane.showMessageDialog(tabs.getRootPane(), "Для просмотра информации о рейсе используйте " +
                                "кнопку 'Информация', выбрав нужный маршрут на вкладке рейсов.\nПри необходимости воспользуйтесь поиском.",
                        "Очень важная информация", JOptionPane.WARNING_MESSAGE);
            }
        });

        setVisible(true);
        setSize(860, 598);
        setLocationRelativeTo(null);
        setResizable(false);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                controller.back(null);
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

        DispatcherView dispatcherView = new DispatcherView("Диспетчер");
        dispatcherView.controller = new DispatcherController();
        dispatcherView.controller.setController(dispatcherView);
        dispatcherView.controller.drawTable(0);

        try {
            dispatcherView.controller.addRoutes(true);
            dispatcherView.controller.addRoutes(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        dispatcherView.listener = (PropertyChangeEvent evt) -> {
            if (evt.getNewValue() != null) {
                if (evt.getNewValue().equals("00:00")) {
                    try {
                        dispatcherView.controller.refreshTable();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                DefaultTableModel tableModel = (DefaultTableModel) dispatcherView.routesTable.getModel();
                String value = (String)evt.getNewValue();

                if(!dispatcherView.searchCalled) {
                    dispatcherView.controller.callRegistration(value, tableModel);
                    dispatcherView.controller.removeTimeRow(value, tableModel);
                }
            }
        };
        dispatcherView.timeLabel.addPropertyChangeListener(dispatcherView.listener);

        dispatcherView.searchField.grabFocus();
    }

    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        switch(cmd) {
            case "showTable": {
                statusPanel.setEnabled(true);
                showRouteTableButton.setVisible(false);
                controller.drawTable(0);
                searchCalled = false;
                try {
                    controller.addRoutes(true);
                    controller.addRoutes(false);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                break;
            }
            case "back": {
                controller.back(null);
                break;
            }
            case "search": {
                searchCalled = true;
                statusPanel.setEnabled(false);
                showRouteTableButton.setVisible(false);
                controller.search(searchField.getText());
                break;
            }
            case "save": {
                controller.saveToFile();
                searchField.requestFocus();
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        String label = ((JLabel)me.getSource()).getName();
        int row = routesTable.getSelectedRow();

        //not to make next day routes have the same status as routes this day
        if(row < noCopiesRow || label.equals("yellow") || label.equals("clear")) {
            TableModel tableModel = routesTable.getModel();

            String id = (String) tableModel.getValueAt(row, 0),
                    source = (String) tableModel.getValueAt(row, 2),
                    gates = (String) tableModel.getValueAt(row, 6),
                    time = (String) tableModel.getValueAt(row, 4);

            switch (label) {
                case "green": {
                    tableModel.setValueAt("ПРИБЫЛ", row, 7);
                    thread = new Thread(() -> {
                        String destination = (String) tableModel.getValueAt(row, 3);
                        try {
                            controller.activateMic("arrive", id, source, destination, gates, time);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    thread.start();
                    break;
                }
                case "green2": {
                    if (!(tableModel.getValueAt(row, 2)).equals("Минск")) {
                        tableModel.setValueAt("", row, 7);
                        break;
                    }
                    tableModel.setValueAt("ПОСАДКА", row, 7);

                    thread = new Thread(() -> {
                        String destination = (String) tableModel.getValueAt(row, 3);
                        try {
                            controller.activateMic("boarding", id, source, destination, gates, time);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    thread.start();
                    break;
                }
                case "yellow": {
                    if (!(tableModel.getValueAt(row, 2)).equals("Минск")) {
                        tableModel.setValueAt("", row, 7);
                        break;
                    }
                    tableModel.setValueAt("РЕГИСТРАЦИЯ", row, 7);
                    thread = new Thread(() -> {
                        String destination = (String) tableModel.getValueAt(row, 3);
                        try {
                            controller.activateMic("registration", id, source, destination, gates, time);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    thread.start();
                    break;
                }
                case "red": {
                    tableModel.setValueAt("ОТЛОЖЕН", row, 7);
                    thread = new Thread(() -> {
                        String destination = (String) tableModel.getValueAt(row, 3);
                        try {
                            controller.activateMic("delayed", id, source, destination, gates, time);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    thread.start();
                    break;
                }
                case "clear": {
                    tableModel.setValueAt("", row, 7);
                    break;
                }
            }
            String status = (String) routesTable.getModel().getValueAt(row, 7);
            controller.updateStatus(id, source, time, status);
            routesTable.changeSelection(row + 1, 7, false, false);
            routesTable.changeSelection(row, 7, false, false);
        }
    }
    public void mouseEntered(MouseEvent me) {}
    public void mouseExited(MouseEvent me) {}
    public void mouseReleased(MouseEvent me) {}
    public void mousePressed(MouseEvent me) {}

    private void createUIComponents() {
        routesTable = new JTable() {

            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);

                c.setBackground(getBackground());
                c.setForeground(Color.black);

                int modelRow = convertRowIndexToModel(row);

                String days;
                Color gray = new java.awt.Color(209, 208, 206);

                if(searchCalled) {
                    days = (String) getModel().getValueAt(modelRow, 7);
                    Date now = new Date();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(now);

                    int tempDay = calendar.get(Calendar.DAY_OF_WEEK)-1;
                    if(tempDay == 0) {
                        tempDay = 7;
                    }
                    if(!days.contains(Integer.toString(tempDay))) {
                        c.setBackground(gray);
                    }
                    return c;
                }

                Color green = new java.awt.Color(87, 233, 100);
                Color yellow = new java.awt.Color(255, 232, 124);
                Color red = new java.awt.Color(220, 56, 31);

                String type = (String) getModel().getValueAt(modelRow, 7);
                if(modelRow > noCopiesRow && !type.equals("РЕГИСТРАЦИЯ") && !type.equals("") && !searchCalled) {
                    getModel().setValueAt("", modelRow, 7);
                }
                //if (type != null) {
                    switch (type) {
                        case "ПРИБЫЛ":
                        case "ПОСАДКА": {
                            c.setBackground(green);
                            break;
                        }
                        case "РЕГИСТРАЦИЯ": {
                            c.setBackground(yellow);
                            break;
                        }
                        case "ОТЛОЖЕН": {
                            c.setBackground(red);
                            break;
                        }
                    }
                //}
                type = (String) getModel().getValueAt(modelRow, 1);
                if(type != null && type.contains(".")) {
                    c.setBackground(Color.orange);
                }
                return c;
            }

            @Override
            public boolean isCellEditable (int row, int column)
            {
                return column == 7 || column == 8;
            }
        };
    }

    public void setNoCopiesRow(int noCopiesRow) {
        this.noCopiesRow = noCopiesRow;
    }
}