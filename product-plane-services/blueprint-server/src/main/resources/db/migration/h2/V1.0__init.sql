CREATE USER IF NOT EXISTS "SA" SALT '582a7e34517160c6' HASH '6b2d59d21c92ab85fb76c10fe4a0873b2745eeeae07aa2001a8c42a5577baf72' ADMIN;
CREATE SCHEMA IF NOT EXISTS "ODMBLUEPRINT";
CREATE SEQUENCE "ODMBLUEPRINT"."HIBERNATE_SEQUENCE" START WITH 1;


-- BLUEPRINTS ============================================
CREATE MEMORY TABLE "ODMBLUEPRINT"."BLUEPRINTS"(
    "ID" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL,
    "NAME" CHARACTER VARYING(255),
    "VERSION" CHARACTER VARYING(32),
    "DISPLAY_NAME" CHARACTER VARYING(255),
    "DESCRIPTION" CHARACTER VARYING,
    "REPOSITORY_PROVIDER" CHARACTER VARYING(32),
    "REPOSITORY_URL" CHARACTER VARYING(1000),
    "ORGANIZATION" CHARACTER VARYING(255),
    "PROJECT_NAME" CHARACTER VARYING(255),
    "CREATED_AT" TIMESTAMP,
    "UPDATED_AT" TIMESTAMP
);
ALTER TABLE "ODMBLUEPRINT"."BLUEPRINTS" ADD CONSTRAINT "ODMBLUEPRINT"."C_PK_BLUEPRINTS" PRIMARY KEY("ID");