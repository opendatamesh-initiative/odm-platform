package org.opendatamesh.platform.pp.registry.resources.v1.observers;

import java.util.ArrayList;
import java.util.List;

public class ObserverRegistry {

    private String prop;
    private List<Observer> observers = new ArrayList<>();

    public void addObserver(Observer observer) {
        this.observers.add(observer);
    }

    public void removeObserver(Observer observer) {
        this.observers.remove(observer);
    }

    public void setProp(String prop) {
        this.prop = prop;
        for (Observer observer : this.observers) {
            observer.update(this.prop);
        }
    }

}
