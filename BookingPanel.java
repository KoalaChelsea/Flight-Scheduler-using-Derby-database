package flightscheduleryjw5018.gui;

import flightscheduleryjw5018.data.Flight;
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

public class BookingPanel extends JPanel implements FlightUpdateListener, DayUpdateListener {

    private SchedulingService schedulingService;

    private final JTextField customer = new JTextField("", 20);
    private final JComboBox flightList = new JComboBox();
    private final JComboBox dayList = new JComboBox();

    public BookingPanel() {
    }

    public void init() {
        setLayout(new FlowLayout());

        add(new JLabel("Customer"));
        add(customer);
        add(new JLabel("Date"));
        add(dayList);

        add(new JLabel("Flight"));
        add(flightList);
        JButton bookButton = new JButton("Book");
        bookButton.addActionListener((ActionEvent e) -> {
            String flight = (String) flightList.getSelectedItem();
            Date day = DateUtils.parseDate((String) dayList.getSelectedItem());
            String customerName = customer.getText();
            boolean booked = false;
            try {
                booked = schedulingService.bookFlight(flight, day, customerName);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            if (booked) {
                // Booked
                JOptionPane.showMessageDialog(null, customerName + " has been successfully booked for flight " + flight + " on " + DateUtils.formatDate(day));
            } else {
                // Added to waiting list
                JOptionPane.showMessageDialog(null, "No seats available, " + customerName + " have been put on the waiting list for flight " + flight + " on " + DateUtils.formatDate(day));
            }
        });
        add(bookButton);

        updateFlightList();
        updateDayList();
    }

    public void updateFlightList() {
        flightList.removeAllItems();

        Collection<Flight> flights = schedulingService.findFlights();
        for (Flight flight : flights) {
            flightList.addItem(flight.getName());
        }
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
    public void flightUpdated() {
        updateFlightList();
    }

    @Override
    public void dayUpdated() {
        updateDayList();
    }

    public void setSchedulingService(SchedulingService schedulingService) {
        this.schedulingService = schedulingService;
    }
}
