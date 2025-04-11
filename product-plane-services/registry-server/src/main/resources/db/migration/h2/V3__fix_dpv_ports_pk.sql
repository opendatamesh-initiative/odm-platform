
CREATE TABLE temp_DPV_PORTS AS
SELECT *
FROM "ODMREGISTRY"."DPV_PORTS";

CREATE TABLE temp_DPV_PORT_TAGS AS
SELECT *
FROM "ODMREGISTRY"."DPV_PORT_TAGS";

-- Drop DPV_PORTS Constraints
ALTER TABLE "ODMREGISTRY"."DPV_PORTS" DROP CONSTRAINT  "C_FK1_DPV_PORTS";
ALTER TABLE "ODMREGISTRY"."DPV_PORTS" DROP CONSTRAINT  "C_FK2_DPV_PORTS";
ALTER TABLE "ODMREGISTRY"."DPV_PORTS" DROP CONSTRAINT  "C_FK3_DPV_PORTS";
ALTER TABLE "ODMREGISTRY"."DPV_PORTS" DROP CONSTRAINT  "C_FK4_DPV_PORTS";
ALTER TABLE "ODMREGISTRY"."DPV_PORTS" DROP CONSTRAINT  "C_FK5_DPV_PORTS";

-- Drop DPV_PORT_TAGS Constraints
ALTER TABLE "ODMREGISTRY"."DPV_PORT_TAGS" DROP CONSTRAINT  "FKSXD77OQ8WIO381WHOJWICLXLG";

DROP TABLE "ODMREGISTRY"."DPV_PORTS";
DROP TABLE "ODMREGISTRY"."DPV_PORT_TAGS";

CREATE MEMORY TABLE "ODMREGISTRY"."DPV_PORTS" (
    "DATA_PRODUCT_ID" CHARACTER VARYING(255),
    "DATA_PRODUCT_VERSION" CHARACTER VARYING(255),

    "SEQUENCE_ID" BIGSERIAL,
    "ID" CHARACTER VARYING(255) NOT NULL,
    "FQN" CHARACTER VARYING(255),
    "ENTITY_TYPE" CHARACTER VARYING(255),
    "NAME" CHARACTER VARYING(255),
    "VERSION" CHARACTER VARYING(255),

    "DISPLAY_NAME" CHARACTER VARYING(255),
    "DESCRIPTION" CHARACTER VARYING,

    "PROMISES_ID" BIGINT,
    "EXPECTATIONS_ID" BIGINT,
    "CONTRACTS_ID" BIGINT,

    "COMPONENT_GROUP" CHARACTER VARYING(255),
    "EXTERNAL_DOC_ID" BIGINT,

    "CONTENT" CHARACTER VARYING,

    "CREATED_AT" TIMESTAMP,
    "UPDATED_AT" TIMESTAMP
);
ALTER TABLE "ODMREGISTRY"."DPV_PORTS" ADD CONSTRAINT "ODMREGISTRY"."C_PK_DPV_PORTS" PRIMARY KEY("SEQUENCE_ID");

CREATE MEMORY TABLE "ODMREGISTRY"."DPV_PORT_TAGS"(
    "SEQUENCE_ID" BIGINT,
    "TAG_ID" CHARACTER VARYING(255)
);

ALTER TABLE "ODMREGISTRY"."DPV_PORTS" ADD CONSTRAINT  "C_FK1_DPV_PORTS" FOREIGN KEY("DATA_PRODUCT_ID", "DATA_PRODUCT_VERSION") REFERENCES "ODMREGISTRY"."DP_VERSIONS"("DATA_PRODUCT_ID", "VERSION_NUMBER")  ON DELETE CASCADE;
ALTER TABLE "ODMREGISTRY"."DPV_PORTS" ADD CONSTRAINT  "C_FK2_DPV_PORTS" FOREIGN KEY("PROMISES_ID") REFERENCES "ODMREGISTRY"."DPV_PORT_PROMISES"("ID")  ON DELETE CASCADE;
ALTER TABLE "ODMREGISTRY"."DPV_PORTS" ADD CONSTRAINT  "C_FK3_DPV_PORTS" FOREIGN KEY("EXPECTATIONS_ID") REFERENCES "ODMREGISTRY"."DPV_PORT_EXPECTATIONS"("ID")  ON DELETE CASCADE;
ALTER TABLE "ODMREGISTRY"."DPV_PORTS" ADD CONSTRAINT  "C_FK4_DPV_PORTS" FOREIGN KEY("CONTRACTS_ID") REFERENCES "ODMREGISTRY"."DPV_PORT_CONTRACTS"("ID")  ON DELETE CASCADE;
ALTER TABLE "ODMREGISTRY"."DPV_PORTS" ADD CONSTRAINT  "C_FK5_DPV_PORTS" FOREIGN KEY("EXTERNAL_DOC_ID") REFERENCES "ODMREGISTRY"."DPV_EXTERNAL_RESOURCES"("ID")  ON DELETE CASCADE;
ALTER TABLE "ODMREGISTRY"."DPV_PORT_TAGS" ADD CONSTRAINT  "FKSXD77OQ8WIO381WHOJWICLXLG" FOREIGN KEY("SEQUENCE_ID") REFERENCES "ODMREGISTRY"."DPV_PORTS"("SEQUENCE_ID")  ON DELETE CASCADE;


-- Create a mapping table to capture the relationship between the old "ID" and the new "SEQUENCE_ID"
CREATE TABLE temp_DPV_PORTS_mapping (
    old_id CHARACTER VARYING(255),
    sequence_id BIGINT
);

-- Insert data into the new DPV_PORTS table from the backup.
INSERT INTO "ODMREGISTRY"."DPV_PORTS" (
    "DATA_PRODUCT_ID",
    "DATA_PRODUCT_VERSION",
    "ID",
    "FQN",
    "ENTITY_TYPE",
    "NAME",
    "VERSION",
    "DISPLAY_NAME",
    "DESCRIPTION",
    "PROMISES_ID",
    "EXPECTATIONS_ID",
    "CONTRACTS_ID",
    "COMPONENT_GROUP",
    "EXTERNAL_DOC_ID",
    "CONTENT",
    "CREATED_AT",
    "UPDATED_AT"
)
SELECT
    "DATA_PRODUCT_ID",
    "DATA_PRODUCT_VERSION",
    "ID",
    "FQN",
    "ENTITY_TYPE",
    "NAME",
    "VERSION",
    "DISPLAY_NAME",
    "DESCRIPTION",
    "PROMISES_ID",
    "EXPECTATIONS_ID",
    "CONTRACTS_ID",
    "COMPONENT_GROUP",
    "EXTERNAL_DOC_ID",
    "CONTENT",
    "CREATED_AT",
    "UPDATED_AT"
FROM temp_DPV_PORTS;

-- Populate the mapping table by selecting the new SEQUENCE_ID along with the original "ID".
INSERT INTO temp_DPV_PORTS_mapping (old_id, sequence_id)
SELECT "ID", "SEQUENCE_ID"
FROM "ODMREGISTRY"."DPV_PORTS";

--Restore data into DPV_PORT_TAGS using the mapping to assign the correct SEQUENCE_ID.
INSERT INTO "ODMREGISTRY"."DPV_PORT_TAGS" ("SEQUENCE_ID", "TAG_ID")
SELECT m.sequence_id, t."TAG_ID"
FROM temp_DPV_PORT_TAGS t
JOIN temp_DPV_PORTS_mapping m
  ON t."ID" = m.old_id;

--Clean up tables (optional cleanup)
DROP TABLE temp_DPV_PORTS;
DROP TABLE temp_DPV_PORT_TAGS;
DROP TABLE temp_DPV_PORTS_mapping;