-- see https://flywaydb.org/documentation/tutorials/repeatable
-- Disabled, developed only for testing purpose
-- To re-enable it rename the file R__<migration-name>.sql instead of __R__<migration-name>.sql

DROP VIEW IF EXISTS "TEST_VIEW";

CREATE VIEW "PUBLIC"."TEST_VIEW" AS
SELECT
    a."ID"
    , a."DESCRIPTION"
    , a."DOMAIN"
    , b."VERSION_NUMBER"
    , b."DESCRIPTION" AS "VERSION_DESCRIPTION"
FROM "PUBLIC"."DATA_PRODUCTS" a
    INNER JOIN "PUBLIC"."DP_VERSIONS" b
        ON a."ID" = b."DATA_PRODUCT_ID"
;