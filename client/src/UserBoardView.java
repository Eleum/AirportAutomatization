import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import static java.awt.event.KeyEvent.VK_ESCAPE;

public class UserBoardView extends JFrame implements KeyListener {
    private JFrame frame = this;
    private JPanel rootPanel;
    private JLabel dateLabel;
    private TimerThread timerThread;
    public static UpdateThreadSwing updateThreadSwing;
    public JTable departuresTable;
    public JTable arrivalsTable;
    public UserBoardController controller;
    public JLabel timeLabel;
    public boolean isDisabled;
    public PropertyChangeListener listener;
    private int noCopiesRowDepartures = 0;
    private int noCopiesRowArrivals = 0;

    public UserBoardView() {
        super("Рейсы");
        setContentPane(rootPanel);

        setVisible(true);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

        timerThread = new TimerThread(dateLabel, timeLabel);
        timerThread.start();

        UserBoardController.addColumns(departuresTable, "Destination");
        UserBoardController.addColumns(arrivalsTable, "Source");

        rootPanel.addKeyListener(this);
        super.addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e) {
                rootPanel.requestFocus();
                updateThreadSwing = new UpdateThreadSwing(frame);
                updateThreadSwing.execute();
            }
        });
        super.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                try {
                    controller.back(timerThread);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
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

        UserBoardView userBoardView = new UserBoardView();
        userBoardView.controller = new UserBoardController();
        userBoardView.controller.setController(userBoardView);
        userBoardView.setSize(970, 653);
        userBoardView.setResizable(false);
        userBoardView.setLocationRelativeTo(null);

        //setting listeners to make tables be full and synchronized all the time
        userBoardView.departuresTable.getModel().addTableModelListener((TableModelEvent e) -> {
            if (e.getType() == TableModelEvent.DELETE) {
                if (!userBoardView.isDisabled) {
                    UserBoardView.updateThreadSwing.setRunning(false);
                    try {
                        userBoardView.controller.addRoutes(false, 1);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    UserBoardView.updateThreadSwing = new UpdateThreadSwing(userBoardView);
                    UserBoardView.updateThreadSwing.execute();
                }
            }
        });
        userBoardView.arrivalsTable.getModel().addTableModelListener((TableModelEvent e) -> {
            if (!userBoardView.isDisabled) {
                if (e.getType() == TableModelEvent.DELETE) {
                    UserBoardView.updateThreadSwing.setRunning(false);
                    try {
                        userBoardView.controller.addRoutes(false, 2);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    UserBoardView.updateThreadSwing = new UpdateThreadSwing(userBoardView);
                    UserBoardView.updateThreadSwing.execute();
                }
            }
        });

        //checking for refreshing table on 00:00 and rows deleting
        userBoardView.listener = (PropertyChangeEvent evt) -> {
            if (evt.getNewValue() != null) {
                if (evt.getNewValue().equals("00:00")) {
                    try {
                        userBoardView.controller.refreshTable(false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            userBoardView.controller.removeTimeRow((String) evt.getNewValue(),
                    (DefaultTableModel) userBoardView.departuresTable.getModel());
            userBoardView.controller.removeTimeRow((String) evt.getNewValue(),
                    (DefaultTableModel) userBoardView.arrivalsTable.getModel());
        };
        userBoardView.timeLabel.addPropertyChangeListener(userBoardView.listener);

        try {
            userBoardView.controller.addRoutes(true, 0);
            while(userBoardView.departuresTable.getRowCount() < 15) {
                userBoardView.controller.addRoutes(false, 1);
            }
            while(userBoardView.arrivalsTable.getRowCount() < 15) {
                userBoardView.controller.addRoutes(false, 2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void keyReleased(KeyEvent e) {
        if(e.getKeyCode() == VK_ESCAPE) {
            controller.back(timerThread);
        }
    }
    public void keyTyped(KeyEvent e) {}
    public void keyPressed(KeyEvent e) {}

    private void createUIComponents() {
        departuresTable = new JTable() {
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                int modelRow = convertRowIndexToModel(row);

                Color green = new java.awt.Color(87, 233, 100);
                Color yellow = new java.awt.Color(255, 232, 124);
                Color red = new java.awt.Color(220, 56, 31);

                c.setBackground(getBackground());
                c.setForeground(getForeground());

                if (column == 1) {
                    String type = (String) getModel().getValueAt(modelRow, column - 1);
                    if (type.equals("")) {
                        c.setForeground(Color.white);
                    }
                }

                if (column == 2 || column == 3) {
                    c.setForeground(Color.white);
                }

                if (column == 4) {
                    String type = (String) getModel().getValueAt(modelRow, column);
                    if (type != null) {
                        switch (type.toUpperCase()) {
                            case "ПРИБЫЛ":
                            case "ARRIVED": {
                                if(modelRow > noCopiesRowDepartures) {
                                    getModel().setValueAt("", modelRow, column);
                                    break;
                                }
                                c.setForeground(green);
                                getModel().setValueAt("ARRIVED", modelRow, column);
                                break;
                            }
                            case "ПОСАДКА":
                            case "BOARDING": {
                                if(modelRow > noCopiesRowDepartures) {
                                    getModel().setValueAt("", modelRow, column);
                                    break;
                                }
                                c.setForeground(green);
                                getModel().setValueAt("BOARDING", modelRow, column);
                                break;
                            }
                            case "РЕГИСТРАЦИЯ":
                            case "REGISTRATION": {
                                if(modelRow > noCopiesRowDepartures) {
                                    getModel().setValueAt("", modelRow, column);
                                    break;
                                }
                                getModel().setValueAt("REGISTRATION", modelRow, column);
                                c.setForeground(yellow);
                                break;
                            }
                            case "ОТЛОЖЕН":
                            case "DELAYED": {
                                if(modelRow > noCopiesRowDepartures) {
                                    getModel().setValueAt("", modelRow, column);
                                    break;
                                }
                                getModel().setValueAt("DELAYED", modelRow, column);
                                c.setForeground(red);
                                break;
                            }
                        }
                    }
                }
                return c;
            }
        };

        arrivalsTable = new JTable() {
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                int modelRow = convertRowIndexToModel(row);

                Color green = new java.awt.Color(87, 233, 100);
                Color yellow = new java.awt.Color(255, 232, 124);
                Color red = new java.awt.Color(220, 56, 31);

                c.setBackground(getBackground());
                c.setForeground(getForeground());

                if (column == 1) {
                    String type = (String) getModel().getValueAt(modelRow, column - 1);
                    if (type.equals("")) {
                        c.setForeground(Color.white);
                    }
                }

                if (column == 2 || column == 3) {
                    c.setForeground(Color.white);
                }

                if (column == 4) {
                    String type = (String) getModel().getValueAt(modelRow, column);
                    if (type != null) {
                        switch (type.toUpperCase()) {
                            case "ПРИБЫЛ": case "ARRIVED": {
                                if(modelRow > noCopiesRowArrivals) {
                                    getModel().setValueAt("", modelRow, column);
                                    break;
                                }
                                c.setForeground(green);
                                getModel().setValueAt("ARRIVED", modelRow, column);
                                break;
                            }
                            case "ПОСАДКА": case "BOARDING": {
                                if(modelRow > noCopiesRowArrivals) {
                                    getModel().setValueAt("", modelRow, column);
                                    break;
                                }
                                c.setForeground(green);
                                getModel().setValueAt("BOARDING", modelRow, column);
                                break;
                            }
                            case "РЕГИСТРАЦИЯ": case "REGISTRATION": {
                                if(modelRow > noCopiesRowArrivals) {
                                    getModel().setValueAt("", modelRow, column);
                                    break;
                                }
                                getModel().setValueAt("REGISTRATION", modelRow, column);
                                c.setForeground(yellow);
                                break;
                            }
                            case "ОТЛОЖЕН": case "DELAYED": {
                                if(modelRow > noCopiesRowArrivals) {
                                    getModel().setValueAt("", modelRow, column);
                                    break;
                                }
                                getModel().setValueAt("DELAYED", modelRow, column);
                                c.setForeground(red);
                                break;
                            }
                        }
                    }
                }
                return c;
            }
        };
    }

    public int getNoCopiesRowDepartures() {
        return noCopiesRowDepartures;
    }

    public int getNoCopiesRowArrivals() {
        return noCopiesRowArrivals;
    }

    public void setNoCopiesRowDepartures(int noCopiesRow) {
        this.noCopiesRowDepartures = noCopiesRow;
    }

    public void setNoCopiesRowArrivals(int noCopiesRow) {
        this.noCopiesRowArrivals = noCopiesRow;
    }
}
