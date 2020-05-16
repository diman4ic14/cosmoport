package com.space.service;

import com.space.exceptions.BadRequestException;
import com.space.exceptions.ShipNotFoundException;
import com.space.model.Ship;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Service
public class ShipServiceImpl implements ShipService{

    @Autowired
    private ShipRepository shipRepository;


    @Override
    public Ship getShip(Long id) {
        Optional<Ship> optionalShip = shipRepository.findById(id);
        if (optionalShip.isPresent())
            return optionalShip.get();
        else
            throw new ShipNotFoundException("The ship is not found");
    }

    @Override
    public void createShip(Ship ship) {
        if (ship.getName() == null
                || ship.getPlanet() == null
                || ship.getShipType() == null
                || ship.getProdDate() == null
                || ship.getSpeed() == null
                || ship.getCrewSize() == null)
            throw new BadRequestException("One of the Params is null");

        checkValidShip(ship);

        if (ship.getUsed() == null)
            ship.setUsed(false);
        else
            ship.setUsed(ship.getUsed());

        ship.setRating(calculateRating(ship));

        shipRepository.saveAndFlush(ship);
    }

    @Override
    public void deleteShip(Long id) {
        Optional<Ship> optional = shipRepository.findById(id);
        if (!optional.isPresent())
            throw new ShipNotFoundException("The ship is not found");

        shipRepository.deleteById(id);
    }

    @Override
    public List<Ship> getAllShips(Specification<Ship> specification) {
        return shipRepository.findAll(specification);
    }

    @Override
    public Page<Ship> getAllShips(Specification<Ship> specification, Pageable sortedByName) {
        return shipRepository.findAll(specification, sortedByName);
    }

    @Override
    public Ship editShip(Long id, Ship ship) {
        checkValidShip(ship);

        Optional<Ship> optional = shipRepository.findById(id);

        if (!optional.isPresent())
            throw new ShipNotFoundException("The ship is not found");

        Ship editedShip = optional.get();

        if (ship.getName() != null)
            editedShip.setName(ship.getName());

        if (ship.getPlanet() != null)
            editedShip.setPlanet(ship.getPlanet());

        if (ship.getShipType() != null)
            editedShip.setShipType(ship.getShipType());

        if (ship.getProdDate() != null)
            editedShip.setProdDate(ship.getProdDate());

        if (ship.getUsed() != null)
            editedShip.setUsed(ship.getUsed());

        if (ship.getSpeed() != null)
            editedShip.setSpeed(ship.getSpeed());

        if (ship.getCrewSize() != null)
            editedShip.setCrewSize(ship.getCrewSize());


        editedShip.setRating(calculateRating(editedShip));
        return shipRepository.saveAndFlush(editedShip);
    }

    private void checkValidShip(Ship ship) {
        if (ship.getName() != null && (ship.getName().length() < 1 || ship.getName().length() > 50))
            throw new BadRequestException("Incorrect the name of the ship");

        if (ship.getPlanet() != null && (ship.getPlanet().length() < 1 || ship.getPlanet().length() > 50))
            throw new BadRequestException("Incorrect the planet of the ship");

        if (ship.getSpeed() != null && (ship.getSpeed() < 0.01d || ship.getSpeed() > 0.99d))
            throw new BadRequestException("Incorrect the speed of the ship");

        if (ship.getCrewSize() != null && (ship.getCrewSize() < 1 || ship.getCrewSize() > 9999))
            throw new BadRequestException("Incorrect the crew size of the ship");

        if (ship.getProdDate() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(ship.getProdDate());
            if (calendar.get(Calendar.YEAR) < 2800 || calendar.get(Calendar.YEAR) > 3019)
                throw new BadRequestException("Incorrect the production date of the ship");
        }
    }

    private Double calculateRating(Ship ship) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(ship.getProdDate());
        int y1 = calendar.get(Calendar.YEAR);
        BigDecimal rating = BigDecimal.valueOf((80 * ship.getSpeed() * (ship.getUsed() ? 0.5 : 1)) / (3019 - y1 + 1));
        rating = rating.setScale(2, RoundingMode.HALF_UP);
        return rating.doubleValue();
    }
}
