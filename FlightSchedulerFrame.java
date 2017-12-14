/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flightscheduleryjw5018.gui;

import flightscheduleryjw5018.services.SchedulingService;
import javax.swing.*;

/**
 *
 * @author Yingjie(Chelsea) Wang
 */
public class FlightSchedulerFrame extends JFrame {
    private final SchedulingService schedulingService = new SchedulingService();
    
    private final BookingPanel bookingPanel = new BookingPanel();
    private final CancellationPanel cancellationPanel = new CancellationPanel();
    private final CustomerStatusPanel customerStatusPanel = new CustomerStatusPanel();
    private final FlightStatusPanel flightStatusPanel = new FlightStatusPanel();
    private final WaitingListPanel waitingListPanel = new WaitingListPanel();
    private final FlightManagementPanel flightManagementPanel = new FlightManagementPanel();
    private final DayManagementPanel dayManagementPanel = new DayManagementPanel();
    
    //Constructor initializes components and organize them using certain layouts
    public FlightSchedulerFrame() {
        setTitle("Flight Scheduler - Fly-By-Night Airline");
        
        //set up TabbedPane
        JTabbedPane rootPane = new JTabbedPane();
        setContentPane(rootPane);

        //////////////////////////////////////////////
        // Construct the Booking tab
        bookingPanel.setSchedulingService(schedulingService);
        bookingPanel.init();
        flightManagementPanel.addFlightUpdateListener(bookingPanel);
        dayManagementPanel.addDayUpdateListener(bookingPanel);
        rootPane.addTab("Book Flight", bookingPanel);

        //////////////////////////////////////////////
        // Construct the Cancellation tab
        cancellationPanel.setSchedulingService(schedulingService);
        cancellationPanel.init();
        dayManagementPanel.addDayUpdateListener(cancellationPanel);
        rootPane.addTab("Cancel Flight", cancellationPanel);
        
        //////////////////////////////////////////////
        // Construct the Customer Status tab
        customerStatusPanel.setSchedulingService(schedulingService);
        customerStatusPanel.init();
        rootPane.addTab("Customer Status", customerStatusPanel);

        //////////////////////////////////////////////
        // Construct the Flight Status tab
        flightStatusPanel.setSchedulingService(schedulingService);
        flightStatusPanel.init();
        rootPane.addTab("Flight Status", flightStatusPanel);

        //////////////////////////////////////////////
        // Construct the Waiting List tab
        waitingListPanel.setSchedulingService(schedulingService);
        waitingListPanel.init();
        rootPane.addTab("Waiting List", waitingListPanel); 
    
        /////////////////////////////////////////////
        // Construct the Flight Management tab
        flightManagementPanel.setSchedulingService(schedulingService);
        flightManagementPanel.init();
        flightManagementPanel.addFlightUpdateListener(flightStatusPanel);
        rootPane.addTab("Manage Flights", flightManagementPanel);
        
        /////////////////////////////////////////////
        // Construct the Day Management tab
        dayManagementPanel.setSchedulingService(schedulingService);
        dayManagementPanel.init();
        dayManagementPanel.addDayUpdateListener(flightStatusPanel);
        dayManagementPanel.addDayUpdateListener(waitingListPanel);
        rootPane.addTab("Manage Days", dayManagementPanel);
        
    }
}
