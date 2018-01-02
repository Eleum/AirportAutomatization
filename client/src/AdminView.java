import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.XChartPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class AdminView extends JFrame implements ActionListener {
    private JButton addButton;
    public JTextField idField;
    public JTextField sourceField;
    public JTextField destinationField;
    public JTextField departureTimeField;
    public JTextField arriveTimeField;
    public JTextField daysField;
    public JTextField planeTypeField;
    public JTextField gateField;
    private JButton backButton;
    private JPanel rootPanel;
    private JTabbedPane tabs;
    private JTable tableUsers;
    private JButton refreshButton;
    private JButton findUserButton;
    private JTextField findUserField;
    private JButton findRouteButton;
    private JTextField findRouteField;
    private JButton navigationBack;
    private JButton navigationForward;
    private JButton deleteButton;
    private JButton editButton;
    public JPanel editPanel;
    private JPanel inputsPanel;
    private JScrollPane scrollPane;
    private JButton editExitButton;
    private JButton graphButton;
    private JButton fileWriteButton;
    public AdminViewController controller;
    private int currentID;
    private boolean ignore = false;
    private int height;

    public AdminView(String title) {
        super(title);
        setContentPane(rootPanel);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addButton.addActionListener(this);
        addButton.setActionCommand("add");
        backButton.addActionListener(this);
        backButton.setActionCommand("back");
        super.getRootPane().setDefaultButton(addButton);

        DefaultTableModel model = (DefaultTableModel)tableUsers.getModel();
        String[] columns = {"Номер", "Диспетчер", "Администратор"};
        for(String col : columns) {
            model.addColumn(col);
        }
        model.addTableModelListener((TableModelEvent e) -> {
            if(!ignore) {
                boolean[] types = new boolean[2];
                int row = e.getFirstRow();
                int column = e.getColumn();

                if (column != 1 && column != 2) {
                    return;
                }

                types[0] = (boolean) model.getValueAt(row, 2);
                types[1] = (boolean) model.getValueAt(row, 1);

                //if we remove admin property from CURRENT logged admin
                int currentID = getCurrentID();
                if (column == 2 && model.getValueAt(row, 0).equals(currentID)) {
                    if (controller.warn() == 0) {
                        controller.editUserType(currentID, types);
                        dispose();
                        LoginView.showView();
                    } else {
                        //this calls change of cell which triggers this listener again causing update in DB
                        //using ignore flag will temporary disable listener
                        ignore = true;
                        model.setValueAt(true, row, 2);
                        ignore = false;
                    }
                } else {
                    int id = (int)model.getValueAt(row, 0);
                    controller.editUserType(id, types);
                }
            }
        });

        refreshButton.setVisible(false);
        refreshButton.addActionListener(this);
        refreshButton.setActionCommand("refresh");
        findRouteButton.addActionListener(this);
        findRouteButton.setActionCommand("findroute");
        findUserButton.addActionListener(this);
        findUserButton.setActionCommand("finduser");
        navigationBack.addActionListener(this);
        navigationBack.setActionCommand("navigationback");
        navigationForward.addActionListener(this);
        navigationForward.setActionCommand("navigationforward");
        editButton.addActionListener(this);
        editButton.setActionCommand("edit");
        deleteButton.addActionListener(this);
        deleteButton.setActionCommand("delete");
        editExitButton.addActionListener(this);
        editExitButton.setActionCommand("editExit");
        graphButton.addActionListener(this);
        graphButton.setActionCommand("graph");
        fileWriteButton.addActionListener(this);
        fileWriteButton.setActionCommand("fileWrite");

        //popup with delete option
        tableUsers.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(e.isPopupTrigger()) {
                    int row = tableUsers.rowAtPoint(e.getPoint());
                    tableUsers.setRowSelectionInterval(row, row);
                    doPopUp(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if(e.isPopupTrigger()) {
                    int row = tableUsers.rowAtPoint(e.getPoint());
                    tableUsers.setRowSelectionInterval(row, row);
                    doPopUp(e);
                }
            }

            private void doPopUp(MouseEvent e) {
                int row = tableUsers.rowAtPoint(e.getPoint());
                MyPopUp popup = new MyPopUp(row);
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        });

        scrollPane.getViewport().setBackground(new Color(65, 65, 65));

        //adding full selection on focus
        Component[] inputs = inputsPanel.getComponents();
        for(Component input : inputs) {
            input.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    ((JTextField)input).selectAll();
                }

                @Override
                public void focusLost(FocusEvent e) {
                    ((JTextField)input).select(0,0);
                }
            });
        }
        findRouteField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                findRouteField.selectAll();
            }

            @Override
            public void focusLost(FocusEvent e) {
                findRouteField.select(0,0);
            }
        });

        tabs.addChangeListener((ChangeEvent e) -> {
            refreshButton.setVisible(tabs.getSelectedIndex() == 1);
            super.getRootPane().setDefaultButton(tabs.getSelectedIndex() == 1 ? findUserButton : addButton);
            if(tabs.getSelectedIndex() == 1) {
                graphButton.setVisible(false);
                findUserField.requestFocus();
            } else {
                graphButton.setVisible(true);
                idField.requestFocus();
            }
        });

        editPanel.setVisible(false);

        //focus on number field on start
        super.addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e) {
                idField.requestFocus();
            }
            public void windowClosing(WindowEvent e) {
                controller.back(null);
            }
        });
    }

    private class MyPopUp extends JPopupMenu {
        JMenuItem item;
        public MyPopUp(int row) {
            item = new JMenuItem("Удалить");
            add(item);

            DefaultTableModel tableModel = (DefaultTableModel)tableUsers.getModel();

            int id = (int)tableModel.getValueAt(row, 0);

            item.addActionListener((ActionEvent e) -> {
                Object[] options = new Object[]{"Да", "Нет"};
                int result = JOptionPane.showOptionDialog(this,
                        "Вы действительно хотите удалить пользователя " + id + "?\nЭто действие нельзя будет отменить!",
                        "Удаление", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
                        null, options, null);

                if(result == JOptionPane.YES_OPTION) {
                    try {
                        int deleting = controller.deleteUser(id, currentID);
                        if(deleting == 0 || deleting == 1) {
                            tableModel.removeRow(row);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }
    }

    public static void showView(Account account) {
        AdminView av = new AdminView("Администратор");
        av.controller = new AdminViewController();
        av.controller.setController(av);

        av.setCurrentID(account.getId());

        try {
            av.controller.refreshTable(av.tableUsers);
        } catch (Exception e) {
            e.printStackTrace();
        }

        av.setSize(307, 399);
        av.height = av.getHeight();
        av.setResizable(false);
        av.setLocationRelativeTo(null);
    }

    public void actionPerformed (ActionEvent e) {
        String cmd = e.getActionCommand();
        try {
            switch (cmd) {
                case "add": {
                    controller.addRoute(idField.getText(), sourceField.getText(),
                            destinationField.getText(), departureTimeField.getText(),
                            arriveTimeField.getText(), daysField.getText(),
                            planeTypeField.getText(), gateField.getText());
                    idField.requestFocus();
                    break;
                }
                case "refresh": {
                    controller.refreshTable(tableUsers);
                    findUserField.requestFocus();
                    break;
                }
                case "finduser": {
                    controller.findUser(findUserField.getText(), tableUsers);
                    findUserField.requestFocus();
                    break;
                }
                case "findroute": {
                    controller.findRoute(findRouteField.getText());
                    findRouteField.requestFocus();
                    break;
                }
                case "navigationback": {
                    controller.previousPage();
                    break;
                }
                case "navigationforward": {
                    controller.nextPage();
                    break;
                }
                case "edit": {
                    if (idField.isEditable()) {
                        controller.updateRoute(idField.getText(), sourceField.getText(),
                                destinationField.getText(), departureTimeField.getText(),
                                arriveTimeField.getText(), daysField.getText(),
                                planeTypeField.getText(), gateField.getText());
                    } else {
                        controller.makeEditable(true);
                    }
                    break;
                }
                case "delete": {
                    controller.deleteRoute(idField.getText(), sourceField.getText(),
                            departureTimeField.getText(), daysField.getText());
                    break;
                }
                case "editExit": {
                    editPanel.setVisible(false);
                    controller.makeEditable(true);
                    setSize(getWidth(), 399);
                    break;
                }
                case "fileWrite": {
                    controller.saveToFile();
                    break;
                }
                case "graph": {
                    SwingUtilities.invokeLater(() -> {
                        JFrame frame = new JFrame("Статистика");
                        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        frame.setLocation(50, 50);
                        frame.setResizable(false);

                        CategoryChart chart = LineChart.getChart(controller);
                        JPanel chartPanel = new XChartPanel<>(chart);

                        frame.add(chartPanel, BorderLayout.CENTER);
                        frame.pack();
                        frame.setVisible(true);
                    });
                }
                break;
                case "back": {
                    controller.back(null);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setCurrentID(int id) {
        currentID = id;
    }

    public int getCurrentID() {
        return currentID;
    }

    private void createUIComponents() {
        tableUsers = new JTable() {
            @Override
            public Class getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return String.class;
                    case 1:
                        return Boolean.class;
                    default:
                        return Boolean.class;
                }
            }
        };

    }

    public int getFrameHeight() {
        return height;
    }
}
