package org.opendatamesh.platform.pp.policy.api.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

public class PolicyEvaluationResultSearchOptions {

    @JsonProperty("dataProductId")
    @Schema(description = "The data product identifier of the evaluation result")
    private String dataProductId;

    @JsonProperty("dataProductVersion")
    @Schema(description = "The data product version of the evaluation result")
    private String dataProductVersion;

    @JsonProperty("daysFromLastCreated")
    @Schema(description = "Number of days from the most recent policy result to include in the search. Default is 30 days.")
    private Integer daysFromLastCreated = 30;

    public String getDataProductId() {
        return dataProductId;
    }

    public void setDataProductId(String dataProductId) {
        this.dataProductId = dataProductId;
    }

    public String getDataProductVersion() {
        return dataProductVersion;
    }

    public void setDataProductVersion(String dataProductVersion) {
        this.dataProductVersion = dataProductVersion;
    }

    public Integer getDaysFromLastCreated() {
        return daysFromLastCreated;
    }

    public void setDaysFromLastCreated(Integer daysFromLastCreated) {
        this.daysFromLastCreated = daysFromLastCreated;
    }

}
