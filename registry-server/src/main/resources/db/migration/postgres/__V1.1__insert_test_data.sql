-- Disabled, developed only for testing purpose
-- To re-enable it rename the file V<version>__<migration-name>.sql instead of __V<version>__<migration-name>.sql

INSERT INTO "PUBLIC"."DPDS_DATA_PRODUCTS" VALUES
('849aba17-9244-34b0-8de1-315a6bf10e12', NULL, 'sampleDomain', 'urn:dpds:it.quantyca:dataproducts:sampleDomain:tripExecution');

INSERT INTO "PUBLIC"."DPDS_INFO_OWNERS" VALUES
('john.doe.old@company-xyz.com', 'John Doe');

INSERT INTO "PUBLIC"."DPDS_DATA_PRODUCT_VERSIONS" VALUES
('849aba17-9244-34b0-8de1-315a6bf10e12', '0.0.1', NULL, '0.0.1', 'Gestione dei viaggi necessari ad eseguire il trasporto della merce dalla sorgente alla destinazione', 'Trip Execution', 'sampleDomain', 'dataproduct', 'urn:dpds:it.quantyca:dataproducts:sampleDomain:tripExecution', 'tripExecution', '{"dataProductDescriptor":"1.0.0","info":{"entityType":"dataproduct","fullyQualifiedName":"urn:dpds:it.quantyca:dataproducts:sampleDomain:tripExecution","domain":"sampleDomain","name":"tripExecution","version":"1.0.0","displayName":"Trip Execution","description":"Gestione dei viaggi necessari ad eseguire il trasporto della merce dalla sorgente alla destinazione","x-prop":"custom-prop-value","owner":{"id":"john.doe@company-xyz.com","name":"John Doe","x-prop":"custom-prop-value"},"contactPoints":[{"name":"Support Team Mail","description":"The mail address of to the team that give support on this product","channel":"email","address":"trip-execution-support@company-xyz.com","x-prop":"custom-prop-value"},{"name":"Issue Tracker","description":"The address of the issue tracker associated to this product","channel":"web","address":"https://readmine.company-xyz.com/trip-execution","x-prop":"custom-prop-value"}],"id":"849aba17-9244-34b0-8de1-315a6bf10e12"}}', NULL, NULL, 'john.doe.old@company-xyz.com');

