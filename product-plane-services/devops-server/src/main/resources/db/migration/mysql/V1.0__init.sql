-- MySql 8;

CREATE TABLE hibernate_sequence (
    NEXT_VAL BIGINT AUTO_INCREMENT,
    PRIMARY KEY (NEXT_VAL)
);
INSERT INTO hibernate_sequence VALUES (0);


-- ACTIVITIES ============================================


CREATE TABLE ACTIVITIES(
    ID BIGINT NOT NULL AUTO_INCREMENT,
    DATA_PRODUCT_ID VARCHAR(255),
    DATA_PRODUCT_VERSION VARCHAR(255),
    TYPE VARCHAR(125),
    STATUS VARCHAR(125),
    RESULTS VARCHAR(5000),
    ERRORS VARCHAR(5000),

    CREATED_AT TIMESTAMP,
    STARTED_AT TIMESTAMP,
    FINISHED_AT TIMESTAMP
);