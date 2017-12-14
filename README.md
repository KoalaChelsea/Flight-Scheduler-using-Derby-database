# Flight-Scheduler-using-Derby-database
This application have a very nice GUI interface and is a Derby database driven application.
I developed a Flight Scheduling application for the Fly-By-Night Airline. The Airline has one or more flights per day but they are not by time. Every Flight is available for each day the airline flies. The customer can get booked on a specific flight for a specific day. Each Flight has a name and a number of seats on the flight. Each date is just a specific day. The Customer is identified by a single name.

Functions implemented in this project:
1. Book Customer Day Flight
The customer will be assigned the flight for the requested day, if there are seats available. If seats are not available, the customer will be put on the wait list for that flight. The waiting list must be maintained in the order the customers tried to book their flights.

2. Status Flight Day
The Status command for flight and day will display the customers that have been booked for that flight on that day.

3. Status Waiting List by Day
The Status command for the Waitlist will display all the customers waiting for flights on the specified day.

4. Add Flight Seats
Add a new flight to the system. The Flight name is a string and Seats is the number of seats in the flight.

5. Cancel Customer Day
The booking for that Customer and Day must be removed from the flight’s bookings or the waiting list. If the booked entry is removed from a flights bookings, the waiting list must be checked to determine if another customer can be booked with that flight for that day.

6. Add Day
Add a new Day for flights to the system.

7. Status Customer
The Status command for a customer will display the flight and day for each flight the customer has booked and/or is waitlisted for.

8. Drop Flight
The Drop command must remove a flight from the application. Any customers that have been booked for this flight on any day must be rebooked with another flight for that day if possible in priority sequence and the rebooking reported to the user. If the customer cannot be rebooked, the user is informed that the customer could not be rebooked. Any customers on the waitlist for that flight must also be deleted.
