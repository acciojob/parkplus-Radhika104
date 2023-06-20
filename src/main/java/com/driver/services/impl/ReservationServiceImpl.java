package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    UserRepository userRepository3;
    @Autowired
    SpotRepository spotRepository3;
    @Autowired
    ReservationRepository reservationRepository3;
    @Autowired
    ParkingLotRepository parkingLotRepository3;
    @Override
    public Reservation reserveSpot(Integer userId, Integer parkingLotId, Integer timeInHours, Integer numberOfWheels) throws Exception {

        try {
            ParkingLot parkingLot = parkingLotRepository3.findById(parkingLotId).get();
            User user = userRepository3.findById(userId).get();
            Reservation reservation = new Reservation();

            Spot spot1 = null;
            int minCost = Integer.MAX_VALUE;

            for (Spot spot : parkingLot.getSpotList()) {
                if (!spot.getOccupied() && numberOfWheels <= 2 && spot.getSpotType() == SpotType.TWO_WHEELER) {
                    if (minCost >= spot.getPricePerHour()) {
                        minCost = spot.getPricePerHour();
                        spot1 = spot;
                    }
                } else if (!spot.getOccupied() && numberOfWheels <= 4 && spot.getSpotType() == SpotType.FOUR_WHEELER) {
                    if (minCost >= spot.getPricePerHour()) {
                        minCost = spot.getPricePerHour();
                        spot1 = spot;
                    }
                } else {
                    if (!spot.getOccupied() && minCost >= spot.getPricePerHour()) {
                        minCost = spot.getPricePerHour();
                        spot1 = spot;
                    }
                }
            }
            if (spot1 == null) {
                throw new Exception("Cannot make reservation");
            }
            spot1.setOccupied(Boolean.TRUE);
            reservation.setSpot(spot1);
            reservation.setUser(user);
            reservation.setNumberOfHours(timeInHours);

            spot1.getReservationList().add(reservation);
            user.getReservationList().add(reservation);

            userRepository3.save(user);
            spotRepository3.save(spot1);

            return reservation;
        } catch (Exception ex) {
            return null;
        }
    }
}
