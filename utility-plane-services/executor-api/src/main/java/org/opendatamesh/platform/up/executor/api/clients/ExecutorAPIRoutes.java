package org.opendatamesh.platform.up.executor.api.clients;

public enum ExecutorAPIRoutes {
       
        TASKS("/tasks");

        private final String path;

        private static final String CONTEXT_PATH = "/api/v1/up/executor";


        ExecutorAPIRoutes(String path) {
            this.path = CONTEXT_PATH + path;
        }

        @Override
        public String toString() {
            return this.path;
        }

        public String getPath() {
            return path;
        }
}
