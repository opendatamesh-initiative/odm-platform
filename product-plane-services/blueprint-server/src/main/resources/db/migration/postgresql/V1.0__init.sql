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
    "BLUEPRINT_PATH" VARCHAR(255),
    "TARGET_PATH" VARCHAR(255),
    "CREATED_AT" TIMESTAMP,
    "UPDATED_AT" TIMESTAMP
);


-- BLUEPRINT CONFIGS =====================================
CREATE TABLE "ODMBLUEPRINT"."BLUEPRINT_CONFIGS" (
	"BLUEPRINT_ID" BIGINT NOT NULL,
	"PARAMETER" VARCHAR(255) NOT NULL,
	"PARAMETER_VALUE" VARCHAR(255)
);
ALTER TABLE "ODMBLUEPRINT"."BLUEPRINT_CONFIGS" ADD CONSTRAINT "C_PK_BLUEPRINT_CONFIGS" PRIMARY KEY("BLUEPRINT_ID", "PARAMETER");
ALTER TABLE "ODMBLUEPRINT"."BLUEPRINT_CONFIGS" ADD CONSTRAINT "C_FK_BLUEPRINT_CONFIGS" FOREIGN KEY("BLUEPRINT_ID") REFERENCES "ODMBLUEPRINT"."BLUEPRINTS"("ID");