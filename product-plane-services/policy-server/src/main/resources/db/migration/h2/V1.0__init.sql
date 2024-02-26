-- H2

CREATE USER IF NOT EXISTS "SA" SALT '582a7e34517160c6' HASH '6b2d59d21c92ab85fb76c10fe4a0873b2745eeeae07aa2001a8c42a5577baf72' ADMIN;
CREATE SCHEMA IF NOT EXISTS "ODMPOLICY";
CREATE SEQUENCE "ODMPOLICY"."HIBERNATE_SEQUENCE" START WITH 1;

-- ENTITIES ============================================================================================================

-- POLICY_ENGINES ------------------------------------------------------------------------------------------------------
CREATE MEMORY TABLE "ODMPOLICY"."POLICY_ENGINES"(
    "UUID" CHARACTER VARYING(255) NOT NULL,
    "NAME" CHARACTER VARYING(255),
    "DISPLAY_NAME" CHARACTER VARYING(255),
    "ADAPTER_URL" CHARACTER VARYING(1000),
    "CREATED_AT" TIMESTAMP,
    "UPDATED_AT" TIMESTAMP
);
ALTER TABLE "ODMPOLICY"."POLICY_ENGINES" ADD CONSTRAINT "ODMPOLICY"."C_PK_POLICY_ENGINES" PRIMARY KEY("UUID");

-- POLICIES ------------------------------------------------------------------------------------------------------------
CREATE MEMORY TABLE "ODMPOLICY"."POLICIES"(
    "UUID" CHARACTER VARYING(255) NOT NULL,
    "NAME" CHARACTER VARYING(255),
    "DISPLAY_NAME" CHARACTER VARYING(255),
    "DESCRIPTION" CHARACTER VARYING,
    "BLOCKING_FLAG" BOOLEAN,
    "SUITE" CHARACTER VARYING(255), -- fase, tag, ...
    "RAW_CONTENT" CHARACTER VARYING,
    "CREATED_AT" TIMESTAMP,
    "UPDATED_AT" TIMESTAMP,
    "POLICY_ENGINE_UUID" CHARACTER VARYING(255)
);
ALTER TABLE "ODMPOLICY"."POLICIES" ADD CONSTRAINT "ODMPOLICY"."C_PK_POLICIES" PRIMARY KEY("UUID");

-- POLICIES_HISTORY
CREATE MEMORY TABLE "ODMPOLICY"."POLICIES_HISTORY"(
    "ID" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL,
    "POLICY_UUID" CHARACTER VARYING(255),
    "RAW_CONTENT" CHARACTER VARYING
);
ALTER TABLE "ODMPOLICY"."POLICIES_HISTORY" ADD CONSTRAINT "ODMPOLICY"."C_PK_POLICIES_HISTORY" PRIMARY KEY("ID");

-- POLICY_EVALUATION_RESULTS
CREATE MEMORY TABLE "ODMPOLICY"."POLICY_EVALUATION_RESULTS"(
    "ID" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL,
    "DATA_PRODUCT_ID" CHARACTER VARYING(255),
    "DATA_PRODUCT_VERSION" CHARACTER VARYING(255),
    "POLICY_HISTORY_ID" BIGINT,
    "POLICY_UUID" CHARACTER VARYING(255),
    "INPUT_OBJECT" CHARACTER VARYING,
    "OUTPUT_OBJECT" CHARACTER VARYING,
    "RESULT" BOOLEAN,
    "CREATED_AT" TIMESTAMP
);
ALTER TABLE "ODMPOLICY"."POLICY_EVALUATION_RESULTS" ADD CONSTRAINT "ODMPOLICY"."C_PK_POLICY_EVALUATION_RESULTS" PRIMARY KEY("ID");


-- FOREING KEYS ========================================================================================================

-- POLICIES FKs
ALTER TABLE "ODMPOLICY"."POLICIES" ADD CONSTRAINT "ODMPOLICY"."C_FK1__POLICIES" FOREIGN KEY("POLICY_ENGINE_UUID") REFERENCES "ODMPOLICY"."POLICY_ENGINES"("UUID") ON DELETE NO ACTION;

-- POLICIES_HISTORY FKs
ALTER TABLE "ODMPOLICY"."POLICIES_HISTORY" ADD CONSTRAINT "ODMPOLICY"."C_FK1__POLICIES_HISTORY" FOREIGN KEY("POLICY_UUID") REFERENCES "ODMPOLICY"."POLICIES"("UUID") ON DELETE CASCADE;

-- POLICY_EVALUATION_RESULTS FKs
ALTER TABLE "ODMPOLICY"."POLICY_EVALUATION_RESULTS" ADD CONSTRAINT "ODMPOLICY"."C_FK1__POLICY_EVALUATION_RESULTS" FOREIGN KEY("POLICY_UUID") REFERENCES "ODMPOLICY"."POLICIES"("UUID") ON DELETE CASCADE;
