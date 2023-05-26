package org.opendatamesh.platform.pp.registry.resources.v1.observers;

import org.opendatamesh.notification.EventResource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EventNotifier {
    private EventResource event;
    private List<Observer> observers = new ArrayList<>();

    public void addObserver(Observer observer) {
        this.observers.add(observer);
    }

    public void removeObserver(Observer observer) {
        this.observers.remove(observer);
    }

    public void notifyEvent(EventResource event) {
        this.event = event;
        for (Observer observer : this.observers) {
            observer.notify(this.event);
        }
    }

}
