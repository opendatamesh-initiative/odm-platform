-- H2 2.1.214;

CREATE USER IF NOT EXISTS "SA" SALT '582a7e34517160c6' HASH '6b2d59d21c92ab85fb76c10fe4a0873b2745eeeae07aa2001a8c42a5577baf72' ADMIN;
CREATE SCHEMA IF NOT EXISTS "ODMDEVOPS";
CREATE SEQUENCE "ODMDEVOPS"."HIBERNATE_SEQUENCE" START WITH 1;

-- ACTIVITIES ============================================

CREATE MEMORY TABLE "ODMDEVOPS"."ACTIVITIES"(
    "ID" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL,
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
ALTER TABLE "ODMDEVOPS"."ACTIVITIES" ADD CONSTRAINT "ODMDEVOPS"."C_PK_ACTIVITIES" PRIMARY KEY("ID");


-- TASKS ============================================

CREATE MEMORY TABLE "ODMDEVOPS"."TASKS"(
    "ID" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL,
    "ACTIVITY_ID" BIGINT,
    "EXECUTOR_REF" CHARACTER VARYING(500),
    "TEMPLATE" CHARACTER VARYING,
    "CONFIGURATIONS" CHARACTER VARYING,
    "STATUS" CHARACTER VARYING(125),
    "RESULTS" CHARACTER VARYING,
    "ERRORS" CHARACTER VARYING,
    
    "CREATED_AT" TIMESTAMP,
    "STARTED_AT" TIMESTAMP,
    "FINISHED_AT" TIMESTAMP
   
);
ALTER TABLE "ODMDEVOPS"."TASKS" ADD CONSTRAINT "ODMDEVOPS"."C_PK_TASKS" PRIMARY KEY("ID");

-- TASKS FKs

ALTER TABLE "ODMDEVOPS"."TASKS" ADD CONSTRAINT "ODMDEVOPS"."C_FK1__TASKS" FOREIGN KEY("ACTIVITY_ID") REFERENCES "ODMDEVOPS"."ACTIVITIES"("ID") NOCHECK;