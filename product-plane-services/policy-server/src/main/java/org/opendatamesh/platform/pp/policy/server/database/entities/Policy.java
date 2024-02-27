package org.opendatamesh.platform.pp.policy.server.database.entities;

import org.opendatamesh.platform.pp.policy.server.database.utils.TimestampedEntity;

import javax.persistence.*;

@Entity
@Table(name = "POLICIES", schema = "ODMPOLICY")
public class Policy extends TimestampedEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "root_id")
    private Long rootId;

    @Column(name = "name")
    private String name;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "description")
    private String description;

    @Column(name = "blocking_flag")
    private Boolean blockingFlag;

    @Column(name = "raw_content")
    private String rawContent;

    @Column(name = "suite")
    private String suite;

    @Column(name = "is_last_version")
    private Boolean isLastVersion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POLICY_ENGINE_ID")
    private PolicyEngine policyEngine;

    @Column(name = "POLICY_ENGINE_ID", insertable = false, updatable = false)
    private Long policyEngineId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRootId() {
        return rootId;
    }

    public void setRootId(Long rootId) {
        this.rootId = rootId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getBlockingFlag() {
        return blockingFlag;
    }

    public void setBlockingFlag(Boolean blockingFlag) {
        this.blockingFlag = blockingFlag;
    }

    public String getRawContent() {
        return rawContent;
    }

    public void setRawContent(String rawContent) {
        this.rawContent = rawContent;
    }

    public String getSuite() {
        return suite;
    }

    public void setSuite(String suite) {
        this.suite = suite;
    }

    public Boolean getLastVersion() {
        return isLastVersion;
    }

    public void setLastVersion(Boolean lastVersion) {
        isLastVersion = lastVersion;
    }

    public PolicyEngine getPolicyEngine() {
        return policyEngine;
    }

    public void setPolicyEngine(PolicyEngine policyEngine) {
        this.policyEngine = policyEngine;
        if (policyEngine != null) {
            this.policyEngineId = policyEngine.getId();
        }
    }

    public Long getPolicyEngineId() {
        return policyEngineId;
    }

    public void setPolicyEngineId(Long policyEngineId) {
        this.policyEngineId = policyEngineId;
        PolicyEngine pg = new PolicyEngine();
        pg.setId(policyEngineId);
        this.policyEngine = pg;
    }
}
