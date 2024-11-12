UPDATE ODMPOLICY.POLICIES
SET EVALUATION_EVENT = 'DATA_PRODUCT_VERSION_CREATION'
WHERE EVALUATION_EVENT = 'DATA_PRODUCT_CREATION';

UPDATE ODMPOLICY.POLICIES
SET EVALUATION_EVENT = 'DATA_PRODUCT_VERSION_CREATION'
WHERE EVALUATION_EVENT = 'DATA_PRODUCT_UPDATE';

CREATE TABLE IF NOT EXISTS POLICIES_EVALUATION_EVENTS (
    SEQUENCE_ID BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    POLICY_ID BIGINT REFERENCES POLICIES(ID) ON DELETE CASCADE,
    EVENT VARCHAR(255)
);

INSERT INTO ODMPOLICY.POLICIES_EVALUATION_EVENTS (POLICY_ID, EVENT)
SELECT ID, EVALUATION_EVENT
FROM ODMPOLICY.POLICIES;