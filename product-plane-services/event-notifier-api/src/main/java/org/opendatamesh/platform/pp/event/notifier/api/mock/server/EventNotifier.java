package org.opendatamesh.platform.pp.event.notifier.api.mock.server;

import org.opendatamesh.platform.up.notification.api.resources.EventResource;

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

    public List<Observer> getObservers() { return this.observers; }

    public void notifyEvent(EventResource event) {
        for (Observer observer : this.observers) {
            observer.notify(event);
        }
    }

}
