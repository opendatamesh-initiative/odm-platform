package org.opendatamesh.platform.pp.policy.server.services.proxies;

import org.opendatamesh.platform.pp.policy.api.resources.PolicyResource;
import org.opendatamesh.platform.pp.policy.server.client.notificationservice.NotificationServiceClient;
import org.opendatamesh.platform.pp.policy.server.client.utils.exceptions.ClientException;
import org.opendatamesh.platform.pp.policy.server.client.utils.exceptions.ClientResourceMappingException;
import org.opendatamesh.platform.pp.policy.server.resources.notificationservice.NotificationEventResource;
import org.opendatamesh.platform.pp.policy.server.resources.notificationservice.NotificationEventType;
import org.opendatamesh.platform.pp.policy.server.resources.notificationservice.eventstates.PolicyNotificationEventState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationProxy {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private NotificationServiceClient notificationClient;

    public void notifyPolicyCreated(PolicyResource policy) {
        PolicyNotificationEventState afterState = new PolicyNotificationEventState();
        afterState.setPolicy(policy);

        NotificationEventResource notificationEvent = new NotificationEventResource();
        notificationEvent.setAfterState(afterState.toJsonNode());
        notificationEvent.setType(NotificationEventType.POLICY_CREATED.name());
        notificationEvent.setEntityId(policy.getRootId().toString());

        notifyEvent(notificationEvent);
    }

    public void notifyPolicyDeleted(PolicyResource policy) {
        PolicyNotificationEventState beforeState = new PolicyNotificationEventState();
        beforeState.setPolicy(policy);

        NotificationEventResource notificationEvent = new NotificationEventResource();
        notificationEvent.setType(NotificationEventType.POLICY_DELETED.name());
        notificationEvent.setBeforeState(beforeState.toJsonNode());
        notificationEvent.setEntityId(policy.getRootId().toString());

        notifyEvent(notificationEvent);
    }

    public void notifyPolicyUpdated(PolicyResource before, PolicyResource after) {
        PolicyNotificationEventState afterState = new PolicyNotificationEventState();
        afterState.setPolicy(after);

        NotificationEventResource notificationEvent = new NotificationEventResource();
        notificationEvent.setAfterState(afterState.toJsonNode());
        notificationEvent.setType(NotificationEventType.POLICY_UPDATED.name());
        notificationEvent.setEntityId(after.getRootId().toString());

        notifyEvent(notificationEvent);
    }

    private void notifyEvent(NotificationEventResource event) {
        try {
            notificationClient.notifyEvent(event);
        } catch (ClientException | ClientResourceMappingException e) {
            log.warn(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
