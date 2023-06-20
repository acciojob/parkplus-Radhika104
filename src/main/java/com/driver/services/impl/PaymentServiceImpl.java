package com.driver.services.impl;

import com.driver.model.Payment;
import com.driver.model.PaymentMode;
import com.driver.model.Reservation;
import com.driver.repository.PaymentRepository;
import com.driver.repository.ReservationRepository;
import com.driver.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    ReservationRepository reservationRepository2;
    @Autowired
    PaymentRepository paymentRepository2;

    @Override
    public Payment pay(Integer reservationId, int amountSent, String mode) throws Exception {
        if(reservationRepository2.findById(reservationId).get()==null) throw new Exception("reservation not exists");

        Reservation reservation=reservationRepository2.findById(reservationId).get();
        int bill=reservation.getNumberOfHours()*reservation.getSpot().getPricePerHour();

        if(amountSent<bill) throw new Exception("Insufficient Amount ");

        if(!mode.equalsIgnoreCase("cash") || !mode.equalsIgnoreCase("card") || !mode.equalsIgnoreCase("upi")) throw new Exception("Payment mode not detected");

        Payment payment=new Payment();
        PaymentMode paymentMode;
        if(mode.equalsIgnoreCase("cash")) paymentMode=PaymentMode.CASH;
        else if(mode.equalsIgnoreCase("card")) paymentMode=PaymentMode.CARD;
        else paymentMode=PaymentMode.UPI;
        payment.setPaymentMode(paymentMode);
        payment.setPaymentCompleted(true);
        reservation.getSpot().setOccupied(Boolean.FALSE);
        payment.setReservation(reservation);

        reservation.setPayment(payment);

        reservationRepository2.save(reservation);
        return payment;
     }
}
