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

     ParkingLot parkingLot=parkingLotRepository3.findById(parkingLotId).get();
     if(parkingLot==null) throw new Exception("Cannot make reservation");
     User user=userRepository3.findById(userId).get();
     if(user==null) throw new Exception("Cannot make reservation");

     List<Spot> list=parkingLot.getSpotList();
     Collections.sort(list, new Comparator<Spot>() {
         @Override
         public int compare(Spot o1, Spot o2) {
             return o1.getPricePerHour()- o2.getPricePerHour();
         }
     });

     SpotType spotType=SpotType.TWO_WHEELER;
        if(numberOfWheels>2 && numberOfWheels<=4)
        {
            spotType=SpotType.FOUR_WHEELER;
        }
        else if(numberOfWheels>4) spotType=SpotType.OTHERS;
        int minPrice=Integer.MAX_VALUE;
        int id=0;
     for(Spot spot:list)
     {
         if(spot.getSpotType()==spotType && spot.getOccupied()==Boolean.FALSE)
         {
             minPrice=Math.min(spot.getPricePerHour()*timeInHours,minPrice);
             id= spot.getId();
         }
     }
     if(minPrice==Integer.MIN_VALUE) throw new Exception("Cannot make reservation");


     Spot spot=spotRepository3.findById(id).get();
     Reservation reservation=new Reservation();
     reservation.setSpot(spot);
     reservation.setUser(user);
     reservation.setNumberOfHours(timeInHours);
     //reservation.setPayment();
        spot.setOccupied(Boolean.TRUE);
        //saving it before to get PK of reservation to avoid collisions
        reservation=reservationRepository3.save(reservation);


        user.getReservationList().add(reservation);
        spot.getReservationList().add(reservation);


        userRepository3.save(user);
        spotRepository3.save(spot);

     return reservation;
    }
}
