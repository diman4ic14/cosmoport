package com.space.controller;


import com.space.exceptions.BadRequestException;
import com.space.model.Ship;
import com.space.model.ShipOrder;
import com.space.model.ShipType;
import com.space.service.ShipService;
import com.space.specification.ShipSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/ships")
public class ShipRestController {

    private ShipService shipService;

    @Autowired
    public void setShipService(ShipService shipService) {
        this.shipService = shipService;
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<Ship> getShip(@PathVariable("id") String id) {
        Long longId = checkId(id);

        if (longId < 1)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Ship ship = shipService.getShip(longId);

        if (null == ship)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(ship, HttpStatus.OK);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<Ship> addShip(@RequestBody Ship ship) {
        shipService.createShip(ship);
        return new ResponseEntity<>(ship, HttpStatus.OK);
    }

    @PostMapping(value = "/{id}" ,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<Ship> editShip(@PathVariable("id") String id, @RequestBody Ship ship) {
        Long idLong = checkId(id);

        return new ResponseEntity<>(shipService.editShip(idLong, ship), HttpStatus.OK);
    }


    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Ship> deleteShip(@PathVariable("id") String id) {
        Long idLong = checkId(id);

        shipService.deleteShip(idLong);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Ship>> getAllShips(@RequestParam(name = "name", required = false) String name,
                                                  @RequestParam(name = "planet", required = false) String planet,
                                                  @RequestParam(name = "shipType", required = false) ShipType shipType,
                                                  @RequestParam(name = "after", required = false) Long after,
                                                  @RequestParam(name = "before", required = false) Long before,
                                                  @RequestParam(name = "isUsed", required = false) Boolean isUsed,
                                                  @RequestParam(name = "minSpeed", required = false) Double minSpeed,
                                                  @RequestParam(name = "maxSpeed", required = false) Double maxSpeed,
                                                  @RequestParam(name = "minCrewSize", required = false) Integer minCrewSize,
                                                  @RequestParam(name = "maxCrewSize", required = false) Integer maxCrewSize,
                                                  @RequestParam(name = "minRating", required = false) Double minRating,
                                                  @RequestParam(name = "maxRating", required = false) Double maxRating,
                                                  @RequestParam(name = "order", required = false, defaultValue = "ID") ShipOrder order,
                                                  @RequestParam(name = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
                                                  @RequestParam(name = "pageSize", required = false, defaultValue = "3") Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(order.getFieldName()));

        Specification<Ship> specification = ShipSpecification.getSpecification(name, planet, shipType,
                after, before, isUsed, minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating);

        Page<Ship> allShips = shipService.getAllShips(specification, pageRequest);
        return new ResponseEntity<>(allShips.getContent(), HttpStatus.OK);
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getCount(@RequestParam(name = "name", required = false) String name,
                                            @RequestParam(name = "planet", required = false) String planet,
                                            @RequestParam(name = "shipType", required = false) ShipType shipType,
                                            @RequestParam(name = "after", required = false) Long after,
                                            @RequestParam(name = "before", required = false) Long before,
                                            @RequestParam(name = "isUsed", required = false) Boolean isUsed,
                                            @RequestParam(name = "minSpeed", required = false) Double minSpeed,
                                            @RequestParam(name = "maxSpeed", required = false) Double maxSpeed,
                                            @RequestParam(name = "minCrewSize", required = false) Integer minCrewSize,
                                            @RequestParam(name = "maxCrewSize", required = false) Integer maxCrewSize,
                                            @RequestParam(name = "minRating", required = false) Double minRating,
                                            @RequestParam(name = "maxRating", required = false) Double maxRating) {

        Specification<Ship> specification = ShipSpecification.getSpecification(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed,
                minCrewSize, maxCrewSize, minRating, maxRating);
        return new ResponseEntity<>(shipService.getAllShips(specification).size(), HttpStatus.OK);
    }

    private Long checkId(String id) {
        if (id == null || id.equals("") || id.equals("0"))
            throw new BadRequestException("Incorrect ID");

        try {
            return Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new BadRequestException("ID is not digit", e);
        }
    }
}
