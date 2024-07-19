-- Postgres 11;

CREATE SCHEMA IF NOT EXISTS "ODMPARAM";
CREATE SEQUENCE IF NOT EXISTS "ODMPARAM".HIBERNATE_SEQUENCE START WITH 1;


-- PARAMS ============================================
CREATE TABLE "ODMPARAM"."PARAMS"(
    "ID" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY,
    "PARAM_NAME" VARCHAR(255),
    "PARAM_VALUE" VARCHAR(255),
    "SECRET" BOOLEAN DEFAULT FALSE,
    "DISPLAY_NAME" VARCHAR(255),
    "DESCRIPTION" VARCHAR,
    "CREATED_AT" TIMESTAMP,
    "UPDATED_AT" TIMESTAMP
);