package org.opendatamesh.platform.pp.registry.resources.v1.observers.metaservice;

import lombok.Data;
import org.opendatamesh.platform.pp.registry.resources.v1.observers.Observer;

@Data
public class MetaServiceObserver implements Observer {

    private String prop;

    @Override
    public void update(Object o) {
        this.setProp((String) o);
    }
}
