package com.space.service;

import com.space.model.Ship;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface ShipService {

    Ship getShip(Long id);

    void createShip(Ship ship);

    void deleteShip(Long id);

    List<Ship> getAllShips(Specification<Ship> specification);

    Page<Ship> getAllShips(Specification<Ship> specification, Pageable sortedByName);

    Ship editShip(Long id, Ship ship);
}
