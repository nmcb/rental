package rental.api;

import java.util.List;

public class Checkin {
    private Long userId;
    private List<Long> filmIds;

    Checkin() {}

    public Long getUserId() {
        return userId;
    }

    public List<Long> getFilmIds() {
        return filmIds;
    }
}
