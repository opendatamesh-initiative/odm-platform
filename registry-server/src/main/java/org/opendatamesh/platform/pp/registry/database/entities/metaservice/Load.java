package org.opendatamesh.platform.pp.registry.database.entities.metaservice;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.opendatamesh.platform.pp.registry.resources.v1.metaservice.LoadStatus;

import java.util.Date;
import java.util.Objects;

@Entity(name = "Load")
@Table( name = "DPDS_LOADS", schema="PUBLIC")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Load {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("dataproduct")
    @Column(name = "id_dataproduct")
    @NotNull
    private String dataproductId;

    @Column(name="id_metaservice")
    private String metaServiceId;

    @JsonProperty("status")
    @Enumerated(EnumType.STRING)
    private LoadStatus status;

    @Column(name="created_date")
    @JsonIgnore
    private Date createdDate;
    @Column(name="lastupdate_date")
    @JsonIgnore
    private Date lastupdateDate;

    public Load() {
    }

    public Load(String dataproductId) {
        this.dataproductId = dataproductId;
    }

    @JsonProperty("id_metaservice")
    public String getMetaServiceId() {
        return metaServiceId;
    }

    public void setMetaServiceId(String metaServiceId) {
        this.metaServiceId = metaServiceId;
    }

    public void setStatus(LoadStatus status) {
        this.status = status;
    }

    @PrePersist
    protected void onCreate() {
        createdDate = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        lastupdateDate = new Date();
    }

    public LoadStatus getStatus() {
        return status;
    }

    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDataproductId(String dataproductId) {
        this.dataproductId = dataproductId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Load)) return false;
        Load load = (Load) o;
        return getId().equals(load.getId()) && dataproductId.equals(load.dataproductId) && metaServiceId.equals(load.metaServiceId) && getStatus() == load.getStatus() && createdDate.equals(load.createdDate) && Objects.equals(lastupdateDate, load.lastupdateDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), dataproductId, metaServiceId, getStatus(), createdDate, lastupdateDate);
    }

    @Override
    public String toString() {
        return "Load{" +
                "id=" + id +
                ", dataproductId='" + dataproductId + '\'' +
                ", metaServiceId='" + metaServiceId + '\'' +
                ", status=" + status +
                ", createdDate=" + createdDate +
                ", lastupdateDate=" + lastupdateDate +
                '}';
    }
}