package flightscheduleryjw5018.gui;

import flightscheduleryjw5018.data.Booking;
import flightscheduleryjw5018.data.Flight;
import flightscheduleryjw5018.services.DateUtils;
import flightscheduleryjw5018.services.SchedulingService;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.NumberFormatter;

public class FlightManagementPanel extends JPanel {

    private SchedulingService schedulingService;
    private final List<FlightUpdateListener> listeners = new ArrayList();

    private final JTable flightTable = new JTable();

    public FlightManagementPanel() {
    }

    public void init() {
        setLayout(new BorderLayout());

        JButton addButton = new JButton("Add");
        addButton.addActionListener((ActionEvent e) -> {
            addFlight();
        });

        JButton editButton = new JButton("Edit");
        editButton.setEnabled(false);
        editButton.addActionListener((ActionEvent e) -> {
            int row = flightTable.getSelectedRow();
            String name = (String) flightTable.getValueAt(row, 0);

            if (name != null) {
                editFlight(name);
            }
        });

        JButton dropButton = new JButton("Drop");
        dropButton.addActionListener((ActionEvent e) -> {
            int row = flightTable.getSelectedRow();
            String name = (String) flightTable.getValueAt(row, 0);

            if (name != null) {
                dropFlight(name);
            }
        });

        JPanel topPanel = new JPanel();
        topPanel.add(addButton);
        topPanel.add(editButton);
        topPanel.add(dropButton);

        flightTable.setModel(new DefaultTableModel(new Object[]{"Name", "Number Of Seats"}, 0));
        refreshFlights();

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(flightTable), BorderLayout.CENTER);
    }

    public void refreshFlights() {
        DefaultTableModel model = (DefaultTableModel) flightTable.getModel();
        model.setRowCount(0);

        Collection<Flight> flights = schedulingService.findFlights();
        flights.forEach((flight) -> {
            model.addRow(new Object[]{flight.getName(), flight.getNumberOfSeats()});
        });
    }

    public void addFlight() {
        JTextField flightName = new JTextField();

        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(0);
        formatter.setMaximum(Integer.MAX_VALUE);
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true);
        JFormattedTextField flightSeats = new JFormattedTextField(formatter);

        Object[] message = {
            "Flight:", flightName,
            "Number of seats:", flightSeats
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Add Flight", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            schedulingService.addFlight(flightName.getText(), Integer.valueOf(flightSeats.getText()));
            refreshFlights();
            notifyListeners();
        }
    }

    public void editFlight(String flightName) {
        // Not implemetned yet
    }

    public void dropFlight(String flightName) {
        Collection<Booking> bookings = schedulingService.dropFlight(flightName);

        refreshFlights();
        notifyListeners();

        String msg = flightName + " has been successfully dropped.\n";
        for (Booking booking : bookings) {
            if (booking.getFlight() != null)
                msg += booking.getCustomer() + " has been booked for an alternative flight " + booking.getFlight() + " on " + DateUtils.formatDate(booking.getDate()) + ".\n";
            else
                msg += booking.getCustomer() + " couldn't get booked for any alternative flight on " + DateUtils.formatDate(booking.getDate()) + " :(\n";
        }
        JOptionPane.showMessageDialog(null, msg);
    }

    public void setSchedulingService(SchedulingService bookingService) {
        this.schedulingService = bookingService;
    }

    public void addFlightUpdateListener(FlightUpdateListener listener) {
        listeners.add(listener);
    }

    protected void notifyListeners() {
        listeners.forEach(listener -> {
            listener.flightUpdated();
        });
    }
}
