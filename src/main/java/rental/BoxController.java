package rental;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import rental.api.Checkin;
import rental.api.Checkout;
import rental.dal.Boxes;
import rental.dal.Users;
import rental.mdl.Box;
import rental.mdl.Price;
import rental.mdl.User;

import java.util.ArrayList;
import java.util.List;

import static rental.mdl.Price.checkinPrice;
import static rental.mdl.Price.checkoutPrice;

@Transactional
@RestController
@RequestMapping("/boxes")
public class BoxController {
    private ControllerDelegate<Box> delegate;
    private Boxes boxes;
    private Users users;

    @Autowired
    BoxController(Users users, Boxes boxes) {
        this.delegate = new ControllerDelegate<>(boxes);
        this.boxes = boxes;
        this.users = users;
    }

    // CRUD Mapping

    @RequestMapping(value = ControllerDelegate.BASE, method = RequestMethod.POST)
    public ResponseEntity<Box> add(@RequestBody Box box) {
        return delegate.add(box);
    }

    @RequestMapping(value = ControllerDelegate.WITH_ID)
    public Box byId(@PathVariable(name = "id") Long id) {
        return delegate.byId(id);
    }

    @RequestMapping(value = ControllerDelegate.BASE)
    public List<Box> findAll() {
        return delegate.findAll();
    }

    // Rental Process Mapping

    @RequestMapping(value = "/checkout", method = RequestMethod.POST)
    public Price checkout(@RequestBody Checkout request) {
        User user = users.findOne(request.getUserId());
        List<Box> rentals = new ArrayList<>();
        for (Checkout.Item item : request.getItems()) {
            Box box = boxes.findOne(item.getBoxId()).checkout(user, item.getNrOfDays());
            user.addBonus(box.getFilm().getType().BONUS_POINTS);
            rentals.add(box);
        }
        return checkoutPrice(rentals);
    }

    @RequestMapping(value = "/checkin", method = RequestMethod.POST)
    public Price checkin(@RequestBody Checkin request) {
        List<Box> rentals = this.boxes.findAll(request.getBoxIds());
        Price price = checkinPrice(rentals);
        rentals.forEach(Box::checkin);
        return price;
    }
}
