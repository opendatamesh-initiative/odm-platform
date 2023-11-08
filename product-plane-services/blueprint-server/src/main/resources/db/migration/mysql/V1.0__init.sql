-- MySql 8;

CREATE TABLE hibernate_sequence (
    NEXT_VAL BIGINT AUTO_INCREMENT,
    PRIMARY KEY (NEXT_VAL)
);
INSERT INTO hibernate_sequence VALUES (0);


-- BLUEPRINTS ============================================
CREATE TABLE BLUEPRINTS(
    ID BIGINT NOT NULL AUTO_INCREMENT,
    NAME VARCHAR(255),
    VERSION VARCHAR(32),
    DISPLAY_NAME VARCHAR(255),
    DESCRIPTION TEXT,
    REPOSITORY_PROVIDER VARCHAR(32),
    REPOSITORY_URL VARCHAR(1000),
    BLUEPRINT_DIR VARCHAR(255),
    ORGANIZATION VARCHAR(255),
    PROJECT_ID VARCHAR(255),
    CREATED_AT TIMESTAMP,
    UPDATED_AT TIMESTAMP,

    PRIMARY KEY (ID)
);