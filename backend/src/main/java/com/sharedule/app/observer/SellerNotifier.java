package com.sharedule.app.observer;

import com.sharedule.app.model.timeslot.Timeslot;
import com.sharedule.app.model.user.Users;
import com.sharedule.app.repository.user.UserRepo;
import com.sharedule.app.service.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SellerNotifier implements AppointmentObserver {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepo userRepo;

    @Override
    public void notify(Timeslot timeslot) {
        Users seller = timeslot.getTransaction().getSeller();

        if (seller != null) {
            emailService.sendEmail(
                    seller.getEmail(),
                    "You have a new booking",
                    "You have been booked at " + timeslot.getStartDateTime()
            );
        }
    }
}
