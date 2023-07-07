package org.opendatamesh.platform.pp.registry.resources.v1.observers;

import java.util.ArrayList;
import java.util.List;

import org.opendatamesh.platform.up.notification.api.resources.EventResource;

public class EventNotifier {
    private List<Observer> observers = new ArrayList<>();

    public void addObserver(Observer observer) {
        this.observers.add(observer);
    }

    public void removeObserver(Observer observer) {
        this.observers.remove(observer);
    }

    public List<Observer> getObservers() { return this.observers; }

    public void notifyEvent(EventResource event) {
        for (Observer observer : this.observers) {
            observer.notify(event);
        }
    }

}
