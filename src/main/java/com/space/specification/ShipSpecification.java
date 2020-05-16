package com.space.specification;

import com.space.model.Ship;
import com.space.model.ShipOrder;
import com.space.model.ShipType;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;

public class ShipSpecification {

    public static Specification<Ship> getSpecification(String name, String planet, ShipType shipType, Long after,
                                                       Long before, Boolean isUsed, Double minSpeed, Double maxSpeed,
                                                       Integer minCrewSize, Integer maxCrewSize, Double minRating,
                                                       Double maxRating) {

        return Specification.where(shipsByName(name))
                .and(shipsByPlanet(planet))
                .and(shipsByShipType(shipType))
                .and(shipsByDate(after, before))
                .and(shipsByUsed(isUsed))
                .and(shipsBySpeed(minSpeed, maxSpeed))
                .and(shipsByCrewSize(minCrewSize, maxCrewSize))
                .and(shipsByRating(minRating, maxRating));
    }


    public static Specification<Ship> shipsByRating(Double minRating, Double maxRating) {
        return ((root, query, criteriaBuilder) -> {
            if (minRating == null && maxRating == null)
                return null;

            if (minRating == null)
                return criteriaBuilder.lessThanOrEqualTo(root.get("rating"), maxRating);

            if (maxRating == null)
                return criteriaBuilder.greaterThanOrEqualTo(root.get("rating"), minRating);

            return criteriaBuilder.between(root.get("rating"), minRating, maxRating);
        });
    }


    public static Specification<Ship> shipsByName(String name) {
        return ((root, query, criteriaBuilder) ->
                name == null ? null : criteriaBuilder.like(root.get("name"), "%" + name + "%"));
    }


    public static Specification<Ship> shipsByPlanet(String planet) {
        return ((root, query, criteriaBuilder) ->
                planet == null ? null : criteriaBuilder.like(root.get("planet"), "%" + planet + "%"));
    }


    public static Specification<Ship> shipsByShipType(ShipType shipType) {
        return ((root, query, criteriaBuilder) ->
                shipType == null ? null : criteriaBuilder.equal(root.get("shipType"), shipType));
    }


    public static Specification<Ship> shipsByDate(Long after, Long before) {
        return ((root, query, criteriaBuilder) -> {
            if (after == null && before == null)
                return null;

            if (after == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("prodDate"), new Date(before));
            }

            if (before == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("prodDate"), new Date(after));
            }

            return criteriaBuilder.between(root.get("prodDate"), new Date(after), new Date(before));
        });
    }


    public static Specification<Ship> shipsByUsed(Boolean isUsed) {
        return ((root, query, criteriaBuilder) -> {
            if (isUsed == null)
                return null;

            if (isUsed)
                return criteriaBuilder.isTrue(root.get("isUsed"));
            else
                return criteriaBuilder.isFalse(root.get("isUsed"));
        });
    }


    public static Specification<Ship> shipsBySpeed(Double minSpeed, Double maxSpeed) {
        return ((root, query, criteriaBuilder) -> {
            if (minSpeed == null && maxSpeed == null)
                return null;

            if (maxSpeed == null)
                return criteriaBuilder.greaterThanOrEqualTo(root.get("speed"), minSpeed);

            if (minSpeed == null)
                return criteriaBuilder.lessThanOrEqualTo(root.get("speed"), maxSpeed);

            return criteriaBuilder.between(root.get("speed"), minSpeed, maxSpeed);
        });
    }


    public static Specification<Ship> shipsByCrewSize(Integer minCrewSize, Integer maxCrewSize) {
        return ((root, query, criteriaBuilder) -> {
            if (minCrewSize == null && maxCrewSize == null)
                return null;

            if (maxCrewSize == null)
                return criteriaBuilder.greaterThanOrEqualTo(root.get("crewSize"), minCrewSize);

            if (minCrewSize == null)
                return criteriaBuilder.lessThanOrEqualTo(root.get("crewSize"), maxCrewSize);

            return criteriaBuilder.between(root.get("crewSize"), minCrewSize, maxCrewSize);
        });
    }
}
