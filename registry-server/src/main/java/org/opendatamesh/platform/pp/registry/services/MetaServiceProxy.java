package org.opendatamesh.platform.pp.registry.services;

import org.opendatamesh.notification.EventResource;
import org.opendatamesh.notification.NotificationResource;
import org.opendatamesh.platform.pp.registry.exceptions.BadGatewayException;
import org.opendatamesh.platform.pp.registry.exceptions.OpenDataMeshAPIStandardError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    public void postEventToMetaService(EventResource event) {

        if(skipmetaservice.equals("true")){
            logger.debug("Skipping meta service");
            return;
        }

        NotificationResource notification = new NotificationResource();
        notification.setEvent(event);

        try {

            ResponseEntity<NotificationResource> responseEntity = restTemplate
                    .postForEntity(
                            metaserviceaddress + "/api/v1/up/metaservice/notifications",
                            notification,
                            NotificationResource.class
                    );

            if(responseEntity.getStatusCode().is2xxSuccessful()){
                notification = responseEntity.getBody();
                logger.debug("Successfuly loaded information to Meta service system: " + notification.toString());
            } else {
                throw new BadGatewayException(
                        OpenDataMeshAPIStandardError.SC502_05_META_SERVICE_ERROR,
                        "An error occurred while comunicating with the metaService");
            }

        } catch (Exception e) {
            throw new BadGatewayException(
                    OpenDataMeshAPIStandardError.SC502_05_META_SERVICE_ERROR,
                    "metaService not reachable"
            );
        }
    }
}
