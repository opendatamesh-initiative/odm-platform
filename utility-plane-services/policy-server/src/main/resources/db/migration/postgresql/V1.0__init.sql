-- Postgres 11

CREATE SCHEMA IF NOT EXISTS "ODMPOLICY";
CREATE SEQUENCE "ODMPOLICY".HIBERNATE_SEQUENCE START WITH 1;

CREATE TABLE "ODMPOLICY"."POLICY"(
    "ID" VARCHAR(255) PRIMARY KEY,
    "DISPLAY_NAME" VARCHAR(255),
    "DESCRIPTION" VARCHAR(1000),
    "RAW_POLICY" TEXT,
    "CREATED_AT" TIMESTAMP,
    "UPDATED_AT" TIMESTAMP
);

CREATE TABLE "ODMPOLICY"."SUITE"(
    "ID" VARCHAR(255) PRIMARY KEY,
    "DISPLAY_NAME" VARCHAR(255),
    "DESCRIPTION" VARCHAR(1000),
    "CREATED_AT" TIMESTAMP,
    "UPDATED_AT" TIMESTAMP
);

CREATE TABLE "ODMPOLICY"."SuiteEntity_policies"(
    "SuiteEntity_ID" VARCHAR(255) NOT NULL,
    "policies" VARCHAR(255)
);