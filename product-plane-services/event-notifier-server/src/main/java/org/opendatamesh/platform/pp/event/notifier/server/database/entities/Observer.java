package org.opendatamesh.platform.pp.event.notifier.server.database.entities;

import org.opendatamesh.platform.core.commons.database.utils.TimestampedEntity;

import javax.persistence.*;

@Entity(name = "Observer")
@Table(name = "OBSERVERS", schema = "ODMEVENTNOTIFIER")
public class Observer extends TimestampedEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DISPLAY_NAME")
    private String displayName;

    @Column(name = "OBSERVER_URL")
    private String observerServerBaseUrl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getObserverServerBaseUrl() {
        return observerServerBaseUrl;
    }

    public void setObserverServerBaseUrl(String observerServerBaseUrl) {
        this.observerServerBaseUrl = observerServerBaseUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
