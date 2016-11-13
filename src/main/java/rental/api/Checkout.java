package rental.api;

import java.util.List;

public class Checkout {
    private long userId;
    private List<Item> items;

    Checkout() {}

    public long getUserId() {
        return userId;
    }

    public List<Item> getItems() {
        return items;
    }

    public static class Item {
        private long boxId;
        private int nrOfDays;

        Item() {}

        public long getBoxId() {
            return boxId;
        }

        public int getNrOfDays() {
            return nrOfDays;
        }
    }
}
