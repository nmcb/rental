package rental.api;

import java.util.List;

public class Checkin {
    private Long userId;
    private List<Long> boxIds;

    Checkin() {}

    public Long getUserId() {
        return userId;
    }

    public List<Long> getBoxIds() {
        return boxIds;
    }
}
