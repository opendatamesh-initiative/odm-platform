package org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.interfaces;


import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.core.Component;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.core.ExternalResource;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "Port")
@Table(name = "DPV_PORTS", schema = "ODMREGISTRY")
public class Port extends Component implements Cloneable {

    @Id
    @Column(name="SEQUENCE_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sequenceId;

    @Column(name = "ID")
    private String id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "PROMISES_ID", referencedColumnName = "ID")
    private Promises promises;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "EXPECTATIONS_ID", referencedColumnName = "ID")
    private Expectations expectations;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "CONTRACTS_ID", referencedColumnName = "ID")
    private Contracts obligations;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "DPV_PORT_TAGS", schema = "ODMREGISTRY", joinColumns = @JoinColumn(name = "SEQUENCE_ID"))
    @Column(name = "TAG_ID")
    @Fetch(value = FetchMode.SUBSELECT)
    private List<String> tags = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "EXTERNAL_DOC_ID", referencedColumnName = "ID")
    private ExternalResource externalDocs;

    public Port() {
    }

    public boolean hasApi() {
        return getPromises() != null && getPromises().getApi() != null;
    }

    public boolean hasApiDefinition() {
        return hasApi() && getPromises().getApi().hasDefinition();
    }

    public String getId() {
        return this.id;
    }

    public Promises getPromises() {
        return this.promises;
    }

    public Expectations getExpectations() {
        return this.expectations;
    }

    public Contracts getObligations() {
        return this.obligations;
    }

    public List<String> getTags() {
        return this.tags;
    }

    public ExternalResource getExternalDocs() {
        return this.externalDocs;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPromises(Promises promises) {
        this.promises = promises;
    }

    public void setExpectations(Expectations expectations) {
        this.expectations = expectations;
    }

    public void setObligations(Contracts obligations) {
        this.obligations = obligations;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void setExternalDocs(ExternalResource externalDocs) {
        this.externalDocs = externalDocs;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Port)) return false;
        final Port other = (Port) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final Object this$promises = this.getPromises();
        final Object other$promises = other.getPromises();
        if (this$promises == null ? other$promises != null : !this$promises.equals(other$promises)) return false;
        final Object this$expectations = this.getExpectations();
        final Object other$expectations = other.getExpectations();
        if (this$expectations == null ? other$expectations != null : !this$expectations.equals(other$expectations))
            return false;
        final Object this$obligations = this.getObligations();
        final Object other$obligations = other.getObligations();
        if (this$obligations == null ? other$obligations != null : !this$obligations.equals(other$obligations))
            return false;
        final Object this$tags = this.getTags();
        final Object other$tags = other.getTags();
        if (this$tags == null ? other$tags != null : !this$tags.equals(other$tags)) return false;
        final Object this$externalDocs = this.getExternalDocs();
        final Object other$externalDocs = other.getExternalDocs();
        if (this$externalDocs == null ? other$externalDocs != null : !this$externalDocs.equals(other$externalDocs))
            return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Port;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $promises = this.getPromises();
        result = result * PRIME + ($promises == null ? 43 : $promises.hashCode());
        final Object $expectations = this.getExpectations();
        result = result * PRIME + ($expectations == null ? 43 : $expectations.hashCode());
        final Object $obligations = this.getObligations();
        result = result * PRIME + ($obligations == null ? 43 : $obligations.hashCode());
        final Object $tags = this.getTags();
        result = result * PRIME + ($tags == null ? 43 : $tags.hashCode());
        final Object $externalDocs = this.getExternalDocs();
        result = result * PRIME + ($externalDocs == null ? 43 : $externalDocs.hashCode());
        return result;
    }

    public String toString() {
        return "Port(id=" + this.getId() + ", promises=" + this.getPromises() + ", expectations=" + this.getExpectations() + ", obligations=" + this.getObligations() + ", tags=" + this.getTags() + ", externalDocs=" + this.getExternalDocs() + ")";
    }

    public Long getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(Long sequenceId) {
        this.sequenceId = sequenceId;
    }
}
