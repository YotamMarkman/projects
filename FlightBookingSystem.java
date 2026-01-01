public class FlightBookingSystem {

    private String[] seats;
    private int seatsLeft;

    public FlightBookingSystem(int numSeats) {
        this.seats = new String[numSeats]; // All start as null (Empty)
        this.seatsLeft = numSeats;
    }

    public synchronized boolean bookSeat(String passenger, int seatNumber) {
        if (seatNumber < 0 || seatNumber >= seats.length) {
            return false;
        }

        // 2. Logic Check: Is it already taken?
        // We use 'seats[i] != null' to know if it's occupied
        if (seats[seatNumber] != null) {
            return false; // Already taken
        }

        // 3. Action: Book it
        seats[seatNumber] = passenger;
        seatsLeft--;
        System.out.println("CONFIRMED: " + passenger + " booked seat " + seatNumber);
        return true;
    }

    public synchronized void cancelBooking(int seatNumber) {
        // 1. Safety Check
        if (seatNumber < 0 || seatNumber >= seats.length) {
            return;
        }

        // 2. Logic: Only cancel if it is CURRENTLY taken
        if (seats[seatNumber] != null) {
            System.out.println("CANCELLED: " + seats[seatNumber] + " left seat " + seatNumber);
            seats[seatNumber] = null; // Make it free again
            seatsLeft++;
        }
    }

    public void printStatus() {
        System.out.println("\n--- Current Flight Status ---");
        for (int i = 0; i < seats.length; i++) {
            if (seats[i] != null) {
                System.out.println("Seat " + i + ": [" + seats[i] + "]");
            } else {
                System.out.println("Seat " + i + ": Empty");
            }
        }
        System.out.println("Total Seats Left: " + seatsLeft + "\n");
    }
}