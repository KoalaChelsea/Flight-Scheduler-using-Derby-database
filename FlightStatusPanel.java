package flightscheduleryjw5018.gui;

import flightscheduleryjw5018.data.Booking;
import flightscheduleryjw5018.data.Flight;
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

public class FlightStatusPanel extends JPanel implements FlightUpdateListener, DayUpdateListener {

    private SchedulingService schedulingService;

    private final JComboBox flightList = new JComboBox();
    private final JComboBox dayList = new JComboBox();
    private final JTable flightStatus = new JTable();

    public FlightStatusPanel() {

    }

    public void init() {
        setLayout(new BorderLayout());
        
        updateFlightList();
        updateDayList();

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Date"));
        topPanel.add(dayList);
        topPanel.add(new JLabel("Flight"));
        topPanel.add(flightList);
        JButton getStatusButton = new JButton("Get Status");
        getStatusButton.addActionListener((ActionEvent e) -> {
            String flight = (String) flightList.getSelectedItem();
            Date day = DateUtils.parseDate((String) dayList.getSelectedItem());

            flightStatus.removeAll();
            //flightStatus.setco
            Collection<Booking> bookings = schedulingService.getFlightStatus(flight, day);
            if (bookings != null) {
                DefaultTableModel model = (DefaultTableModel) flightStatus.getModel();
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

        flightStatus.setModel(new DefaultTableModel(new Object[]{"Flight", "Date", "Customer", "Booked On"}, 0));
        add(new JScrollPane(flightStatus), BorderLayout.CENTER);

    }

    public void setSchedulingService(SchedulingService schedulingService) {
        this.schedulingService = schedulingService;
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
}
