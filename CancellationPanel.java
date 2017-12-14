package flightscheduleryjw5018.gui;

import flightscheduleryjw5018.data.Booking;
import flightscheduleryjw5018.services.DateUtils;
import flightscheduleryjw5018.services.SchedulingService;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class CancellationPanel extends JPanel implements DayUpdateListener {

    private SchedulingService schedulingService;

    private final JTextField customer = new JTextField("", 20);
    private final JComboBox dayList = new JComboBox();

    public CancellationPanel() {
    }

    public void init() {
        setLayout(new FlowLayout());

        add(new JLabel("Customer"));
        add(customer);
        add(new JLabel("Date"));
        add(dayList);

        JButton bookButton = new JButton("Cancel");
        bookButton.addActionListener((ActionEvent e) -> {
            Date day = DateUtils.parseDate((String) dayList.getSelectedItem());
            String customerName = customer.getText();
            boolean booked = false;
            Collection<Booking> newBookings = schedulingService.cancelFlight(customerName, day);

            String msg = customerName + " has been successfully cancelled for all flights on " + DateUtils.formatDate(day) + ".\n";

            for (Booking booking : newBookings) {
                msg += "Previously waitlisted customer " + booking.getCustomer() + " has been booked for flight " + booking.getFlight() + " on " + DateUtils.formatDate(booking.getDate()) + ".\n";
            }

            JOptionPane.showMessageDialog(null, msg);

        });
        add(bookButton);

        updateDayList();
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

    public void setSchedulingService(SchedulingService schedulingService) {
        this.schedulingService = schedulingService;
    }
}
