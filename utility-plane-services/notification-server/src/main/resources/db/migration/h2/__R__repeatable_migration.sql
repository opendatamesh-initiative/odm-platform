-- see https://flywaydb.org/documentation/tutorials/repeatable

DROP VIEW IF EXISTS "PUBLIC"."TEST_VIEW";

CREATE VIEW "PUBLIC"."TEST_VIEW" AS
SELECT
    *
FROM "PUBLIC"."notification" a;