import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ButtonEditor extends DefaultCellEditor {
    protected JButton button;
    private String label;
    private boolean isPushed;
    private JTabbedPane tabs;
    private int tableRow;

    public ButtonEditor(JCheckBox checkBox, JTabbedPane tabs) {
        super(checkBox);
        this.tabs = tabs;
        button = new JButton();
        button.setOpaque(true);
        button.addActionListener((ActionEvent e) -> fireEditingStopped());
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        if (isSelected) {
            button.setForeground(table.getSelectionForeground());
            button.setBackground(table.getSelectionBackground());
        } else {
            button.setForeground(table.getForeground());
            button.setBackground(table.getBackground());
        }
        label = (value == null) ? "" : value.toString();
        button.setText(label);
        tableRow = row;
        if(!button.getText().equals("")) {
            isPushed = true;
        }
        return button;
    }

    public Object getCellEditorValue() {
        DispatcherView.infoCalled = true;
        if (isPushed) {
            DispatcherController.information(tabs, tableRow);
        }
        isPushed = false;
        return label;
    }

    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
    }

    protected void fireEditingStopped() {
        super.fireEditingStopped();
    }
}