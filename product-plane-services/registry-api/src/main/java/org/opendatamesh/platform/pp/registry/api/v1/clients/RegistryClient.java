package org.opendatamesh.platform.pp.registry.api.v1.clients;

import java.util.Collections;
import java.util.Objects;

import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.pp.registry.api.v1.resources.DataProductResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class RegistryClient {
    String address;
    RestTemplate restTemplate;

    public RegistryClient(String address) {
        this.address = Objects.requireNonNull(address);
        restTemplate = new RestTemplate();
    }

    
    public DataProductResource readDataProduct(Long id) {

        ResponseEntity<DataProductResource> getResponse =  restTemplate.getForEntity(
            apiUrlOfItem(Routes.DATA_PRODUCTS),
            DataProductResource.class,
            id);

        return getResponse.getBody();
    }

    public DataProductVersionDPDS readDataProductVersion(Long id, String versionNumber) {

        ResponseEntity<DataProductVersionDPDS> getResponse =  restTemplate.getForEntity(
            apiUrlOfItem(Routes.DATA_PRODUCTS)+ "/versions/{versionNumer}",
            DataProductVersionDPDS.class,
            id, versionNumber);

        return getResponse.getBody();
    }

    protected String apiUrl(Routes route) {
        return apiUrl(route, "");
    }

    protected String apiUrl(Routes route, String extension) {
        return apiUrlFromString(route.getPath() + extension);
    }

    protected String apiUrlOfItem(Routes route) {
        return apiUrl(route, "/{id}");
    }

    protected String apiUrlFromString(String routeUrlString) {
        return address + routeUrlString;
    }

    private static enum Routes {

        DATA_PRODUCTS("/api/v1/pp/products");

        private final String path;

        private Routes(String path) {
            this.path = path;
        }

        @Override
        public String toString() {
            return this.path;
        }

        public String getPath() {
            return path;
        }
    }    
}
