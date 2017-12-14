package flightscheduleryjw5018.gui;

import flightscheduleryjw5018.data.Booking;
import flightscheduleryjw5018.services.DateUtils;
import flightscheduleryjw5018.services.SchedulingService;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class WaitingListPanel extends JPanel implements DayUpdateListener {

    private SchedulingService schedulingService;

    private final JComboBox dayList = new JComboBox();
    private final JTable waitingList = new JTable();

    public WaitingListPanel() {

    }

    public void init() {
        setLayout(new BorderLayout());

        updateDayList();
        
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Date"));
        topPanel.add(dayList);
        JButton getStatusButton = new JButton("Get Status");
        getStatusButton.addActionListener((ActionEvent e) -> {
            Date day = DateUtils.parseDate((String) dayList.getSelectedItem());

            waitingList.removeAll();

            Collection<Booking> bookings = schedulingService.getWaitingList(day);
            if (bookings != null) {
                DefaultTableModel model = (DefaultTableModel) waitingList.getModel();
                // Clear the table
                model.setRowCount(0);
                // Add new rows
                for (Booking booking : bookings) {
                    model.addRow(new Object[]{booking.getFlight(), booking.getDate(), booking.getCustomer(), DateUtils.formatTimestamp(booking.getEntered())});
                }
            }

        });
        topPanel.add(getStatusButton);
        add(topPanel, BorderLayout.NORTH);

        waitingList.setModel(new DefaultTableModel(new Object[]{"Flight", "Date", "Customer", "Waitlisted On"}, 0));
        add(new JScrollPane(waitingList), BorderLayout.CENTER);

    }

    public void setSchedulingService(SchedulingService schedulingService) {
        this.schedulingService = schedulingService;
    }
    
    public void updateDayList() {
        dayList.removeAllItems();
        
        Collection<Date> days = schedulingService.getDays();
        for (Date day : days) {
            String d = DateUtils.formatDate(day);
            dayList.addItem(d);
        }
    }
    
    @Override
    public void dayUpdated() {
        updateDayList();
    }
}
