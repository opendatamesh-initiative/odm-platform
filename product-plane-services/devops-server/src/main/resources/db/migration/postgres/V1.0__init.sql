-- Postgres 11;

CREATE SCHEMA IF NOT EXISTS "ODMDEVOPS";
CREATE SEQUENCE "ODMDEVOPS".HIBERNATE_SEQUENCE START WITH 1;


-- ACTIVITIES ============================================

CREATE TABLE "ODMDEVOPS"."ACTIVITIES"(
    "ID" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY,
    "DATA_PRODUCT_ID" CHARACTER VARYING(255),
    "DATA_PRODUCT_VERSION" CHARACTER VARYING(255),
    "TYPE" CHARACTER VARYING(125),
    "STATUS" CHARACTER VARYING(125),
    "RESULTS" CHARACTER VARYING,
    "ERRORS" CHARACTER VARYING,

    "CREATED_AT" TIMESTAMP,
    "STARTED_AT" TIMESTAMP,
    "FINISHED_AT" TIMESTAMP
);
