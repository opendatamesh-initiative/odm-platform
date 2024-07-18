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


-- EVENTS --------------------------------------------------------------------------------------------------------------

CREATE TABLE EVENTS(
    ID BIGINT NOT NULL AUTO_INCREMENT,
    TYPE VARCHAR(255),
    ENTITY_ID VARCHAR(255),
    BEFORE_STATE TEXT,
    AFTER_STATE TEXT,
    TIME TIMESTAMP,
    PRIMARY KEY (ID)
);


-- NOTIFICATION --------------------------------------------------------------------------------------------------------
CREATE TABLE NOTIFICATIONS(
    ID BIGINT NOT NULL AUTO_INCREMENT,
    OBSERVER_ID BIGINT,
    EVENT_ID BIGINT,
    STATUS VARCHAR(255),
    PROCESSING_OUTPUT BLOB,
    RECEIVED_AT TIMESTAMP,
    PROCESSED_AT TIMESTAMP,
    PRIMARY KEY (ID)
) ENGINE=InnoDB;


-- FOREING KEYS ========================================================================================================

-- NOTIFICATIONS FKs
ALTER TABLE NOTIFICATIONS ADD CONSTRAINT C_FK1__NOTIFICATIONS FOREIGN KEY(OBSERVER_ID) REFERENCES OBSERVERS(ID) ON DELETE NO ACTION;
ALTER TABLE NOTIFICATIONS ADD CONSTRAINT C_FK2__NOTIFICATIONS FOREIGN KEY(EVENT_ID) REFERENCES EVENTS(ID) ON DELETE NO ACTION;