package org.opendatamesh.platform.pp.api.services;

import org.opendatamesh.platform.pp.api.database.entities.dataproduct.DataProductVersion;
import org.opendatamesh.platform.pp.api.database.entities.metaservice.Load;
import org.opendatamesh.platform.pp.api.exceptions.BadGatewayException;
import org.opendatamesh.platform.pp.api.exceptions.OpenDataMeshAPIStandardError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MetaServiceProxy {

    @Value("${skipmetaservice}")
    private String skipmetaservice;


    @Value("${metaserviceaddress}")
    private String metaserviceaddress;

    @Autowired
    private RestTemplate restTemplate;

    private static final Logger logger = LoggerFactory.getLogger(MetaServiceProxy.class);


    public MetaServiceProxy() {}

    public void uploadDataProductVersion(DataProductVersion dataProductVersion)  {
        if(skipmetaservice.equals("true")){
            logger.debug("Skipping meta service");
            return;
        }
        ResponseEntity<Load> responseEntity = restTemplate.postForEntity(metaserviceaddress+"/api/v1/planes/utility/meta-services/loads", dataProductVersion, Load.class);
        if(responseEntity.getStatusCode().is2xxSuccessful()){
            Load load = responseEntity.getBody();
            logger.debug("Successfully loaded information to Meta service system: " + load.toString());
        } else {
            throw new BadGatewayException(
                OpenDataMeshAPIStandardError.SC502_05_META_SERVICE_ERROR,
                "An error occurred while comunicating with the metaService");
        }
    }

    public void delete(String dataProductId) {
        if(skipmetaservice.equals("true")){
            logger.debug("Skipping delete in meta service");
            return;
        }
        restTemplate.delete(metaserviceaddress+"/api/v1/planes/utility/meta-services/loads?dataProductId=" + dataProductId);
    }

    public void deleteDataProductVersion(DataProductVersion dataProductVersion) {
        if(skipmetaservice.equals("true")){
            logger.debug("Skipping delete in meta service");
            return;
        }
        throw new RuntimeException("Method not implemented");
    }


    public void putInMetaService(DataProductVersion dataProductVersion)  {
        if (skipmetaservice.equals("true")) {
            logger.debug("Skipping put in Meta service");
            return;
        }
        ResponseEntity<Load> response = restTemplate.exchange(
                metaserviceaddress + "/api/v1/planes/utility/meta-services/loads",
                HttpMethod.PUT, new HttpEntity<>(dataProductVersion), Load.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            Load load = response.getBody();
            logger.debug("Successfully PUT information to Meta service system: " + load.toString());
        } else {
            throw new BadGatewayException(
                OpenDataMeshAPIStandardError.SC502_05_META_SERVICE_ERROR,
                "An error occurred while comunicating with the metaService");
        }
    }
}
