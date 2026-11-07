import java.util.concurrent.atomic.AtomicReference;

public class Demo2 {
    public static void main(String[] args) {
        SeatBooking booking = new SeatBooking();
        // Only one thread should be able to change the seat from Empty to its name.
        Thread t1 = new Thread(() -> {
            boolean status = booking.bookSeat("Ram");
            System.out.println("T1 status: " + status);
        });
        Thread t2 = new Thread(() -> {
            boolean status = booking.bookSeat("Sam");
            System.out.println("T2 status: " + status);
        });

        t1.start();
        t2.start();

        try {
            Thread.sleep(500);
        } catch (Exception e) {
            // TODO: handle exception
        }
        System.out.println("Seat Book for: " + booking.seat);
    }
}

class SeatBooking {
    // String seat = new String("Empty");

    // AtomicReference stores a reference and supports atomic compare-and-set updates.
    AtomicReference<String> seat = new AtomicReference<>("Empty");

    boolean bookSeat(String name) {
        // if (seat.equals("Empty")) {
        // seat = new String(name);
        // return true;
        // }
        // return false;

        // Compare then Set

        // The separate read is just an optimization; compareAndSet decides atomically
        // whether this caller wins if another thread races to book the seat.
        String currentValue = seat.get();

        if (!currentValue.equals("Empty")) {
            return false;
        }

        return seat.compareAndSet(currentValue, name);
    }
}