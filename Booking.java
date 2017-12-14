/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flightscheduleryjw5018.data;

import java.sql.Timestamp;
import java.util.Date;

/**
 *
 * @author koala
 */
public class Booking {

    private String flight;
    private Date date;
    private String customer;
    private boolean booked;
    private Timestamp entered;

    public Booking() {
    }

    public Booking(String flight, Date date, String customer, boolean booked, Timestamp entered) {
        this.flight = flight;
        this.date = date;
        this.customer = customer;
        this.booked = booked;
        this.entered = entered;
    }

    public String getFlight() {
        return flight;
    }

    public void setFlight(String flight) {
        this.flight = flight;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public Date getEntered() {
        return entered;
    }

    public void setEntered(Timestamp entered) {
        this.entered = entered;
    }

    public boolean isBooked() {
        return booked;
    }

    public void setBooked(boolean booked) {
        this.booked = booked;
    }

}
