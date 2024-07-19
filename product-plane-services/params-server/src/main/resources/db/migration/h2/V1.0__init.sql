CREATE USER IF NOT EXISTS "SA" SALT '582a7e34517160c6' HASH '6b2d59d21c92ab85fb76c10fe4a0873b2745eeeae07aa2001a8c42a5577baf72' ADMIN;
CREATE SCHEMA IF NOT EXISTS "ODMPARAM";
CREATE SEQUENCE "ODMPARAM"."HIBERNATE_SEQUENCE" START WITH 1;


-- PARAMS ============================================
CREATE MEMORY TABLE "ODMPARAM"."PARAMS"(
    "ID" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL,
    "PARAM_NAME" CHARACTER VARYING(255),
    "PARAM_VALUE" CHARACTER VARYING(255),
    "SECRET" BOOLEAN DEFAULT FALSE,
    "DISPLAY_NAME" CHARACTER VARYING(255),
    "DESCRIPTION" CHARACTER VARYING,
    "CREATED_AT" TIMESTAMP,
    "UPDATED_AT" TIMESTAMP
);
ALTER TABLE "ODMPARAM"."PARAMS" ADD CONSTRAINT "ODMPARAM"."C_PK_PARAMS" PRIMARY KEY("ID");