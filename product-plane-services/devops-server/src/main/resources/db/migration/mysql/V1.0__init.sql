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
    RESULTS TEXT,
    ERRORS TEXT,

    CREATED_AT TIMESTAMP,
    STARTED_AT TIMESTAMP,
    FINISHED_AT TIMESTAMP,
    PRIMARY KEY (ID)
);

-- TASKS ============================================
CREATE TABLE TASKS(
    ID BIGINT NOT NULL AUTO_INCREMENT,
    ACTIVITY_ID BIGINT,
    EXECUTOR_REF VARCHAR(500),
    TEMPLATE TEXT,
    CONFIGURATIONS TEXT,
    STATUS VARCHAR(125),
    RESULTS TEXT,
    ERRORS TEXT,
    
    CREATED_AT TIMESTAMP,
    STARTED_AT TIMESTAMP,
    FINISHED_AT TIMESTAMP,
    PRIMARY KEY (ID)
);

-- TASKS FKs
ALTER TABLE TASKS ADD CONSTRAINT C_FK1__TASKS FOREIGN KEY(ACTIVITY_ID) REFERENCES ACTIVITIES(ID)  ON DELETE CASCADE;
