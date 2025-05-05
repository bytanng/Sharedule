package com.sharedule.app.observer;

import com.sharedule.app.model.timeslot.Timeslot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AppointmentObserverManager {

    private final List<AppointmentObserver> observers = new ArrayList<>();

    @Autowired
    public AppointmentObserverManager(List<AppointmentObserver> observers) {
        this.observers.addAll((observers));
    }

    public void notifyAll(Timeslot timeslot) {
        for (AppointmentObserver observer : observers) {
            observer.notify((timeslot));
        }
    }
}
