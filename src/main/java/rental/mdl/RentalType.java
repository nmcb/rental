package rental.mdl;

public enum RentalType {
    NEW(Price.PREMIUM, 1, 2, "New Release"),
    REG(Price.BASIC, 3, 1, "Regular Rental"),
    OLD(Price.BASIC, 5, 1, "Old Film");

    private long price;
    private int offsetDays;
    private int bonusPoints;
    private String label;

    RentalType(long price, int offsetDays, int bonusPoints, String label) {
        this.price = price;
        this.offsetDays = offsetDays;
        this.bonusPoints = bonusPoints;
        this.label = label;
    }

    public long getPrice() {
        return price;
    }

    public int getOffsetDays() {
        return offsetDays;
    }

    public int getBonusPoints() {
        return bonusPoints;
    }

    public String getLabel() {
        return label;
    }

    public long chargeFor(int days) {
        return getPrice() + getPrice() * Math.max(days - getOffsetDays(), 0);
    }
}
