package rental.mdl;

import java.util.List;

public class Price {
    private long total;

    public Price(long total) {
        this.total = total;
    }

    public long getTotal() {
        return total;
    }


    // Calculation

    private static long price(RentalType type, int nrOfDays) {
        return type.chargeFor(nrOfDays);
    }

    public static Price checkout(List<Rental> rentals) {
        long total = 0;
        for (Rental rental : rentals) {
            total += price(rental.getFilm().getType(), rental.getNrOfDays());
        }
        return new Price(total);
    }

    public static Price checkin(List<Rental> rentals) {
        long overdue = 0;
        for (Rental rental : rentals) {
            long total = price(rental.getFilm().getType(), rental.getTotalNrOfDays());
            long paid  = price(rental.getFilm().getType(), rental.getNrOfDays());
            overdue += Math.max(total - paid, 0);
        }
        return new Price(overdue);
    }

    // Constants

    public static final int PREMIUM = 40;  // 40 SEK
    public static final int BASIC   = 30;  // 30 SEK
}
