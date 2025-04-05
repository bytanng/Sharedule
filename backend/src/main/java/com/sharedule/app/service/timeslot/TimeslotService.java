package com.sharedule.app.service.timeslot;

import com.sharedule.app.observer.AppointmentObserverManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sharedule.app.dto.CreateTimeslotDTO;
import com.sharedule.app.model.timeslot.Timeslot;
import com.sharedule.app.model.transaction.Transaction;
import com.sharedule.app.repository.timeslot.TimeslotRepo;

@Service
public class TimeslotService {
    @Autowired
    private TimeslotRepo repo;

    @Autowired
    private AppointmentObserverManager appointmentObserver;

    public Timeslot createTimeslot(CreateTimeslotDTO timeslotDTO, Transaction transaction) {

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
