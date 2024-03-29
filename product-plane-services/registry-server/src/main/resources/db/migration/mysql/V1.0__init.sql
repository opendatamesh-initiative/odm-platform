-- MySql 8;

CREATE TABLE hibernate_sequence (
    NEXT_VAL BIGINT AUTO_INCREMENT,
    PRIMARY KEY (NEXT_VAL)
);
INSERT INTO hibernate_sequence VALUES (0);


-- DATA_PRODUCTS ============================================

CREATE TABLE DATA_PRODUCTS(
    ID  VARCHAR(255) PRIMARY KEY,
    FQN  VARCHAR(255),
    DOMAIN  VARCHAR(255),
    DESCRIPTION TEXT    
) ENGINE=InnoDB;


-- DATA PPRODUCT > VERSIONS ============================================

CREATE TABLE DP_VERSIONS(  
    DATA_PRODUCT_ID  VARCHAR(255) NOT NULL,
    VERSION_NUMBER  VARCHAR(255) NOT NULL,

    DP_FQN  VARCHAR(255),
    DP_ENTITY_TYPE  VARCHAR(255),
    DP_NAME  VARCHAR(255),
    DP_DISPLAY_NAME  VARCHAR(255),
    DP_DOMAIN  VARCHAR(255),
    
    DESCRIPTION TEXT,
    OWNER_ID  VARCHAR(255) NOT NULL,
    EXTERNAL_DOC_ID BIGINT,

    DPDS_VERSION  VARCHAR(255),
    CONTENT  TEXT,
    
    CREATED_AT TIMESTAMP,
    UPDATED_AT TIMESTAMP
) ENGINE=InnoDB;
ALTER TABLE DP_VERSIONS ADD CONSTRAINT C_PK_DP_VERSIONS PRIMARY KEY (DATA_PRODUCT_ID, VERSION_NUMBER);


-- DATA PPRODUCT > VERSION > INFO_OWNERS

CREATE TABLE DPV_INFO_OWNERS(
    ID  VARCHAR(255) PRIMARY KEY,
    NAME  VARCHAR(255)
) ENGINE=InnoDB;


-- DATA PPRODUCT > VERSION > INFO_CONTACT_POINTS

CREATE TABLE DPV_INFO_CONTACT_POINTS(
    DATA_PRODUCT_ID  VARCHAR(255) NOT NULL,
    VERSION_NUMBER  VARCHAR(255) NOT NULL,
    NAME  VARCHAR(255),
    DESCRIPTION TEXT,
    CHANNEL  VARCHAR(255),
    ADDRESS  VARCHAR(255)
) ENGINE=InnoDB;

-- DATA PPRODUCT > VERSION > DPV_PORTS ============================================

CREATE TABLE DPV_PORTS(
    DATA_PRODUCT_ID  VARCHAR(255),
    DATA_PRODUCT_VERSION  VARCHAR(255),

    ID  VARCHAR(255) PRIMARY KEY,
    FQN  VARCHAR(255),
    ENTITY_TYPE  VARCHAR(255),
    NAME  VARCHAR(255),
    VERSION  VARCHAR(255),

    DISPLAY_NAME  VARCHAR(255),
    DESCRIPTION TEXT,
    
    PROMISES_ID BIGINT,
    EXPECTATIONS_ID BIGINT,
    CONTRACTS_ID BIGINT,
    
    COMPONENT_GROUP  VARCHAR(255),
    EXTERNAL_DOC_ID BIGINT,

    CONTENT  TEXT,
    
    CREATED_AT TIMESTAMP,
    UPDATED_AT TIMESTAMP
) ENGINE=InnoDB;


-- DATA PPRODUCT > VERSION > DPV_PORT_PROMISES

CREATE TABLE DPV_PORT_PROMISES(
    ID BIGINT NOT NULL AUTO_INCREMENT,
    PLATFORM  VARCHAR(255),
    SERVICE_TYPE  VARCHAR(255),
    API_ID BIGINT,
    DEPRECTAION_POLICY_ID BIGINT,
    SLO_ID BIGINT,
    PRIMARY KEY (ID)
) ENGINE=InnoDB;


-- DATA PPRODUCT > VERSION > DPV_PORT_EXPECTATIONS

CREATE TABLE DPV_PORT_EXPECTATIONS(
    ID BIGINT NOT NULL AUTO_INCREMENT,
    AUDIENCE_ID BIGINT,
    USAGE_ID BIGINT,
    PRIMARY KEY (ID)
) ENGINE=InnoDB;


-- DATA PPRODUCT > VERSION > DPV_PORTS

CREATE TABLE DPV_PORT_CONTRACTS(
    ID BIGINT NOT NULL AUTO_INCREMENT,
    BILLING_POLICY_ID BIGINT,
    SLA_ID BIGINT,
    TERMS_AND_CONDITIONS_ID BIGINT,
    PRIMARY KEY (ID)
);

-- DATA PPRODUCT > VERSION > DPV_TASK_INFOS ============================================
CREATE TABLE DPV_TASK_INFOS(
    DATA_PRODUCT_ID VARCHAR(255),
    DATA_PRODUCT_VERSION VARCHAR(255),

    ID BIGINT NOT NULL AUTO_INCREMENT,
    NAME VARCHAR(255),
    STAGE VARCHAR(255),
    EXEC_ORDER INT,
    SERVICE_ID BIGINT,
    TEMPLATE_ID BIGINT,
    CONFIGURATIONS  TEXT,
    CONTENT  TEXT,

    PRIMARY KEY (ID)   
);

-- DATA PPRODUCT > VERSION > DPV_APP_COMPONENTS ============================================

CREATE TABLE DPV_APP_COMPONENTS(
    DATA_PRODUCT_ID  VARCHAR(255),
    DATA_PRODUCT_VERSION  VARCHAR(255),

    ID  VARCHAR(255) PRIMARY KEY,
    FQN  VARCHAR(255),
    ENTITY_TYPE  VARCHAR(255),
    NAME  VARCHAR(255),
    VERSION  VARCHAR(255),
    
    DISPLAY_NAME  VARCHAR(255),
    DESCRIPTION TEXT,
    
    APPLICATION_TYPE  VARCHAR(255),
    PLATFORM  VARCHAR(255),
    
    COMPONENT_GROUP  VARCHAR(255),
    EXTERNAL_DOC_ID BIGINT,
    
    CONTENT  TEXT,
    
    CREATED_AT TIMESTAMP,
    UPDATED_AT TIMESTAMP
) ENGINE=InnoDB;


-- DATA PPRODUCT > VERSION > DPV_APP_COMPONENT_DEPENDENCIES

CREATE TABLE DPV_APP_COMPONENT_DEPENDENCIES(
    COMPONENT_ID  VARCHAR(255) NOT NULL,
    DEPENDS_ON_COMPONENT_ID  VARCHAR(255)
) ENGINE=InnoDB;

-- DATA PPRODUCT > VERSION > DPV_APP_COMPONENT_SINKS

CREATE TABLE DPV_APP_COMPONENT_SINKS(
    ID  VARCHAR(255) NOT NULL,
    SINK_ID  VARCHAR(255)
) ENGINE=InnoDB;

-- DATA PPRODUCT > VERSION > DPV_APP_COMPONENT_SOURCES

CREATE TABLE DPV_APP_COMPONENT_SOURCES(
    ID  VARCHAR(255) NOT NULL,
    SOURCE_ID  VARCHAR(255)
) ENGINE=InnoDB;


-- DATA PPRODUCT > VERSION > DPV_INFRA_COMPONENTS ============================================

CREATE TABLE DPV_INFRA_COMPONENTS(
    DATA_PRODUCT_ID  VARCHAR(255),
    DATA_PRODUCT_VERSION  VARCHAR(255),

    ID  VARCHAR(255) PRIMARY KEY,
    FQN  VARCHAR(255),
    ENTITY_TYPE  VARCHAR(255),
    NAME  VARCHAR(255),
    VERSION  VARCHAR(255),

    DESCRIPTION TEXT,
    DISPLAY_NAME  VARCHAR(255),
    
    INFRASTRUCTURE_TYPE  VARCHAR(255),
    PLATFORM  VARCHAR(255),
        
    COMPONENT_GROUP  VARCHAR(255),
    EXTERNAL_DOC_ID BIGINT,
   
    CONTENT  TEXT,

    CREATED_AT TIMESTAMP,
    UPDATED_AT TIMESTAMP
) ENGINE=InnoDB;

-- DATA PPRODUCT > VERSION > DPV_INFRA_COMPONENT_DEPENDENCIES

CREATE TABLE DPV_INFRA_COMPONENT_DEPENDENCIES(
    COMPONENT_ID  VARCHAR(255) NOT NULL,
    DEPENDS_ON_COMPONENT_ID  VARCHAR(255)
) ENGINE=InnoDB;

-- DATA PPRODUCT > VERSION > TAGS ============================================

CREATE TABLE DPV_DATA_PRODUCT_TAGS(
    DATAPRODUCT_ID  VARCHAR(255) NOT NULL,
    VERSION  VARCHAR(255) NOT NULL,
    TAG_ID  VARCHAR(255)
) ENGINE=InnoDB;

CREATE TABLE DPV_PORT_TAGS(
    ID  VARCHAR(255) NOT NULL,
    TAG_ID  VARCHAR(255)
) ENGINE=InnoDB;

CREATE TABLE DPV_APP_COMPONENT_TAGS(
    ID  VARCHAR(255) NOT NULL,
    TAG_ID  VARCHAR(255)
) ENGINE=InnoDB;

CREATE TABLE DPV_INFRA_COMPONENT_TAGS(
    ID  VARCHAR(255) NOT NULL,
    TAG_ID  VARCHAR(255)
) ENGINE=InnoDB;

-- DATA PPRODUCT > VERSION > REFERENCE & EXTENSIOSN ============================================

-- DATA PPRODUCT > VERSION > DPV_EXTERNAL_RESOURCES

CREATE TABLE DPV_EXTERNAL_RESOURCES(
    ID BIGINT NOT NULL AUTO_INCREMENT,
    DESCRIPTION TEXT,
    MEDIA_TYPE  VARCHAR(255),
    HREF  VARCHAR(255),
    PRIMARY KEY (ID)
) ENGINE=InnoDB;

-- DATA PPRODUCT > VERSION > DPV_REFERENCE_OBJECTS

CREATE TABLE DPV_REFERENCE_OBJECTS(
    ID BIGINT NOT NULL AUTO_INCREMENT,
    DESCRIPTION TEXT,
    MEDIA_TYPE  VARCHAR(255),
    REF  VARCHAR(255),
    ORIGINAL_REF  VARCHAR(255),
    CONTENT  TEXT,
    PRIMARY KEY (ID)
) ENGINE=InnoDB;

-- DATA PPRODUCT > VERSION > DPV_EXTERNAL_RESOURCES

CREATE TABLE DPV_SPEC_EXTENSION_POINTS(
    ID BIGINT NOT NULL AUTO_INCREMENT,
    DESCRIPTION TEXT,
    EXTERNAL_DOC_ID BIGINT,
    PRIMARY KEY (ID)
) ENGINE=InnoDB;

-- DATA PPRODUCT > VERSION > DPV_VARIABLES ============================================

CREATE TABLE DPV_VARIABLES(
    ID BIGINT NOT NULL AUTO_INCREMENT,

    DATA_PRODUCT_ID VARCHAR(255) NOT NULL,
    DATA_PRODUCT_VERSION VARCHAR(255) NOT NULL,

    VARIABLE_NAME VARCHAR(500) NOT NULL,
    VARIABLE_VALUE VARCHAR(255),

    PRIMARY KEY (ID)
) ENGINE=InnoDB;

-- API DEFINITIONS ============================================

CREATE TABLE DPV_API_STD_DEFS(
    INSTANCE_ID BIGINT NOT NULL AUTO_INCREMENT,
    ID VARCHAR(255) NOT NULL,
    FQN VARCHAR(255),
    ENTITY_TYPE VARCHAR(255),
    NAME VARCHAR(255),
    VERSION VARCHAR(255),
    DISPLAY_NAME VARCHAR(255),
    DESCRIPTION TEXT,
    
    SPECIFICATION VARCHAR(255),
    SPECIFICATION_VERSION VARCHAR(255),

    DEF_DESCRIPTION TEXT,
    DEF_MEDIA_TYPE VARCHAR(255),
    DEF_REF VARCHAR(500),
    DEF_ORIGINAL_REF VARCHAR(500),
    
    COMPONENT_GROUP VARCHAR(255),
    EXTERNAL_DOC_ID BIGINT,
    CONTENT TEXT,
    CREATED_AT TIMESTAMP,
    UPDATED_AT TIMESTAMP,

    PRIMARY KEY (INSTANCE_ID)
) ENGINE=InnoDB;

CREATE TABLE APIS(
    ID VARCHAR(255) NOT NULL,
    FQN VARCHAR(255),
    ENTITY_TYPE VARCHAR(255),
    NAME VARCHAR(255),
    VERSION VARCHAR(50),
    DISPLAY_NAME VARCHAR(255),
    DESCRIPTION TEXT,
    SPECIFICATION VARCHAR(255),
    SPECIFICATION_VERSION VARCHAR(255),
    DEFINITION_MEDIA_TYPE VARCHAR(255),
    DEFINITION TEXT,
    CREATED_AT TIMESTAMP,
    UPDATED_AT TIMESTAMP,

    PRIMARY KEY (ID)
) ENGINE=InnoDB;

-- SCHEMAS ============================================

CREATE TABLE DEF_SCHEMAS(
    ID BIGINT NOT NULL AUTO_INCREMENT,
    NAME VARCHAR(255) NOT NULL,
    VERSION VARCHAR(50) NOT NULL,
    MEDIA_TYPE VARCHAR(255),
    CONTENT TEXT,

    PRIMARY KEY (ID)
) ENGINE=InnoDB;

CREATE TABLE REL_APIS_TO_SCHEMAS(
    API_ID  VARCHAR(255) NOT NULL,
    SCHEMA_ID BIGINT NOT NULL,
    OPERATION_ID VARCHAR(255),
    OUTPUT_MEDIA_TYPE VARCHAR(255)
) ENGINE=InnoDB;
ALTER TABLE REL_APIS_TO_SCHEMAS ADD CONSTRAINT REL_APIS_TO_SCHEMAS_PK PRIMARY KEY (API_ID, SCHEMA_ID);


-- TEMPLATES ============================================
CREATE TABLE DPV_TEMPLATE_STD_DEFS(
    INSTANCE_ID BIGINT NOT NULL AUTO_INCREMENT,
    ID VARCHAR(255) NOT NULL,
    FQN VARCHAR(255),
    ENTITY_TYPE VARCHAR(255),
    NAME VARCHAR(255),
    VERSION VARCHAR(255),
    DISPLAY_NAME VARCHAR(255),
    DESCRIPTION TEXT,
    
    SPECIFICATION VARCHAR(255),
    SPECIFICATION_VERSION VARCHAR(255),

    DEF_DESCRIPTION TEXT,
    DEF_MEDIA_TYPE VARCHAR(255),
    DEF_REF VARCHAR(500),
    DEF_ORIGINAL_REF VARCHAR(500),
    
    COMPONENT_GROUP VARCHAR(255),
    EXTERNAL_DOC_ID BIGINT,
    CONTENT TEXT,
    CREATED_AT TIMESTAMP,
    UPDATED_AT TIMESTAMP,

    PRIMARY KEY (INSTANCE_ID)
)ENGINE=InnoDB;

CREATE TABLE TEMPLATES(
    ID VARCHAR(255) NOT NULL,
    FQN VARCHAR(255),
    ENTITY_TYPE VARCHAR(255),
    NAME VARCHAR(255),
    VERSION VARCHAR(50),
    DISPLAY_NAME VARCHAR(255),
    DESCRIPTION TEXT,
    SPECIFICATION VARCHAR(255),
    SPECIFICATION_VERSION VARCHAR(255),
    DEFINITION_MEDIA_TYPE VARCHAR(255),
    DEFINITION TEXT,
    CREATED_AT TIMESTAMP,
    UPDATED_AT TIMESTAMP,

    PRIMARY KEY (ID)
) ENGINE=InnoDB;


-- DOMAINS ============================================

CREATE TABLE DOMAINS(
    DOMAIN_ID VARCHAR(255) NOT NULL,
    NAME VARCHAR(255),
    FQN VARCHAR(255),
    ENTITY_TYPE VARCHAR(255),
    DISPLAY_NAME VARCHAR(255),
    DESCRIPTION TEXT,
    SUMMARY VARCHAR(255),
    CREATED_AT TIMESTAMP,
    UPDATED_AT TIMESTAMP,

    PRIMARY KEY (DOMAIN_ID)
) ENGINE=InnoDB;


-- DP_VERSIONS FKs

ALTER TABLE DP_VERSIONS ADD CONSTRAINT C_FK1_DP_VERSIONS FOREIGN KEY(OWNER_ID) REFERENCES DPV_INFO_OWNERS(ID) ON DELETE CASCADE;
ALTER TABLE DP_VERSIONS ADD CONSTRAINT C_FK2_DP_VERSIONS FOREIGN KEY(EXTERNAL_DOC_ID) REFERENCES DPV_EXTERNAL_RESOURCES(ID)  ON DELETE CASCADE;

ALTER TABLE DPV_INFO_CONTACT_POINTS ADD CONSTRAINT C_FK_DPV_INFO_CONTACT_POINTS FOREIGN KEY(DATA_PRODUCT_ID, VERSION_NUMBER) REFERENCES DP_VERSIONS(DATA_PRODUCT_ID, VERSION_NUMBER)  ON DELETE CASCADE;
ALTER TABLE DPV_DATA_PRODUCT_TAGS ADD CONSTRAINT C_FK_DATA_PRODUCT_TAGS FOREIGN KEY(DATAPRODUCT_ID, VERSION) REFERENCES DP_VERSIONS(DATA_PRODUCT_ID, VERSION_NUMBER) ON DELETE CASCADE;

-- DPV_PORTS FKs
ALTER TABLE DPV_PORTS ADD CONSTRAINT C_FK1_DPV_PORTS FOREIGN KEY(DATA_PRODUCT_ID, DATA_PRODUCT_VERSION) REFERENCES DP_VERSIONS(DATA_PRODUCT_ID, VERSION_NUMBER) ON DELETE CASCADE;
ALTER TABLE DPV_PORTS ADD CONSTRAINT C_FK2_DPV_PORTS FOREIGN KEY(PROMISES_ID) REFERENCES DPV_PORT_PROMISES(ID) ON DELETE CASCADE;
ALTER TABLE DPV_PORTS ADD CONSTRAINT C_FK3_DPV_PORTS FOREIGN KEY(EXPECTATIONS_ID) REFERENCES DPV_PORT_EXPECTATIONS(ID) ON DELETE CASCADE;
ALTER TABLE DPV_PORTS ADD CONSTRAINT C_FK4_DPV_PORTS FOREIGN KEY(CONTRACTS_ID) REFERENCES DPV_PORT_CONTRACTS(ID) ON DELETE CASCADE;
ALTER TABLE DPV_PORTS ADD CONSTRAINT C_FK5_DPV_PORTS FOREIGN KEY(EXTERNAL_DOC_ID) REFERENCES DPV_EXTERNAL_RESOURCES(ID) ON DELETE CASCADE;

ALTER TABLE DPV_PORT_PROMISES ADD CONSTRAINT C_FK1_DPV_PORT_PROMISES FOREIGN KEY(SLO_ID) REFERENCES DPV_SPEC_EXTENSION_POINTS(ID) ON DELETE CASCADE;
ALTER TABLE DPV_PORT_PROMISES ADD CONSTRAINT C_FK2_DPV_PORT_PROMISES FOREIGN KEY(DEPRECTAION_POLICY_ID) REFERENCES DPV_SPEC_EXTENSION_POINTS(ID) ON DELETE CASCADE;
ALTER TABLE DPV_PORT_PROMISES ADD CONSTRAINT C_FK3_DPV_PORT_PROMISES FOREIGN KEY(API_ID) REFERENCES DPV_API_STD_DEFS(INSTANCE_ID) ON DELETE CASCADE;

ALTER TABLE DPV_PORT_EXPECTATIONS ADD CONSTRAINT C_FK1_DPV_PORT_EXPECTATIONS FOREIGN KEY(AUDIENCE_ID) REFERENCES DPV_SPEC_EXTENSION_POINTS(ID) ON DELETE CASCADE;
ALTER TABLE DPV_PORT_EXPECTATIONS ADD CONSTRAINT C_FK2_DPV_PORT_EXPECTATIONS FOREIGN KEY(USAGE_ID) REFERENCES DPV_SPEC_EXTENSION_POINTS(ID) ON DELETE CASCADE;
ALTER TABLE DPV_PORT_CONTRACTS ADD CONSTRAINT C_FK1_DPV_PORT_CONTRACTS FOREIGN KEY(SLA_ID) REFERENCES DPV_SPEC_EXTENSION_POINTS(ID) ON DELETE CASCADE;
ALTER TABLE DPV_PORT_CONTRACTS ADD CONSTRAINT C_FK2_DPV_PORT_CONTRACTS FOREIGN KEY(BILLING_POLICY_ID) REFERENCES DPV_SPEC_EXTENSION_POINTS(ID) ON DELETE CASCADE;
ALTER TABLE DPV_PORT_CONTRACTS ADD CONSTRAINT C_FK3_DPV_PORT_CONTRACTS FOREIGN KEY(TERMS_AND_CONDITIONS_ID) REFERENCES DPV_SPEC_EXTENSION_POINTS(ID) ON DELETE CASCADE;

ALTER TABLE DPV_PORT_TAGS ADD CONSTRAINT C_FK_DPV_PORT_TAGS FOREIGN KEY(ID) REFERENCES DPV_PORTS(ID) ON DELETE CASCADE;

-- DPV_TASK_INFOS

ALTER TABLE DPV_TASK_INFOS ADD CONSTRAINT C_FK3_DPV_TASK_INFOS FOREIGN KEY(DATA_PRODUCT_ID, DATA_PRODUCT_VERSION) REFERENCES DP_VERSIONS(DATA_PRODUCT_ID, VERSION_NUMBER) ON DELETE CASCADE;
ALTER TABLE DPV_TASK_INFOS ADD CONSTRAINT C_FK1_DPV_TASK_INFOS FOREIGN KEY(SERVICE_ID) REFERENCES DPV_EXTERNAL_RESOURCES(ID) ON DELETE CASCADE;
ALTER TABLE DPV_TASK_INFOS ADD CONSTRAINT C_FK2_DPV_TASK_INFOS FOREIGN KEY(TEMPLATE_ID) REFERENCES DPV_TEMPLATE_STD_DEFS(INSTANCE_ID) ON DELETE CASCADE;


-- DPV_APP_COMPONENTS FKs

ALTER TABLE DPV_APP_COMPONENTS ADD CONSTRAINT C_FK1_DPV_APP_COMPONENTS FOREIGN KEY(DATA_PRODUCT_ID, DATA_PRODUCT_VERSION) REFERENCES DP_VERSIONS(DATA_PRODUCT_ID, VERSION_NUMBER) ON DELETE CASCADE;
ALTER TABLE DPV_APP_COMPONENTS ADD CONSTRAINT C_FK4_DPV_APP_COMPONENTS FOREIGN KEY(EXTERNAL_DOC_ID) REFERENCES DPV_EXTERNAL_RESOURCES(ID) ON DELETE CASCADE;

ALTER TABLE DPV_APP_COMPONENT_DEPENDENCIES ADD CONSTRAINT C_FK_DPV_APP_COMPONENT_DEPENDENCIES FOREIGN KEY(COMPONENT_ID) REFERENCES DPV_APP_COMPONENTS(ID) ON DELETE CASCADE;
ALTER TABLE DPV_APP_COMPONENT_SINKS ADD CONSTRAINT C_FK_DPV_APP_COMPONENT_SINKS FOREIGN KEY(ID) REFERENCES DPV_APP_COMPONENTS(ID) ON DELETE CASCADE;
ALTER TABLE DPV_APP_COMPONENT_SOURCES ADD CONSTRAINT C_FK_DPV_APP_COMPONENT_SOURCES FOREIGN KEY(ID) REFERENCES DPV_APP_COMPONENTS(ID) ON DELETE CASCADE;

ALTER TABLE DPV_APP_COMPONENT_TAGS ADD CONSTRAINT FKSQ1G1IFWKXGV440A0HOKR1LEU FOREIGN KEY(ID) REFERENCES DPV_APP_COMPONENTS(ID) ON DELETE CASCADE;

-- DPV_INFRA_COMPONENTS FKs
ALTER TABLE DPV_INFRA_COMPONENTS ADD CONSTRAINT C_FK1_DPV_INFRA_COMPONENTS FOREIGN KEY(DATA_PRODUCT_ID, DATA_PRODUCT_VERSION) REFERENCES DP_VERSIONS(DATA_PRODUCT_ID, VERSION_NUMBER) ON DELETE CASCADE;
ALTER TABLE DPV_INFRA_COMPONENTS ADD CONSTRAINT C_FK3_DPV_INFRA_COMPONENTS FOREIGN KEY(EXTERNAL_DOC_ID) REFERENCES DPV_EXTERNAL_RESOURCES(ID) ON DELETE CASCADE;

ALTER TABLE DPV_INFRA_COMPONENT_DEPENDENCIES ADD CONSTRAINT C_FK_DPV_INFRA_COMPONENT_DEPENDENCIES FOREIGN KEY(COMPONENT_ID) REFERENCES DPV_INFRA_COMPONENTS(ID) ON DELETE CASCADE;
ALTER TABLE DPV_INFRA_COMPONENT_TAGS ADD CONSTRAINT FKOGCUHXRVBR8TRJSNF1SCFJK0D FOREIGN KEY(ID) REFERENCES DPV_INFRA_COMPONENTS(ID) ON DELETE CASCADE;

-- REFS & EXTENSIONS FKs
ALTER TABLE DPV_SPEC_EXTENSION_POINTS ADD CONSTRAINT C_FK_DPV_SPEC_EXTENSION_POINTS FOREIGN KEY(EXTERNAL_DOC_ID) REFERENCES DPV_EXTERNAL_RESOURCES(ID)  ON DELETE CASCADE;

-- DPV_VARIABLES FKs
ALTER TABLE DPV_VARIABLES ADD CONSTRAINT C_FK1_DPV_VARIABLES FOREIGN KEY(DATA_PRODUCT_ID, DATA_PRODUCT_VERSION) REFERENCES DP_VERSIONS(DATA_PRODUCT_ID, VERSION_NUMBER) ON DELETE CASCADE;


