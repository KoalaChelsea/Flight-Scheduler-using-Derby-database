package flightscheduleryjw5018.gui;

import flightscheduleryjw5018.services.SchedulingService;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class DayManagementPanel extends JPanel {

    private SchedulingService schedulingService;
    private final List<DayUpdateListener> listeners = new ArrayList();

    private final JTable dayTable = new JTable();

    public DayManagementPanel() {
    }

    public void init() {
        setLayout(new BorderLayout());

        JButton addButton = new JButton("Add");
        addButton.addActionListener((ActionEvent e) -> {
            addDay();
        });

        JButton dropButton = new JButton("Drop");
        dropButton.addActionListener((ActionEvent e) -> {
            int row = dayTable.getSelectedRow();
            Date date = (Date) dayTable.getValueAt(row, 0);

            if (date != null) {
                dropDay(date);
            }

        });

        JPanel topPanel = new JPanel();
        topPanel.add(addButton);
        topPanel.add(dropButton);

        dayTable.setModel(new DefaultTableModel(new Object[]{"Date(yyyy-MM-dd)"}, 0));
        refreshDays();

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(dayTable), BorderLayout.CENTER);
    }

    public void refreshDays() {
        DefaultTableModel model = (DefaultTableModel) dayTable.getModel();
        model.setRowCount(0);

        Collection<Date> days = schedulingService.getDays();
        days.forEach((day) -> {
            model.addRow(new Object[]{day});
        });
    }

    public void addDay() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        JFormattedTextField day = new JFormattedTextField(df);
        
        Object[] message = {
            "Date (YYYY-MM-DD):", day
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Add Day", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                Date date = df.parse(day.getText());
                schedulingService.addDate(date);
            } catch (ParseException ex) {
                Logger.getLogger(DayManagementPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
            refreshDays();
            notifyListeners();
        }
    }

    public void dropDay(Date day) {
        // Not implemetned yet
        //notifyListeners();
    }

    public void setSchedulingService(SchedulingService schedulingService) {
        this.schedulingService = schedulingService;
    }

    public void addDayUpdateListener(DayUpdateListener listener) {
        listeners.add(listener);
    }

    protected void notifyListeners() {
        listeners.forEach(listener -> {
            listener.dayUpdated();
        });
    }

}
