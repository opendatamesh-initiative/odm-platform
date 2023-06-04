package org.opendatamesh.platform.up.metaservice.server.api.v1.controllers;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;

import org.opendatamesh.platform.up.metaservice.server.services.MetaServiceException;
import org.opendatamesh.platform.up.metaservice.server.services.NotificationService;
import org.opendatamesh.platform.up.notification.api.v1.controllers.AbstractNotificationController;
import org.opendatamesh.platform.up.notification.api.v1.resources.NotificationResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
public class NotificationController extends AbstractNotificationController {

    @Autowired
    private NotificationService notificationService;


    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    public NotificationController() { 
        logger.debug("Notification controller succesfully started");
    }


    public NotificationResource createNotification(
       NotificationResource notificationRes
    ) {
        try {
            notificationRes =  notificationService.createNotification(notificationRes);
        } catch (MetaServiceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return notificationRes;
    }

    public NotificationResource readOneNotification(
        Long notificationId
    ) {
       return notificationService.readOneNotification(notificationId);
    }


    public List<NotificationResource> searchNotifications(
      String eventType,
        String notificationStatus
        
    ) {
        List<NotificationResource> notificationResources = null;
        if(!StringUtils.hasText(eventType) && !StringUtils.hasText(notificationStatus)) {
            notificationResources = notificationService.readAllNotifications();
        } else {
            notificationResources = notificationService.searchNotificationsByEventAndStatus(eventType, notificationStatus);
        }
       
        return notificationResources;
    }

    public void deleteDataProduct(
        Long notificationId
    )  {
        notificationService.deleteNotification(notificationId);
    } 

    /* 
    @DeleteMapping("/{notificationId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteDataProduct(
        @Valid @PathVariable(value = "notificationId", required = true) Long notificationId
    )  {
        notificationService.deleteNotification(notificationId);
    }
    */
}
