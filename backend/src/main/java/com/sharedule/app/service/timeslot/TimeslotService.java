package com.sharedule.app.service.timeslot;

import com.sharedule.app.observer.AppointmentObserverManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sharedule.app.dto.CreateTimeslotDTO;
import com.sharedule.app.model.timeslot.Timeslot;
import com.sharedule.app.model.transaction.Transaction;
import com.sharedule.app.repository.timeslot.TimeslotRepo;

import java.util.List;

@Service
public class TimeslotService {
    @Autowired
    private TimeslotRepo repo;

    @Autowired
    private AppointmentObserverManager appointmentObserver;

    public Timeslot createTimeslot(CreateTimeslotDTO timeslotDTO, Transaction transaction) {
        // For new timeslots, we only need to check seller's schedule since there's no buyer yet
        List<Timeslot> conflicts = repo.findConflictingTimeslots(
                null, // no buyer yet for new timeslots
                transaction.getSeller().getId(),
                timeslotDTO.getStartDateTime(),
                timeslotDTO.getEndDateTime(),
                "" // Empty string for new timeslots
        );
        
        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Timeslot conflicts with an existing schedule");
        }

        Timeslot timeslot = new Timeslot();
        timeslot.setStartDateTime(timeslotDTO.getStartDateTime());
        timeslot.setEndDateTime(timeslotDTO.getEndDateTime());
        timeslot.setTransaction(transaction);

        Timeslot saved = repo.save(timeslot);
        appointmentObserver.notifyAll(saved);
        return saved;
    }

    public Timeslot getTimeslotByTransactionId(String transactionId) {
        return repo.findByTransaction_Id(transactionId);
    }
}
