package com.sharedule.app.observer;

import com.sharedule.app.model.timeslot.Timeslot;

public interface AppointmentObserver {
    void notify(Timeslot timeSlot);
}
