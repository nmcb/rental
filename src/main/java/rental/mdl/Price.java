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

    public static Price checkoutPrice(List<Box> boxes) {
        long total = 0;
        for (Box box : boxes) {
            total += price(box.getFilm().getType(), box.getNrOfDays());
        }
        return new Price(total);
    }

    public static Price checkinPrice(List<Box> boxes) {
        long overdue = 0;
        for (Box box : boxes) {
            long total = price(box.getFilm().getType(), box.getTotalNrOfDays());
            long paid  = price(box.getFilm().getType(), box.getNrOfDays());
            overdue += Math.max(total - paid, 0);
        }
        return new Price(overdue);
    }

    // Constants

    public static final int PREMIUM = 40;  // 40 SEK
    public static final int BASIC   = 30;  // 30 SEK
}
