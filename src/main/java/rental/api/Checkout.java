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
        private long filmId;
        private int nrOfDays;

        Item() {}

        public long getFilmId() {
            return filmId;
        }

        public int getNrOfDays() {
            return nrOfDays;
        }
    }
}
