-- Postgres 11;

CREATE SCHEMA IF NOT EXISTS "ODMNOTIFICATION";
CREATE SEQUENCE IF NOT EXISTS "ODMNOTIFICATION".HIBERNATE_SEQUENCE START WITH 1;


-- ENTITIES ============================================================================================================

-- OBSERVERS -----------------------------------------------------------------------------------------------------------
CREATE MEMORY TABLE "ODMNOTIFICATION"."OBSERVERS"(
    "ID" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY,
    "NAME" VARCHAR(255),
    "DISPLAY_NAME" VARCHAR(255),
    "OBSERVER_URL" VARCHAR(1000),
    "CREATED_AT" TIMESTAMP,
    "UPDATED_AT" TIMESTAMP
);

-- NOTIFICATIONS -------------------------------------------------------------------------------------------------------
CREATE TABLE "ODMNOTIFICATION"."NOTIFICATIONS"(
    "ID" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY,
    "OBSERVER_ID" BIGINT,
    "EVENT_ID" BIGINT,
    "EVENT_TYPE" VARCHAR(255),
    "EVENT_ENTITY_ID" VARCHAR(255),
    "EVENT_BEFORE_STATE" TEXT,
    "EVENT_AFTER_STATE" TEXT,
    "EVENT_TIME" TIMESTAMP,
    "STATUS" VARCHAR(255),
    "PROCESSING_OUTPUT" VARCHAR,
    "RECEIVED_AT" TIMESTAMP,
    "PROCESSED_AT" TIMESTAMP
);


-- FOREING KEYS ========================================================================================================

-- NOTIFICATIONS FKs
ALTER TABLE "ODMNOTIFICATION"."NOTIFICATIONS" ADD CONSTRAINT "C_FK1__NOTIFICATIONS" FOREIGN KEY("OBSERVER_ID") REFERENCES "ODMNOTIFICATION"."OBSERVERS"("ID") ON DELETE NO ACTION;
