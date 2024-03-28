package org.opendatamesh.platform.up.executor.api.clients;

import org.opendatamesh.platform.core.commons.clients.ODMApiRoutes;

public enum ExecutorAPIRoutes implements ODMApiRoutes {
       
        TASKS("/tasks"),

        TASK_STATUS("/tasks/{id}/status");

        private final String path;

        private static final String CONTEXT_PATH = "/api/v1/up/executor";


        ExecutorAPIRoutes(String path) {
            this.path = CONTEXT_PATH + path;
        }

        @Override
        public String getPath() {
            return path;
        }
}
