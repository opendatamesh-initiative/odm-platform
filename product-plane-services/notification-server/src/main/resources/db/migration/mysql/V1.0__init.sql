-- MySql 8;

CREATE TABLE hibernate_sequence (
    NEXT_VAL BIGINT AUTO_INCREMENT,
    PRIMARY KEY (NEXT_VAL)
);
INSERT INTO hibernate_sequence VALUES (0);

-- ENTITIES ============================================================================================================

-- OBSERVERS -----------------------------------------------------------------------------------------------------------
CREATE TABLE OBSERVERS(
    ID BIGINT NOT NULL AUTO_INCREMENT,
    NAME VARCHAR(255),
    DISPLAY_NAME VARCHAR(255),
    OBSERVER_URL VARCHAR(1000),
    CREATED_AT TIMESTAMP,
    UPDATED_AT TIMESTAMP,
    PRIMARY KEY (ID)
) ENGINE=InnoDB;


-- NOTIFICATION --------------------------------------------------------------------------------------------------------
CREATE TABLE NOTIFICATIONS(
    ID BIGINT NOT NULL AUTO_INCREMENT,
    EVENT_ID BIGINT,
    EVENT_TYPE VARCHAR(255),
    EVENT_ENTITY_ID VARCHAR(255),
    EVENT_BEFORE_STATE BLOB,
    EVENT_AFTER_STATE BLOB,
    EVENT_TIME TIMESTAMP,
    STATUS VARCHAR(255),
    PROCESSING_OUTPUT BLOB,
    RECEIVED_AT TIMESTAMP,
    PROCESSED_AT TIMESTAMP,
    PRIMARY KEY (ID)
) ENGINE=InnoDB;