package org.opendatamesh.platform.pp.registry.api.resources.events;

import org.opendatamesh.platform.pp.registry.api.resources.DataProductResource;

public class DataProductEventState {

    private DataProductResource dataProduct;

    public DataProductEventState() {
    }

    public DataProductEventState(DataProductResource dataProduct) {
        this.dataProduct = dataProduct;
    }

    public DataProductResource getDataProduct() {
        return dataProduct;
    }

    public void setDataProduct(DataProductResource dataProduct) {
        this.dataProduct = dataProduct;
    }
}
