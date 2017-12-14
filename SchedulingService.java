/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flightscheduleryjw5018.services;

import flightscheduleryjw5018.data.Booking;
import flightscheduleryjw5018.data.Flight;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 *
 * @author Yingjie(Chelsea) Wang
 */
public class SchedulingService {

    private static final String DATABASE_URL = "jdbc:derby://localhost:1527/FlightSchedulerDBYJW5018";
    private static final String USERNAME = "java";
    private static final String PASSWORD = "java";

    private Connection connection;

    public SchedulingService() {
        try {
            connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            System.exit(1);
        }
    }

    public Collection<Flight> findFlights() {
        ArrayList<Flight> flights = new ArrayList();

        try {
            // JDBC code goes here
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT NAME, SEAT FROM FLIGHT ORDER BY NAME");

            while (rs.next()) {
                Flight flight = new Flight(rs.getString("NAME"), rs.getInt("SEAT"));
                flights.add(flight);
            }

            rs.close();
            stmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return flights;
    }

    public Collection<Date> getDays() {
        ArrayList<Date> days = new ArrayList();

        try {
            // JDBC code goes here
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT DATE FROM DAY ORDER BY DATE");

            while (rs.next()) {
                days.add(rs.getDate("DATE"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return days;
    }

    public boolean bookFlight(String flight, Date day, String customer) {
        try {
            // Check if there's a seat
            PreparedStatement stmt = connection.prepareStatement("SELECT (SEAT - b.BOOKED) AS AVAIL FROM FLIGHT f\n"
                    + "CROSS JOIN (SELECT COUNT(CUSTOMER) BOOKED FROM Booking WHERE FLIGHT = ? AND DATE = ? AND BOOKED = 'Y') b\n"
                    + "WHERE NAME = ?");
            stmt.setString(1, flight);
            stmt.setDate(2, new java.sql.Date(day.getTime()));
            stmt.setString(3, flight);

            ResultSet rs = stmt.executeQuery();
            int availableSeats = 0;
            if (rs.next()) {
                availableSeats = rs.getInt("AVAIL");
            }
            rs.close();
            stmt.close();

            PreparedStatement insertStmt = connection.prepareStatement("INSERT INTO BOOKING (FLIGHT, DATE, CUSTOMER, BOOKED, ENTERED) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)");
            insertStmt.setString(1, flight);
            insertStmt.setDate(2, new java.sql.Date(day.getTime()));
            insertStmt.setString(3, customer);
            if (availableSeats > 0) {
                insertStmt.setString(4, "Y");
            } else {
                insertStmt.setString(4, "N");
            }

            insertStmt.executeUpdate();
            insertStmt.close();

            return availableSeats > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Collection<Booking> getFlightStatus(String flight, Date day) {
        ArrayList<Booking> bookings = new ArrayList();

        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT FLIGHT, DATE, CUSTOMER, ENTERED FROM BOOKING WHERE FLIGHT = ? AND DATE = ? AND BOOKED = 'Y' ORDER BY ENTERED ASC");
            stmt.setString(1, flight);
            stmt.setDate(2, new java.sql.Date(day.getTime()));

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String flightName = rs.getString("FLIGHT");
                Date flightDate = rs.getDate("DATE");
                String customer = rs.getString("CUSTOMER");
                Date entered = rs.getTimestamp("ENTERED");

                Booking booking = new Booking(flightName, flightDate, customer, true, (Timestamp) entered);
                bookings.add(booking);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bookings;
    }

    public Collection<Booking> getWaitingList(Date day) {
        ArrayList<Booking> bookings = new ArrayList();

        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT FLIGHT, DATE, CUSTOMER, ENTERED FROM BOOKING WHERE DATE = ? AND BOOKED = 'N' ORDER BY ENTERED ASC");
            stmt.setDate(1, new java.sql.Date(day.getTime()));

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String flightName = rs.getString("FLIGHT");
                Date flightDate = rs.getDate("DATE");
                String customer = rs.getString("CUSTOMER");
                Date entered = rs.getTimestamp("ENTERED");

                Booking booking = new Booking(flightName, flightDate, customer, false, (Timestamp) entered);
                bookings.add(booking);
            }

            rs.close();
            stmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bookings;
    }

    public boolean addFlight(String flight, int numberOfSeats) {

        try {
            PreparedStatement add = connection.prepareStatement("INSERT INTO FLIGHT (NAME, SEAT) VALUES (?, ?)");

            add.setString(1, flight);
            add.setInt(2, numberOfSeats);
            add.executeUpdate();

            add.close();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String findAlternativeFlight(Date day, String flight) {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT f.NAME AS FLIGHT, (f.SEAT - CASE WHEN b.BOOKED IS NULL THEN 0 ELSE b.BOOKED END) AS AVAIL\n"
                    + "FROM FLIGHT f\n"
                    + "LEFT OUTER JOIN\n"
                    + "(\n"
                    + "    SELECT FLIGHT, COUNT(*) BOOKED FROM BOOKING WHERE DATE = ? GROUP BY FLIGHT, DATE\n"
                    + ") b ON f.NAME = b.FLIGHT WHERE f.NAME != ?");
            stmt.setDate(1, new java.sql.Date(day.getTime()));
            stmt.setString(2, flight);
            ResultSet rs = stmt.executeQuery();

            String newFlight = null;
            while (rs.next()) {
                if (rs.getInt("AVAIL") > 0) {
                    newFlight = rs.getString("FLIGHT");
                    break;
                }
            }

            rs.close();
            stmt.close();

            return newFlight;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Collection<Booking> dropFlight(String flight) {
        ArrayList<Booking> bookings = new ArrayList();

        try {
            // Try and rebook all customers booked for this flight
            PreparedStatement stmt1 = connection.prepareStatement("SELECT CUSTOMER, DATE FROM BOOKING WHERE FLIGHT = ? AND BOOKED = 'Y' ORDER BY DATE ASC");
            stmt1.setString(1, flight);
            ResultSet rs = stmt1.executeQuery();
            while (rs.next()) {
                String customer = rs.getString("CUSTOMER");
                Date day = rs.getDate("DATE");
                String newFlight = findAlternativeFlight(day, flight);

                if (newFlight != null) {
                    bookFlight(newFlight, day, customer);
                }

                Booking booking = new Booking(newFlight, day, customer, (newFlight != null), new Timestamp(System.currentTimeMillis()));
                bookings.add(booking);
            }

            // Delete all bookings
            PreparedStatement stmt2 = connection.prepareStatement("DELETE FROM BOOKING WHERE FLIGHT = ?");
            stmt2.setString(1, flight);
            stmt2.executeUpdate();
            stmt2.close();

            // Delete the flight
            PreparedStatement stmt3 = connection.prepareStatement("DELETE FROM FLIGHT WHERE NAME = ?");
            stmt3.setString(1, flight);
            stmt3.executeUpdate();
            stmt3.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return bookings;
    }

    public boolean addDate(Date date) {
        try {
            PreparedStatement add = connection.prepareStatement("INSERT INTO DAY (DATE) VALUES (?)");
            add.setDate(1, new java.sql.Date(date.getTime()));
            add.executeUpdate();
            add.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Collection<Booking> cancelFlight(String customer, Date day) {
        ArrayList<Booking> bookings = new ArrayList();
        try {
            // Find all flights the customer booked on this day
            PreparedStatement stmt = connection.prepareStatement("SELECT FLIGHT FROM BOOKING WHERE CUSTOMER = ? AND DATE = ? AND BOOKED = 'Y'");
            stmt.setString(1, customer);
            stmt.setDate(2, new java.sql.Date(day.getTime()));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String flight = rs.getString("FLIGHT");
                // Remove the booking
                PreparedStatement stmt2 = connection.prepareStatement("DELETE FROM BOOKING WHERE FLIGHT = ? AND DATE = ? AND CUSTOMER = ? AND BOOKED = 'Y'");
                stmt2.setString(1, flight);
                stmt2.setDate(2, new java.sql.Date(day.getTime()));
                stmt2.setString(3, customer);
                stmt2.executeUpdate();
                stmt2.close();

                // Book a customer from the waiting list
                Booking booking = bookWaitlistedCustomer(flight, day);
                if (booking != null) {
                    bookings.add(booking);
                }
            }
            rs.close();
            stmt.close();

            // Delete all waiting list records as well
            PreparedStatement stmt3 = connection.prepareStatement("DELETE FROM BOOKING WHERE CUSTOMER = ? AND DATE = ? AND BOOKED = 'N'");
            stmt3.setString(1, customer);
            stmt3.setDate(2, new java.sql.Date(day.getTime()));
            stmt3.executeUpdate();
            stmt3.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return bookings;
    }

    public Booking bookWaitlistedCustomer(String flight, Date day) {
        try {
            // Get the first customer on the waiting list
            PreparedStatement stmt = connection.prepareStatement("SELECT CUSTOMER\n"
                    + "FROM BOOKING\n"
                    + "WHERE FLIGHT = ? AND DATE = ? AND BOOKED = 'N'\n"
                    + "ORDER BY ENTERED ASC\n"
                    + "FETCH FIRST 1 ROWS ONLY");
            stmt.setString(1, flight);
            stmt.setDate(2, new java.sql.Date(day.getTime()));
            ResultSet rs = stmt.executeQuery();
            String customer = null;
            if (rs.next()) {
                customer = rs.getString("CUSTOMER");
            }
            rs.close();
            stmt.close();

            if (customer != null) {
                // Book the waitlisted customer
                PreparedStatement stmt2 = connection.prepareStatement("UPDATE BOOKING SET BOOKED = 'Y' WHERE FLIGHT = ? AND DATE = ? AND CUSTOMER = ?");
                stmt2.setString(1, flight);
                stmt2.setDate(2, new java.sql.Date(day.getTime()));
                stmt2.setString(3, customer);
                stmt2.executeUpdate();
                stmt2.close();

                Booking booking = new Booking(flight, day, customer, true, new Timestamp(System.currentTimeMillis()));
                return booking;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public Collection<Booking> getCustomerStatus(String customerName) {
        ArrayList<Booking> bookings = new ArrayList();

        try {

            PreparedStatement stmt = connection.prepareStatement("SELECT FLIGHT, DATE, CUSTOMER, BOOKED, ENTERED FROM BOOKING WHERE CUSTOMER = ? ORDER BY ENTERED ASC");
            stmt.setString(1, customerName);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String flightName = rs.getString("FLIGHT");
                Date flightDate = rs.getDate("DATE");
                String customer = rs.getString("CUSTOMER");
                String booked = rs.getString("BOOKED");
                Date entered = rs.getTimestamp("ENTERED");

                Booking booking = new Booking(flightName, flightDate, customer, "Y".equals(booked) ? true : false, (Timestamp) entered);
                bookings.add(booking);
            }

            rs.close();
            stmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bookings;
    }
}
