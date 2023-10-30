-- Postgres 11;

CREATE SCHEMA IF NOT EXISTS "ODMBLUEPRINT";
CREATE SEQUENCE "ODMBLUEPRINT".HIBERNATE_SEQUENCE START WITH 1;


-- BLUEPRINTS ============================================
CREATE TABLE "ODMBLUEPRINT"."BLUEPRINTS"(
    "ID" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY,
    "NAME" VARCHAR(255),
    "VERSION" VARCHAR(32),
    "DISPLAY_NAME" VARCHAR(255),
    "DESCRIPTION" VARCHAR,
    "REPOSITORY_PROVIDER" VARCHAR(32),
    "REPOSITORY_URL" VARCHAR(1000),
    "ORGANIZATION" VARCHAR(255),
    "PROJECT_NAME" VARCHAR(255),
    "CREATED_AT" TIMESTAMP,
    "UPDATED_AT" TIMESTAMP
);