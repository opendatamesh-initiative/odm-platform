package org.opendatamesh.platform.pp.event.notifier.api.clients;

import org.opendatamesh.platform.pp.event.notifier.api.controllers.DispatchController;
import org.opendatamesh.platform.pp.event.notifier.api.controllers.ObserverController;

public interface EventNotifierClient extends ObserverController, DispatchController {
}
