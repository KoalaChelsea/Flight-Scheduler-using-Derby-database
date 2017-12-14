package flightscheduleryjw5018.gui;

import flightscheduleryjw5018.data.Booking;
import flightscheduleryjw5018.services.SchedulingService;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.Collection;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class CustomerStatusPanel extends JPanel {

    private SchedulingService schedulingService;

    private final JTextField customer = new JTextField("", 20);
    private final JTable flightStatus = new JTable();

    public CustomerStatusPanel() {
    }

    public void init() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Customer"));
        topPanel.add(customer);
        JButton getStatusButton = new JButton("Get Status");
        getStatusButton.addActionListener((ActionEvent e) -> {
            String customerName = customer.getText();

            Collection<Booking> bookings = schedulingService.getCustomerStatus(customerName);
            if (bookings != null) {
                DefaultTableModel model = (DefaultTableModel) flightStatus.getModel();
                // Clear the table
                model.setRowCount(0);
                // Add new rows
                for (Booking booking : bookings) {
                    model.addRow(new Object[]{booking.getFlight(), booking.getDate(), booking.isBooked() ? "booked" : "waitlisted"});
                }
            }

        });
        topPanel.add(getStatusButton);
        add(topPanel, BorderLayout.NORTH);

        flightStatus.setModel(new DefaultTableModel(new Object[]{"Flight", "Date", "Status"}, 0));
        add(new JScrollPane(flightStatus), BorderLayout.CENTER);

    }

    public void setSchedulingService(SchedulingService schedulingService) {
        this.schedulingService = schedulingService;
    }
}
