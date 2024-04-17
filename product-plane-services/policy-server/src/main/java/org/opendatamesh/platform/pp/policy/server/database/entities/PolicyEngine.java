package org.opendatamesh.platform.pp.policy.server.database.entities;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.opendatamesh.platform.core.commons.database.utils.TimestampedEntity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "POLICY_ENGINES", schema = "ODMPOLICY")
public class PolicyEngine extends TimestampedEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DISPLAY_NAME")
    private String displayName;

    @Column(name = "ADAPTER_URL")
    private String adapterUrl;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "policyEngineId")
    @Fetch(FetchMode.SELECT)
    @BatchSize(size = 100)
    private List<Policy> policies;

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

    public String getAdapterUrl() {
        return adapterUrl;
    }

    public void setAdapterUrl(String adapterUrl) {
        this.adapterUrl = adapterUrl;
    }

    public List<Policy> getPolicies() {
        return policies;
    }

    public void setPolicies(List<Policy> policies) {
        this.policies = policies;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
