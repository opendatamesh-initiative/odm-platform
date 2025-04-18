-- Step 1: Back up existing data into  tables.
CREATE TABLE temp_DPV_PORTS AS
  SELECT * FROM "ODMREGISTRY"."DPV_PORTS";

CREATE TABLE temp_DPV_PORT_TAGS AS
  SELECT * FROM "ODMREGISTRY"."DPV_PORT_TAGS";

-- Step 2: Drop foreign key constraints on DPV_PORTS.
ALTER TABLE "ODMREGISTRY"."DPV_PORTS" DROP FOREIGN KEY "C_FK1_DPV_PORTS";
ALTER TABLE "ODMREGISTRY"."DPV_PORTS" DROP FOREIGN KEY "C_FK2_DPV_PORTS";
ALTER TABLE "ODMREGISTRY"."DPV_PORTS" DROP FOREIGN KEY "C_FK3_DPV_PORTS";
ALTER TABLE "ODMREGISTRY"."DPV_PORTS" DROP FOREIGN KEY "C_FK4_DPV_PORTS";
ALTER TABLE "ODMREGISTRY"."DPV_PORTS" DROP FOREIGN KEY "C_FK5_DPV_PORTS";

-- Drop foreign key constraint on DPV_PORT_TAGS.
ALTER TABLE "ODMREGISTRY"."DPV_PORT_TAGS" DROP FOREIGN KEY "FKSXD77OQ8WIO381WHOJWICLXLG";

-- Step 3: Drop the original tables.
DROP TABLE "ODMREGISTRY"."DPV_PORTS";
DROP TABLE "ODMREGISTRY"."DPV_PORT_TAGS";

-- Step 4: Recreate the DPV_PORTS table as a MEMORY table.
CREATE TABLE "ODMREGISTRY"."DPV_PORTS" (
    "DATA_PRODUCT_ID" VARCHAR(255),
    "DATA_PRODUCT_VERSION" VARCHAR(255),
    "SEQUENCE_ID" BIGINT AUTO_INCREMENT,
    "ID" VARCHAR(255) NOT NULL,
    "FQN" VARCHAR(255),
    "ENTITY_TYPE" VARCHAR(255),
    "NAME" VARCHAR(255),
    "VERSION" VARCHAR(255),
    "DISPLAY_NAME" VARCHAR(255),
    "DESCRIPTION" TEXT,
    "PROMISES_ID" BIGINT,
    "EXPECTATIONS_ID" BIGINT,
    "CONTRACTS_ID" BIGINT,
    "COMPONENT_GROUP" VARCHAR(255),
    "EXTERNAL_DOC_ID" BIGINT,
    "CONTENT" TEXT,
    "CREATED_AT" TIMESTAMP NULL,
    "UPDATED_AT" TIMESTAMP NULL,
    PRIMARY KEY ("SEQUENCE_ID")
) ENGINE=MEMORY;

-- Step 5: Recreate the DPV_PORT_TAGS table as a MEMORY table.
CREATE TABLE "ODMREGISTRY"."DPV_PORT_TAGS" (
    "SEQUENCE_ID" BIGINT,
    "TAG_ID" VARCHAR(255)
) ENGINE=MEMORY;

-- Step 6: Add foreign key constraints back to the DPV_PORTS table.
ALTER TABLE "ODMREGISTRY"."DPV_PORTS"
  ADD CONSTRAINT "C_FK1_DPV_PORTS"
  FOREIGN KEY ("DATA_PRODUCT_ID", "DATA_PRODUCT_VERSION")
  REFERENCES "ODMREGISTRY"."DP_VERSIONS" ("DATA_PRODUCT_ID", "VERSION_NUMBER")
  ON DELETE CASCADE;

ALTER TABLE "ODMREGISTRY"."DPV_PORTS"
  ADD CONSTRAINT "C_FK2_DPV_PORTS"
  FOREIGN KEY ("PROMISES_ID")
  REFERENCES "ODMREGISTRY"."DPV_PORT_PROMISES" ("ID")
  ON DELETE CASCADE;

ALTER TABLE "ODMREGISTRY"."DPV_PORTS"
  ADD CONSTRAINT "C_FK3_DPV_PORTS"
  FOREIGN KEY ("EXPECTATIONS_ID")
  REFERENCES "ODMREGISTRY"."DPV_PORT_EXPECTATIONS" ("ID")
  ON DELETE CASCADE;

ALTER TABLE "ODMREGISTRY"."DPV_PORTS"
  ADD CONSTRAINT "C_FK4_DPV_PORTS"
  FOREIGN KEY ("CONTRACTS_ID")
  REFERENCES "ODMREGISTRY"."DPV_PORT_CONTRACTS" ("ID")
  ON DELETE CASCADE;

ALTER TABLE "ODMREGISTRY"."DPV_PORTS"
  ADD CONSTRAINT "C_FK5_DPV_PORTS"
  FOREIGN KEY ("EXTERNAL_DOC_ID")
  REFERENCES "ODMREGISTRY"."DPV_EXTERNAL_RESOURCES" ("ID")
  ON DELETE CASCADE;

ALTER TABLE "ODMREGISTRY"."DPV_PORT_TAGS"
  ADD CONSTRAINT "FKSXD77OQ8WIO381WHOJWICLXLG"
  FOREIGN KEY ("SEQUENCE_ID")
  REFERENCES "ODMREGISTRY"."DPV_PORTS" ("SEQUENCE_ID")
  ON DELETE CASCADE;

-- Step 7: Create a  mapping table to map old IDs to new SEQUENCE_ID values.
CREATE TABLE temp_DPV_PORTS_mapping (
    old_id VARCHAR(255),
    sequence_id BIGINT
);

-- Step 8: Insert data from the backup into the new DPV_PORTS table.
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

-- Step 9: Populate the mapping table with the new SEQUENCE_ID values.
INSERT INTO temp_DPV_PORTS_mapping (old_id, sequence_id)
SELECT "ID", "SEQUENCE_ID"
FROM "ODMREGISTRY"."DPV_PORTS";

-- Step 10: Restore data into DPV_PORT_TAGS using the mapping table.
INSERT INTO "ODMREGISTRY"."DPV_PORT_TAGS" ("SEQUENCE_ID", "TAG_ID")
SELECT m.sequence_id, t."TAG_ID"
FROM temp_DPV_PORT_TAGS t
JOIN temp_DPV_PORTS_mapping m
  ON t."ID" = m.old_id;

-- Step 11: Clean up the  tables.
DROP TABLE temp_DPV_PORTS;
DROP TABLE temp_DPV_PORT_TAGS;
DROP TABLE temp_DPV_PORTS_mapping;
