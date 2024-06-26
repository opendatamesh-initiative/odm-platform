-- Postgres 11;

CREATE SCHEMA IF NOT EXISTS "ODMPOLICY";
CREATE SEQUENCE IF NOT EXISTS "ODMPOLICY".HIBERNATE_SEQUENCE START WITH 1;


-- ENTITIES ============================================================================================================

-- POLICY_ENGINES ------------------------------------------------------------------------------------------------------
CREATE TABLE "ODMPOLICY"."POLICY_ENGINES"(
    "ID" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL,
    "NAME" VARCHAR(255),
    "DISPLAY_NAME" VARCHAR(255),
    "ADAPTER_URL" VARCHAR(1000),
    "CREATED_AT" TIMESTAMP,
    "UPDATED_AT" TIMESTAMP
);
ALTER TABLE "ODMPOLICY"."POLICY_ENGINES" ADD CONSTRAINT "C_PK_POLICY_ENGINES" PRIMARY KEY("ID");

-- POLICIES ------------------------------------------------------------------------------------------------------------
CREATE TABLE "ODMPOLICY"."POLICIES"(
    "ID" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL,
    "ROOT_ID" BIGINT,
    "NAME" VARCHAR(255),
    "DISPLAY_NAME" VARCHAR(255),
    "DESCRIPTION" VARCHAR,
    "BLOCKING_FLAG" BOOLEAN,
    "SUITE" VARCHAR(255), -- fase, tag, ...
    "EVALUATION_EVENT" VARCHAR(255),
    "FILTERING_EXPRESSION" VARCHAR,
    "RAW_CONTENT" VARCHAR,
    "IS_LAST_VERSION" BOOLEAN,
    "CREATED_AT" TIMESTAMP,
    "UPDATED_AT" TIMESTAMP,
    "POLICY_ENGINE_ID" BIGINT NOT NULL
);
ALTER TABLE "ODMPOLICY"."POLICIES" ADD CONSTRAINT "C_PK_POLICIES" PRIMARY KEY("ID");

-- POLICY_EVALUATION_RESULTS
CREATE TABLE "ODMPOLICY"."POLICY_EVALUATION_RESULTS"(
    "ID" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL,
    "DATA_PRODUCT_ID" VARCHAR(255),
    "DATA_PRODUCT_VERSION" VARCHAR(255),
    "POLICY_ID" BIGINT NOT NULL,
    "INPUT_OBJECT" VARCHAR,
    "OUTPUT_OBJECT" VARCHAR,
    "RESULT" BOOLEAN,
    "CREATED_AT" TIMESTAMP,
    "UPDATED_AT" TIMESTAMP
);
ALTER TABLE "ODMPOLICY"."POLICY_EVALUATION_RESULTS" ADD CONSTRAINT "C_PK_POLICY_EVALUATION_RESULTS" PRIMARY KEY("ID");


-- FOREING KEYS ========================================================================================================

-- POLICIES FKs
ALTER TABLE "ODMPOLICY"."POLICIES" ADD CONSTRAINT "C_FK1__POLICIES" FOREIGN KEY("POLICY_ENGINE_ID") REFERENCES "ODMPOLICY"."POLICY_ENGINES"("ID") ON DELETE NO ACTION;

-- POLICY_EVALUATION_RESULTS FKs
ALTER TABLE "ODMPOLICY"."POLICY_EVALUATION_RESULTS" ADD CONSTRAINT "C_FK1__POLICY_EVALUATION_RESULTS" FOREIGN KEY("POLICY_ID") REFERENCES "ODMPOLICY"."POLICIES"("ID") ON DELETE CASCADE;
