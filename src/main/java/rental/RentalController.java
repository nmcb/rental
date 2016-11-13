package rental;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import rental.api.Checkin;
import rental.api.Checkout;
import rental.dal.BoxRepository;
import rental.dal.UserRepository;
import rental.mdl.Box;
import rental.mdl.Price;
import rental.mdl.User;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static rental.mdl.Price.checkinPrice;
import static rental.mdl.Price.checkoutPrice;

@Transactional
@RestController
@RequestMapping("/boxes")
public class RentalController {
    private final UserRepository userRepository;
    private final BoxRepository boxRepository;

    @Autowired
    RentalController(UserRepository userRepository, BoxRepository boxRepository) {
        this.userRepository = userRepository;
        this.boxRepository = boxRepository;
    }

    @RequestMapping(value = "/checkout", method = RequestMethod.POST)
    public Price checkout(@RequestBody Checkout request) {
        User user = userRepository.findOne(request.getUserId());
        List<Box> boxes = new ArrayList<>();
        for (Checkout.Item item : request.getItems()) {
            Box box = boxRepository.findOne(item.getBoxId()).checkout(user, item.getNrOfDays());
            user.addBonus(box.getFilm().getType().getBonusPoints());
            boxes.add(box);
        }
        return checkoutPrice(boxes);
    }

    @RequestMapping(value = "/checkin", method = RequestMethod.POST)
    public Price checkin(@RequestBody Checkin request) {
        List<Box> boxes = boxRepository.findAll(request.getBoxIds());
        Price price = checkinPrice(boxes);
        boxes.forEach(Box::checkin);
        return price;
    }

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<Box> add(@RequestBody Box box) {
        Box added = this.boxRepository.save(box);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(added.getId()).toUri();
        return ResponseEntity.created(location).body(added);
    }

    @RequestMapping("/{id}")
    public Box byId(@PathVariable(value = "id") Long id) {
        return boxRepository.findOne(id);
    }

    @RequestMapping
    public List<Box> all() {
        return boxRepository.findAll();
    }
}
