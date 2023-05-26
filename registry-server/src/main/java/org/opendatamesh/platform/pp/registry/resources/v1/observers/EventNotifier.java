package org.opendatamesh.platform.pp.registry.resources.v1.observers;

import org.opendatamesh.notification.EventResource;

import java.util.ArrayList;
import java.util.List;

public class EventNotifier {
    private List<Observer> observers = new ArrayList<>();

    public void addObserver(Observer observer) {
        this.observers.add(observer);
    }

    public void removeObserver(Observer observer) {
        this.observers.remove(observer);
    }

    public void notifyEvent(EventResource event) {
        for (Observer observer : this.observers) {
            observer.notify(event);
        }
    }

}
