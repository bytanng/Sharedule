package com.sharedule.app.observer;

import com.sharedule.app.model.timeslot.Timeslot;
import com.sharedule.app.model.user.Users;
import com.sharedule.app.repository.user.UserRepo;
import com.sharedule.app.service.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BuyerNotifier implements AppointmentObserver {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepo userRepo;

    @Override
    public void notify(Timeslot timeslot) {
        Users buyer = timeslot.getTransaction().getBuyer();

        if (buyer != null) {
            emailService.sendEmail(
                    buyer.getEmail(),
                    "Booking Confirmed",
                    "Your timeslot has been successfully booked for " + timeslot.getStartDateTime()
            );
        }
    }
}
