package flightscheduleryjw5018.gui;

/**
 * Listens on flight updates.
 */
public interface FlightUpdateListener {
    /**
     * Called when one or more flights are added/updated/removed.
     */
    public void flightUpdated();
}
