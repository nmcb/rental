package rental.mdl;

import static java.lang.Math.*;

@SuppressWarnings(value = "unchecked")
public enum RentalType {

    NEW  ( Price.PREMIUM,   1,   2,    "New Release" ),
    REG  (   Price.BASIC,   3,   1, "Regular Rental" ),
    OLD  (   Price.BASIC,   5,   1,       "Old Film" );

    public final long   PRICE;
    public final int    OFFSET_DAYS;
    public final int    BONUS_POINTS;
    public final String LABEL;

    RentalType(long price, int offsetDays, int bonusPoints, String label) {
        PRICE         = price;
        OFFSET_DAYS   = offsetDays;
        BONUS_POINTS  = bonusPoints;
        LABEL         = label;
    }

    public long chargeFor(int days) {
        return PRICE + PRICE * max(days - OFFSET_DAYS, 0);
    }
}