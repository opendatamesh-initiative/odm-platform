package org.opendatamesh.platform.pp.registry.server.database.entities.dataproduct;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import lombok.Data;

@Data
@Embeddable
public class LifecycleInfo {

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinColumns({
            @JoinColumn(name = "DATA_PRODUCT_ID"),
            @JoinColumn(name = "DATA_PRODUCT_VERSION")
    })
    private List<LifecycleActivityInfo> activityInfos = new ArrayList<LifecycleActivityInfo>();
}
