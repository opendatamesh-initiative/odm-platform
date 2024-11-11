UPDATE "ODMPOLICY"."POLICIES"
SET "EVALUATION_EVENT" = 'DATA_PRODUCT_VERSION_CREATION'
WHERE "EVALUATION_EVENT" = 'DATA_PRODUCT_CREATION';

UPDATE "ODMPOLICY"."POLICIES"
SET "EVALUATION_EVENT" = 'DATA_PRODUCT_VERSION_CREATION'
WHERE "EVALUATION_EVENT" = 'DATA_PRODUCT_UPDATE';

CREATE TABLE IF NOT EXISTS "ODMPOLICY"."POLICIES_EVALUATION_EVENTS" (
    "SEQUENCE_ID" BIGSERIAL PRIMARY KEY,
    "POLICY_ID" BIGINT REFERENCES "ODMPOLICY"."POLICIES"("ID") ON DELETE CASCADE,
    "EVENT" VARCHAR(255)
);
