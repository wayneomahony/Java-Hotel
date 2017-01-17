package Hotel;

/*
    Hotel Room Booking System.
*/

public class NoSuchBookingException extends Exception {
    public NoSuchBookingException (String bookingRef) {
	super(Thread.currentThread().getName() + " :There is no booking with reference " + bookingRef + "\n***");
    }
}
