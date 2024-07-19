-- MySql 8;

CREATE TABLE hibernate_sequence (
    NEXT_VAL BIGINT AUTO_INCREMENT,
    PRIMARY KEY (NEXT_VAL)
);
INSERT INTO hibernate_sequence VALUES (0);


-- PARAMS ============================================
CREATE TABLE PARAMS(
    ID BIGINT NOT NULL AUTO_INCREMENT,
    PARAM_NAME VARCHAR(255),
    PARAM_VALUE VARCHAR(255),
    SECRET TINYINT(1) DEFAULT 0,
    DISPLAY_NAME VARCHAR(255),
    DESCRIPTION TEXT,
    CREATED_AT TIMESTAMP,
    UPDATED_AT TIMESTAMP,

    PRIMARY KEY (ID)
);