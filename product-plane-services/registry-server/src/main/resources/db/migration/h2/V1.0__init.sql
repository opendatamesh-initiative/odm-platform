-- H2 2.1.214;

CREATE USER IF NOT EXISTS "SA" SALT '582a7e34517160c6' HASH '6b2d59d21c92ab85fb76c10fe4a0873b2745eeeae07aa2001a8c42a5577baf72' ADMIN;
CREATE SCHEMA IF NOT EXISTS "ODMREGISTRY";
CREATE SEQUENCE "ODMREGISTRY"."HIBERNATE_SEQUENCE" START WITH 1;

-- DATA_PRODUCTS

CREATE MEMORY TABLE "ODMREGISTRY"."DATA_PRODUCTS"(
    "ID" CHARACTER VARYING(255) NOT NULL,
    "FQN" CHARACTER VARYING(255),
    "DOMAIN" CHARACTER VARYING(255),
    "DESCRIPTION" CHARACTER VARYING(255)
   
);
ALTER TABLE "ODMREGISTRY"."DATA_PRODUCTS" ADD CONSTRAINT "ODMREGISTRY"."C_PK_DATA_PRODUCTS" PRIMARY KEY("ID");


-- DATA PPRODUCT > VERSIONS

CREATE MEMORY TABLE "ODMREGISTRY"."DP_VERSIONS"(
    "DATA_PRODUCT_ID" CHARACTER VARYING(255) NOT NULL,
    "VERSION_NUMBER" CHARACTER VARYING(255) NOT NULL,

    "DP_FQN" CHARACTER VARYING(255),
    "DP_ENTITY_TYPE" CHARACTER VARYING(255),
    "DP_NAME" CHARACTER VARYING(255),
    "DP_DISPLAY_NAME" CHARACTER VARYING(255),
    "DP_DOMAIN" CHARACTER VARYING(255),

    "DESCRIPTION" CHARACTER VARYING(255),
    "OWNER_ID" CHARACTER VARYING(255) NOT NULL,
    "EXTERNAL_DOC_ID" BIGINT,
    
    "DPDS_VERSION" CHARACTER VARYING(255),
    "CONTENT" CHARACTER VARYING,    
    
    "CREATED_AT" TIMESTAMP,
    "UPDATED_AT" TIMESTAMP
);
ALTER TABLE "ODMREGISTRY"."DP_VERSIONS" ADD CONSTRAINT "ODMREGISTRY"."C_PK_DP_VERSIONS" PRIMARY KEY("DATA_PRODUCT_ID", "VERSION_NUMBER");


-- DATA PPRODUCT > VERSION >  INFO_OWNERS

CREATE MEMORY TABLE "ODMREGISTRY"."DPV_INFO_OWNERS"(
    "ID" CHARACTER VARYING(255) NOT NULL,
    "NAME" CHARACTER VARYING(255)
);
ALTER TABLE "ODMREGISTRY"."DPV_INFO_OWNERS" ADD CONSTRAINT "ODMREGISTRY"."C_PK_DPV_INFO_OWNERS" PRIMARY KEY("ID");


-- DATA PPRODUCT > VERSION > INFO_CONTACT_POINTS

CREATE MEMORY TABLE "ODMREGISTRY"."DPV_INFO_CONTACT_POINTS"(
    "DATA_PRODUCT_ID" CHARACTER VARYING(255) NOT NULL,
    "VERSION_NUMBER" CHARACTER VARYING(255) NOT NULL,
    "NAME" CHARACTER VARYING(255),
    "DESCRIPTION" CHARACTER VARYING(255),
    "CHANNEL" CHARACTER VARYING(255),
    "ADDRESS" CHARACTER VARYING(255)
);

-- DATA PPRODUCT > VERSION > DPV_PORTS

CREATE MEMORY TABLE "ODMREGISTRY"."DPV_PORTS"(
    "DATA_PRODUCT_ID" CHARACTER VARYING(255),
    "DATA_PRODUCT_VERSION" CHARACTER VARYING(255),

    "ID" CHARACTER VARYING(255) NOT NULL,
    "FQN" CHARACTER VARYING(255),
    "ENTITY_TYPE" CHARACTER VARYING(255),
    "NAME" CHARACTER VARYING(255),
    "VERSION" CHARACTER VARYING(255),

    "DISPLAY_NAME" CHARACTER VARYING(255),
    "DESCRIPTION" CHARACTER VARYING(255),
  
    "PROMISES_ID" BIGINT,
    "EXPECTATIONS_ID" BIGINT,
    "CONTRACTS_ID" BIGINT,
    
    "COMPONENT_GROUP" CHARACTER VARYING(255),
    "EXTERNAL_DOC_ID" BIGINT, 
    
    "CONTENT" CHARACTER VARYING,

    "CREATED_AT" TIMESTAMP,
    "UPDATED_AT" TIMESTAMP
);
ALTER TABLE "ODMREGISTRY"."DPV_PORTS" ADD CONSTRAINT "ODMREGISTRY"."C_PK_DPV_PORTS" PRIMARY KEY("ID");


-- DATA PPRODUCT > VERSION > DPV_PORT_PROMISES

CREATE MEMORY TABLE "ODMREGISTRY"."DPV_PORT_PROMISES"(
    "ID" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL,
    "PLATFORM" CHARACTER VARYING(255),
    "SERVICE_TYPE" CHARACTER VARYING(255),
    "API_ID" BIGINT,
    "DEPRECTAION_POLICY_ID" BIGINT,
    "SLO_ID" BIGINT
);
ALTER TABLE "ODMREGISTRY"."DPV_PORT_PROMISES" ADD CONSTRAINT "ODMREGISTRY"."C_PK_DPV_PORT_PROMISES" PRIMARY KEY("ID");


-- DATA PPRODUCT > VERSION > DPV_PORT_EXPECTATIONS

CREATE MEMORY TABLE "ODMREGISTRY"."DPV_PORT_EXPECTATIONS"(
    "ID" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL,
    "AUDIENCE_ID" BIGINT,
    "USAGE_ID" BIGINT
);
ALTER TABLE "ODMREGISTRY"."DPV_PORT_EXPECTATIONS" ADD CONSTRAINT "ODMREGISTRY"."C_PK_DPV_PORT_EXPECTATIONS" PRIMARY KEY("ID");


-- DATA PPRODUCT > VERSION > DPV_PORT_CONTRACTS

CREATE MEMORY TABLE "ODMREGISTRY"."DPV_PORT_CONTRACTS"(
    "ID" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL,
    "BILLING_POLICY_ID" BIGINT,
    "SLA_ID" BIGINT,
    "TERMS_AND_CONDITIONS_ID" BIGINT
);
ALTER TABLE "ODMREGISTRY"."DPV_PORT_CONTRACTS" ADD CONSTRAINT "ODMREGISTRY"."CONSTRAINT_AE" PRIMARY KEY("ID");



-- DATA PPRODUCT > VERSION > DPV_APP_COMPONENTS ============================================

CREATE MEMORY TABLE "ODMREGISTRY"."DPV_APP_COMPONENTS"(
    "DATA_PRODUCT_ID" CHARACTER VARYING(255),
    "DATA_PRODUCT_VERSION" CHARACTER VARYING(255),

    "ID" CHARACTER VARYING(255) NOT NULL,
    "FQN" CHARACTER VARYING(255),
    "ENTITY_TYPE" CHARACTER VARYING(255),
    "NAME" CHARACTER VARYING(255),
    "VERSION" CHARACTER VARYING(255),
    
    "DISPLAY_NAME" CHARACTER VARYING(255),
    "DESCRIPTION" CHARACTER VARYING(255),
    
    "PLATFORM" CHARACTER VARYING(255),
    "APPLICATION_TYPE" CHARACTER VARYING(255),

    "BUILD_INFO_ID" BIGINT,
    "DEPLOY_INFO_ID" BIGINT,
    
    "COMPONENT_GROUP" CHARACTER VARYING(255),
    "EXTERNAL_DOC_ID" BIGINT,
   
    "CONTENT" CHARACTER VARYING,
     
    "CREATED_AT" TIMESTAMP,
    "UPDATED_AT" TIMESTAMP
);
ALTER TABLE "ODMREGISTRY"."DPV_APP_COMPONENTS" ADD CONSTRAINT "ODMREGISTRY"."C_PK_DPV_APP_COMPONENTS" PRIMARY KEY("ID");

-- DATA PPRODUCT > VERSION > DPV_APP_COMPONENT_DEPENDENCIES

CREATE MEMORY TABLE "ODMREGISTRY"."DPV_APP_COMPONENT_DEPENDENCIES"(
    "COMPONENT_ID" CHARACTER VARYING(255) NOT NULL,
    "DEPENDS_ON_COMPONENT_ID" CHARACTER VARYING(255)
);

-- DATA PPRODUCT > VERSION > DPV_APP_COMPONENT_SINKS 

CREATE MEMORY TABLE "ODMREGISTRY"."DPV_APP_COMPONENT_SINKS"(
    "ID" CHARACTER VARYING(255) NOT NULL,
    "SINK_ID" CHARACTER VARYING(255)
);

-- DATA PPRODUCT > VERSION > DPV_APP_COMPONENT_SOURCES

CREATE MEMORY TABLE "ODMREGISTRY"."DPV_APP_COMPONENT_SOURCES"(
    "ID" CHARACTER VARYING(255) NOT NULL,
    "SOURCE_ID" CHARACTER VARYING(255)
);

-- DATA PPRODUCT > VERSION > DPV_APP_COMPONENT_BUILD_INFOS

CREATE MEMORY TABLE "ODMREGISTRY"."DPV_APP_COMPONENT_BUILD_INFOS"(
    "ID" BIGINT NOT NULL,
    "SERVICE_ID" BIGINT,
    "TEMPLATE_ID" BIGINT,
    "CONFIGURATIONS" CHARACTER VARYING(5000)
);
ALTER TABLE "ODMREGISTRY"."DPV_APP_COMPONENT_BUILD_INFOS" ADD CONSTRAINT "ODMREGISTRY"."C_PK_DPV_APP_COMPONENT_BUILD_INFOS" PRIMARY KEY("ID");

-- DATA PPRODUCT > VERSION > DPV_APP_COMPONENT_DEPLOY_INFOS

CREATE MEMORY TABLE "ODMREGISTRY"."DPV_APP_COMPONENT_DEPLOY_INFOS"(
    "ID" BIGINT NOT NULL,
    "SERVICE_ID" BIGINT,
    "TEMPLATE_ID" BIGINT,
    "CONFIGURATIONS" CHARACTER VARYING(5000)
);
ALTER TABLE "ODMREGISTRY"."DPV_APP_COMPONENT_DEPLOY_INFOS" ADD CONSTRAINT "ODMREGISTRY"."C_PK_DPV_APP_COMPONENT_DEPLOY_INFOS" PRIMARY KEY("ID");



-- DATA PPRODUCT > VERSION > DPV_INFRA_COMPONENTS  ============================================

CREATE MEMORY TABLE "ODMREGISTRY"."DPV_INFRA_COMPONENTS"(
    "DATA_PRODUCT_ID" CHARACTER VARYING(255),    
    "DATA_PRODUCT_VERSION" CHARACTER VARYING(255),

    "ID" CHARACTER VARYING(255) NOT NULL,
    "FQN" CHARACTER VARYING(255),
    "ENTITY_TYPE" CHARACTER VARYING(255),
    "NAME" CHARACTER VARYING(255),
    "VERSION" CHARACTER VARYING(255),
    
    "DISPLAY_NAME" CHARACTER VARYING(255),
    "DESCRIPTION" CHARACTER VARYING(255),
    
    "PLATFORM" CHARACTER VARYING(255),
    "INFRASTRUCTURE_TYPE" CHARACTER VARYING(255),
    
    "PROVISION_INFO_ID" BIGINT,
    
    "COMPONENT_GROUP" CHARACTER VARYING(255),
    "EXTERNAL_DOC_ID" BIGINT,

    "CONTENT" CHARACTER VARYING,
    
    "CREATED_AT" TIMESTAMP,
    "UPDATED_AT" TIMESTAMP
);
ALTER TABLE "ODMREGISTRY"."DPV_INFRA_COMPONENTS" ADD CONSTRAINT "ODMREGISTRY"."C_PK_DPV_INFRA_COMPONENTS" PRIMARY KEY("ID");


-- DATA PPRODUCT > VERSION > DPV_INFRA_COMPONENT_DEPENDENCIES

CREATE MEMORY TABLE "ODMREGISTRY"."DPV_INFRA_COMPONENT_DEPENDENCIES"(
    "COMPONENT_ID" CHARACTER VARYING(255) NOT NULL,
    "DEPENDS_ON_COMPONENT_ID" CHARACTER VARYING(255)
);

-- DATA PPRODUCT > VERSION > DPV_INFRA_PROVISION_INFOS

CREATE MEMORY TABLE "ODMREGISTRY"."DPV_INFRA_PROVISION_INFOS"(
    "ID" BIGINT NOT NULL,
    "SERVICE_ID" BIGINT,
    "TEMPLATE_ID" BIGINT,
    "CONFIGURATIONS" CHARACTER VARYING(5000)
);
ALTER TABLE "ODMREGISTRY"."DPV_INFRA_PROVISION_INFOS" ADD CONSTRAINT "ODMREGISTRY"."C_PK_DPV_INFRA_PROVISION_INFOS" PRIMARY KEY("ID");


-- DATA PPRODUCT > VERSION > TAGS ============================================

CREATE MEMORY TABLE "ODMREGISTRY"."DPV_DATA_PRODUCT_TAGS"(
    "DATAPRODUCT_ID" CHARACTER VARYING(255) NOT NULL,
    "VERSION" CHARACTER VARYING(255) NOT NULL,
    "TAG_ID" CHARACTER VARYING(255)
);

CREATE MEMORY TABLE "ODMREGISTRY"."DPV_PORT_TAGS"(
    "ID" CHARACTER VARYING(255) NOT NULL,
    "TAG_ID" CHARACTER VARYING(255)
);

CREATE MEMORY TABLE "ODMREGISTRY"."DPV_APP_COMPONENT_TAGS"(
    "ID" CHARACTER VARYING(255) NOT NULL,
    "TAG_ID" CHARACTER VARYING(255)
);

CREATE MEMORY TABLE "ODMREGISTRY"."DPV_INFRA_COMPONENT_TAGS"(
    "ID" CHARACTER VARYING(255) NOT NULL,
    "TAG_ID" CHARACTER VARYING(255)
);

-- DATA PPRODUCT > VERSION > REFERENCE & EXTENSIOSN ============================================


-- DATA PPRODUCT > VERSION > DPV_EXTERNAL_RESOURCES

CREATE MEMORY TABLE "ODMREGISTRY"."DPV_EXTERNAL_RESOURCES"(
    "ID" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL,
    "DESCRIPTION" CHARACTER VARYING(255),
    "MEDIA_TYPE" CHARACTER VARYING(255),
    "HREF" CHARACTER VARYING(255) 
);
ALTER TABLE "ODMREGISTRY"."DPV_EXTERNAL_RESOURCES" ADD CONSTRAINT "C_PK_DPV_EXTERNAL_RESOURCES" PRIMARY KEY("ID");

-- DATA PPRODUCT > VERSION > DPV_REFERENCE_OBJECTS

CREATE MEMORY TABLE "ODMREGISTRY"."DPV_REFERENCE_OBJECTS"(
    "ID" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL,
    "DESCRIPTION" CHARACTER VARYING(255),
    "MEDIA_TYPE" CHARACTER VARYING(255),
    "REF" CHARACTER VARYING(255),
    "ORIGINAL_REF" CHARACTER VARYING(255),
    "CONTENT" CHARACTER VARYING
);
ALTER TABLE "ODMREGISTRY"."DPV_REFERENCE_OBJECTS" ADD CONSTRAINT "ODMREGISTRY"."C_PK_DPV_REFERENCE_OBJECTS" PRIMARY KEY("ID");

-- DATA PPRODUCT > VERSION > DPV_SPEC_EXTENSION_POINTS

CREATE MEMORY TABLE "ODMREGISTRY"."DPV_SPEC_EXTENSION_POINTS"(
    "ID" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL,
    "DESCRIPTION" CHARACTER VARYING(255),
    "EXTERNAL_DOC_ID" BIGINT
);
ALTER TABLE "ODMREGISTRY"."DPV_SPEC_EXTENSION_POINTS" ADD CONSTRAINT "ODMREGISTRY"."C_PK_DPV_SPEC_EXTENSION_POINTS" PRIMARY KEY("ID");


-- API DEFINITIONS ============================================


CREATE MEMORY TABLE "ODMREGISTRY"."DEF_APIS"(
    "ID" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL,
    "NAME" CHARACTER VARYING(255),
    "VERSION" CHARACTER VARYING(50),
    "STATUS" CHARACTER VARYING(125),
    "DISPLAY_NAME" CHARACTER VARYING(255),
    "DESCRIPTION" CHARACTER VARYING(255),
    "TYPE" CHARACTER VARYING(255),
    "SPECIFICATION" CHARACTER VARYING(255),
    "SPECIFICATION_VERSION" CHARACTER VARYING(255),
    "CONTENT_MEDIA_TYPE" CHARACTER VARYING(255),
    "CONTENT" CHARACTER VARYING
);
ALTER TABLE "ODMREGISTRY"."DEF_APIS" ADD CONSTRAINT "ODMREGISTRY"."C_PK_DEF_APIS" PRIMARY KEY("ID");

-- SCHEMAS ============================================

CREATE MEMORY TABLE "ODMREGISTRY"."DEF_SCHEMAS"(
    "ID" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL,
    "NAME" CHARACTER VARYING(255),
    "VERSION" CHARACTER VARYING(50),
    "MEDIA_TYPE" CHARACTER VARYING(255),
    "CONTENT" CHARACTER VARYING
);
ALTER TABLE "ODMREGISTRY"."DEF_SCHEMAS" ADD CONSTRAINT "ODMREGISTRY"."C_PK_DEF_SCHEMAS" PRIMARY KEY("ID");

CREATE MEMORY TABLE "ODMREGISTRY"."REL_APIS_TO_SCHEMAS"(
    "API_ID" BIGINT NOT NULL,
    "SCHEMA_ID" BIGINT NOT NULL,
    "OPERATION_ID" CHARACTER VARYING(255),
    "OUTPUT_MEDIA_TYPE" CHARACTER VARYING(255)
);
ALTER TABLE "ODMREGISTRY"."REL_APIS_TO_SCHEMAS" ADD CONSTRAINT "ODMREGISTRY"."C_PK_REL_APIS_TO_SCHEMAS" PRIMARY KEY("API_ID", "SCHEMA_ID");


-- TEMPLATES ============================================

CREATE MEMORY TABLE "ODMREGISTRY"."DEF_TEMPLATES"(
    "ID" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL,
    "NAME" CHARACTER VARYING(255),
    "VERSION" CHARACTER VARYING(50),
    "STATUS" CHARACTER VARYING(125),
    "DISPLAY_NAME" CHARACTER VARYING(255),
    "DESCRIPTION" CHARACTER VARYING(255),
    "TYPE" CHARACTER VARYING(255),
    "SPECIFICATION" CHARACTER VARYING(255),
    "SPECIFICATION_VERSION" CHARACTER VARYING(255),
    "CONTENT_MEDIA_TYPE" CHARACTER VARYING(255),
    "CONTENT" CHARACTER VARYING
);
ALTER TABLE "ODMREGISTRY"."DEF_TEMPLATES" ADD CONSTRAINT "ODMREGISTRY"."C_PK_DEF_TEMPLATES" PRIMARY KEY("ID");



-- DP_VERSIONS FKs

ALTER TABLE "ODMREGISTRY"."DP_VERSIONS" ADD CONSTRAINT "ODMREGISTRY"."C_FK1__DP_VERSIONS" FOREIGN KEY("OWNER_ID") REFERENCES "ODMREGISTRY"."DPV_INFO_OWNERS"("ID") NOCHECK;
ALTER TABLE "ODMREGISTRY"."DP_VERSIONS" ADD CONSTRAINT "ODMREGISTRY"."C_FK2_DP_VERSIONS" FOREIGN KEY("EXTERNAL_DOC_ID") REFERENCES "ODMREGISTRY"."DPV_EXTERNAL_RESOURCES"("ID") NOCHECK;

ALTER TABLE "ODMREGISTRY"."DPV_INFO_CONTACT_POINTS" ADD CONSTRAINT "ODMREGISTRY"."C_FK_DPV_INFO_CONTACT_POINTS" FOREIGN KEY("DATA_PRODUCT_ID", "VERSION_NUMBER") REFERENCES "ODMREGISTRY"."DP_VERSIONS"("DATA_PRODUCT_ID", "VERSION_NUMBER") NOCHECK;
ALTER TABLE "ODMREGISTRY"."DPV_DATA_PRODUCT_TAGS" ADD CONSTRAINT "ODMREGISTRY"."C_FK_DATA_PRODUCT_TAGS" FOREIGN KEY("DATAPRODUCT_ID", "VERSION") REFERENCES "ODMREGISTRY"."DP_VERSIONS"("DATA_PRODUCT_ID", "VERSION_NUMBER") NOCHECK;

-- DPV_PORTS FKs

ALTER TABLE "ODMREGISTRY"."DPV_PORTS" ADD CONSTRAINT "ODMREGISTRY"."C_FK1_DPV_PORTS" FOREIGN KEY("DATA_PRODUCT_ID", "DATA_PRODUCT_VERSION") REFERENCES "ODMREGISTRY"."DP_VERSIONS"("DATA_PRODUCT_ID", "VERSION_NUMBER") NOCHECK;
ALTER TABLE "ODMREGISTRY"."DPV_PORTS" ADD CONSTRAINT "ODMREGISTRY"."C_FK2_DPV_PORTS" FOREIGN KEY("PROMISES_ID") REFERENCES "ODMREGISTRY"."DPV_PORT_PROMISES"("ID") NOCHECK;
ALTER TABLE "ODMREGISTRY"."DPV_PORTS" ADD CONSTRAINT "ODMREGISTRY"."C_FK3_DPV_PORTS" FOREIGN KEY("EXPECTATIONS_ID") REFERENCES "ODMREGISTRY"."DPV_PORT_EXPECTATIONS"("ID") NOCHECK;
ALTER TABLE "ODMREGISTRY"."DPV_PORTS" ADD CONSTRAINT "ODMREGISTRY"."C_FK4_DPV_PORTS" FOREIGN KEY("CONTRACTS_ID") REFERENCES "ODMREGISTRY"."DPV_PORT_CONTRACTS"("ID") NOCHECK;
ALTER TABLE "ODMREGISTRY"."DPV_PORTS" ADD CONSTRAINT "ODMREGISTRY"."C_FK5_DPV_PORTS" FOREIGN KEY("EXTERNAL_DOC_ID") REFERENCES "ODMREGISTRY"."DPV_EXTERNAL_RESOURCES"("ID") NOCHECK;

ALTER TABLE "ODMREGISTRY"."DPV_PORT_PROMISES" ADD CONSTRAINT "ODMREGISTRY"."C_FK1_DPV_PORT_PROMISES" FOREIGN KEY("SLO_ID") REFERENCES "ODMREGISTRY"."DPV_SPEC_EXTENSION_POINTS"("ID") NOCHECK;
ALTER TABLE "ODMREGISTRY"."DPV_PORT_PROMISES" ADD CONSTRAINT "ODMREGISTRY"."C_FK2_DPV_PORT_PROMISES" FOREIGN KEY("DEPRECTAION_POLICY_ID") REFERENCES "ODMREGISTRY"."DPV_SPEC_EXTENSION_POINTS"("ID") NOCHECK;
ALTER TABLE "ODMREGISTRY"."DPV_PORT_PROMISES" ADD CONSTRAINT "ODMREGISTRY"."C_FK3_DPV_PORT_PROMISES" FOREIGN KEY("API_ID") REFERENCES "ODMREGISTRY"."DEF_APIS"("ID") NOCHECK;
ALTER TABLE "ODMREGISTRY"."DPV_PORT_EXPECTATIONS" ADD CONSTRAINT "ODMREGISTRY"."C_FK1_DPV_PORT_EXPECTATIONS" FOREIGN KEY("AUDIENCE_ID") REFERENCES "ODMREGISTRY"."DPV_SPEC_EXTENSION_POINTS"("ID") NOCHECK;
ALTER TABLE "ODMREGISTRY"."DPV_PORT_EXPECTATIONS" ADD CONSTRAINT "ODMREGISTRY"."C_FK2_DPV_PORT_EXPECTATIONS" FOREIGN KEY("USAGE_ID") REFERENCES "ODMREGISTRY"."DPV_SPEC_EXTENSION_POINTS"("ID") NOCHECK;
ALTER TABLE "ODMREGISTRY"."DPV_PORT_CONTRACTS" ADD CONSTRAINT "ODMREGISTRY"."C_FK1_DPV_PORT_CONTRACTS" FOREIGN KEY("SLA_ID") REFERENCES "ODMREGISTRY"."DPV_SPEC_EXTENSION_POINTS"("ID") NOCHECK;
ALTER TABLE "ODMREGISTRY"."DPV_PORT_CONTRACTS" ADD CONSTRAINT "ODMREGISTRY"."C_FK2_DPV_PORT_CONTRACTS" FOREIGN KEY("BILLING_POLICY_ID") REFERENCES "ODMREGISTRY"."DPV_SPEC_EXTENSION_POINTS"("ID") NOCHECK;
ALTER TABLE "ODMREGISTRY"."DPV_PORT_CONTRACTS" ADD CONSTRAINT "ODMREGISTRY"."C_FK3_DPV_PORT_CONTRACTS" FOREIGN KEY("TERMS_AND_CONDITIONS_ID") REFERENCES "ODMREGISTRY"."DPV_SPEC_EXTENSION_POINTS"("ID") NOCHECK;

ALTER TABLE "ODMREGISTRY"."DPV_PORT_TAGS" ADD CONSTRAINT "ODMREGISTRY"."FKSXD77OQ8WIO381WHOJWICLXLG" FOREIGN KEY("ID") REFERENCES "ODMREGISTRY"."DPV_PORTS"("ID") NOCHECK;


-- DPV_APP_COMPONENTS FKs

ALTER TABLE "ODMREGISTRY"."DPV_APP_COMPONENTS" ADD CONSTRAINT "ODMREGISTRY"."C_FK1_DPV_APP_COMPONENTS" FOREIGN KEY("DATA_PRODUCT_ID", "DATA_PRODUCT_VERSION") REFERENCES "ODMREGISTRY"."DP_VERSIONS"("DATA_PRODUCT_ID", "VERSION_NUMBER") NOCHECK;
ALTER TABLE "ODMREGISTRY"."DPV_APP_COMPONENTS" ADD CONSTRAINT "ODMREGISTRY"."C_FK2_DPV_APP_COMPONENTS" FOREIGN KEY("BUILD_INFO_ID") REFERENCES "ODMREGISTRY"."DPV_APP_COMPONENT_BUILD_INFOS"("ID") NOCHECK;
ALTER TABLE "ODMREGISTRY"."DPV_APP_COMPONENTS" ADD CONSTRAINT "ODMREGISTRY"."C_FK3_DPV_APP_COMPONENTS" FOREIGN KEY("DEPLOY_INFO_ID") REFERENCES "ODMREGISTRY"."DPV_APP_COMPONENT_DEPLOY_INFOS"("ID") NOCHECK;
ALTER TABLE "ODMREGISTRY"."DPV_APP_COMPONENTS" ADD CONSTRAINT "ODMREGISTRY"."C_FK4_DPV_APP_COMPONENTS" FOREIGN KEY("EXTERNAL_DOC_ID") REFERENCES "ODMREGISTRY"."DPV_EXTERNAL_RESOURCES"("ID") NOCHECK;

ALTER TABLE "ODMREGISTRY"."DPV_APP_COMPONENT_BUILD_INFOS" ADD CONSTRAINT "ODMREGISTRY"."C_FK1_DPV_APP_COMPONENT_BUILD_INFOS" FOREIGN KEY("SERVICE_ID") REFERENCES "ODMREGISTRY"."DPV_EXTERNAL_RESOURCES"("ID") NOCHECK;
ALTER TABLE "ODMREGISTRY"."DPV_APP_COMPONENT_BUILD_INFOS" ADD CONSTRAINT "ODMREGISTRY"."C_FK2_DPV_APP_COMPONENT_BUILD_INFOS" FOREIGN KEY("TEMPLATE_ID") REFERENCES "ODMREGISTRY"."DEF_TEMPLATES"("ID") NOCHECK;

ALTER TABLE "ODMREGISTRY"."DPV_APP_COMPONENT_DEPLOY_INFOS" ADD CONSTRAINT "ODMREGISTRY"."C_FK1_DPV_APP_COMPONENT_DEPLOY_INFOS" FOREIGN KEY("SERVICE_ID") REFERENCES "ODMREGISTRY"."DPV_EXTERNAL_RESOURCES"("ID") NOCHECK;
ALTER TABLE "ODMREGISTRY"."DPV_APP_COMPONENT_DEPLOY_INFOS" ADD CONSTRAINT "ODMREGISTRY"."C_FK2_DPV_APP_COMPONENT_DEPLOY_INFOS" FOREIGN KEY("TEMPLATE_ID") REFERENCES "ODMREGISTRY"."DEF_TEMPLATES"("ID") NOCHECK;

ALTER  TABLE"ODMREGISTRY"."DPV_APP_COMPONENT_TAGS" ADD CONSTRAINT "ODMREGISTRY"."FKSQ1G1IFWKXGV440A0HOKR1LEU" FOREIGN KEY("ID") REFERENCES "ODMREGISTRY"."DPV_APP_COMPONENTS"("ID") NOCHECK;
ALTER TABLE "ODMREGISTRY"."DPV_APP_COMPONENT_DEPENDENCIES" ADD CONSTRAINT "ODMREGISTRY"."C_FK_DPV_APP_COMPONENT_DEPENDENCIES" FOREIGN KEY("COMPONENT_ID") REFERENCES "ODMREGISTRY"."DPV_APP_COMPONENTS"("ID") NOCHECK;
ALTER TABLE "ODMREGISTRY"."DPV_APP_COMPONENT_SINKS" ADD CONSTRAINT "ODMREGISTRY"."C_FK_DPV_APP_COMPONENT_SINKS" FOREIGN KEY("ID") REFERENCES "ODMREGISTRY"."DPV_APP_COMPONENTS"("ID") NOCHECK;
ALTER TABLE "ODMREGISTRY"."DPV_APP_COMPONENT_SOURCES" ADD CONSTRAINT "ODMREGISTRY"."C_FK_DPV_APP_COMPONENT_SOURCES" FOREIGN KEY("ID") REFERENCES "ODMREGISTRY"."DPV_APP_COMPONENTS"("ID") NOCHECK;

-- DPDS_INFRA_COMPONENTS FKs

ALTER TABLE "ODMREGISTRY"."DPV_INFRA_COMPONENTS" ADD CONSTRAINT "ODMREGISTRY"."C_FK1_DPV_INFRA_COMPONENTS" FOREIGN KEY("DATA_PRODUCT_ID", "DATA_PRODUCT_VERSION") REFERENCES "ODMREGISTRY"."DP_VERSIONS"("DATA_PRODUCT_ID", "VERSION_NUMBER") NOCHECK;
ALTER TABLE "ODMREGISTRY"."DPV_INFRA_COMPONENTS" ADD CONSTRAINT "ODMREGISTRY"."C_FK2_DPV_INFRA_COMPONENTS" FOREIGN KEY("PROVISION_INFO_ID") REFERENCES "ODMREGISTRY"."DPV_INFRA_PROVISION_INFOS"("ID") NOCHECK;
ALTER TABLE "ODMREGISTRY"."DPV_INFRA_COMPONENTS" ADD CONSTRAINT "ODMREGISTRY"."C_FK3_DPV_INFRA_COMPONENTS" FOREIGN KEY("EXTERNAL_DOC_ID") REFERENCES "ODMREGISTRY"."DPV_EXTERNAL_RESOURCES"("ID") NOCHECK;

ALTER TABLE "ODMREGISTRY"."DPV_INFRA_PROVISION_INFOS" ADD CONSTRAINT "ODMREGISTRY"."C_FK1_DPV_INFRA_PROVISION_INFOS" FOREIGN KEY("SERVICE_ID") REFERENCES "ODMREGISTRY"."DPV_EXTERNAL_RESOURCES"("ID") NOCHECK;
ALTER TABLE "ODMREGISTRY"."DPV_INFRA_PROVISION_INFOS" ADD CONSTRAINT "ODMREGISTRY"."C_FK2_DPV_INFRA_PROVISION_INFOS" FOREIGN KEY("TEMPLATE_ID") REFERENCES "ODMREGISTRY"."DEF_TEMPLATES"("ID") NOCHECK;

ALTER TABLE "ODMREGISTRY"."DPV_INFRA_COMPONENT_TAGS" ADD CONSTRAINT "ODMREGISTRY"."FKOGCUHXRVBR8TRJSNF1SCFJK0D" FOREIGN KEY("ID") REFERENCES "ODMREGISTRY"."DPV_INFRA_COMPONENTS"("ID") NOCHECK;
ALTER TABLE "ODMREGISTRY"."DPV_INFRA_COMPONENT_DEPENDENCIES" ADD CONSTRAINT "ODMREGISTRY"."C_FK_DPV_INFRA_COMPONENT_DEPENDENCIES" FOREIGN KEY("COMPONENT_ID") REFERENCES "ODMREGISTRY"."DPV_INFRA_COMPONENTS"("ID") NOCHECK;

-- REFS & EXTENSIONS FKs
ALTER TABLE "ODMREGISTRY"."DPV_SPEC_EXTENSION_POINTS" ADD CONSTRAINT "ODMREGISTRY"."C_FK_DPV_SPEC_EXTENSION_POINTS" FOREIGN KEY("EXTERNAL_DOC_ID") REFERENCES "ODMREGISTRY"."DPV_EXTERNAL_RESOURCES"("ID") NOCHECK;

