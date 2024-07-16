package org.opendatamesh.platform.pp.blueprint.api.resources;

import io.swagger.v3.oas.annotations.media.Schema;

public class BlueprintSearchOptions {
    @Schema(description = "The Blueprint's name or projectId to search for")
    private String search;

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
