package org.opendatamesh.platform.pp.registry.server;

import org.junit.jupiter.api.Test;
import org.opendatamesh.dpds.parser.IdentifierStrategy;
import org.opendatamesh.dpds.parser.IdentifierStrategyFactory;
import org.opendatamesh.platform.pp.registry.api.resources.DataProductResource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.annotation.DirtiesContext.MethodMode;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class DataProductIT extends ODMRegistryIT {

    // ======================================================================================
    // CREATE Data Product
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateDataProductWithAllProperties() {

        DataProductResource dataProductRes = null, createdDataProductRes = null;

        dataProductRes =  resourceBuilder.buildTestDataProduct();
        createdDataProductRes = createDataProduct(dataProductRes);
       
        assertThat(createdDataProductRes).isNotNull();
        
        assertThat(createdDataProductRes.getId()).isEqualTo(dataProductRes.getId());
        assertThat(createdDataProductRes.getFullyQualifiedName()).isEqualTo(dataProductRes.getFullyQualifiedName());
        assertThat(createdDataProductRes.getDomain()).isEqualTo(dataProductRes.getDomain());
        assertThat(createdDataProductRes.getDescription()).isEqualTo(dataProductRes.getDescription());
        
        assertThat(createdDataProductRes).isEqualTo(dataProductRes);

        try {
            DataProductResource readDataProductRes = registryClient.readDataProduct(createdDataProductRes.getId());
            assertThat(readDataProductRes).isNotNull();
            assertThat(readDataProductRes).isEqualTo(createdDataProductRes);

            DataProductResource[] readDataProductsRes = registryClient.readAllDataProducts();
            assertThat(readDataProductsRes).isNotNull();
            assertThat(readDataProductsRes.length).isEqualTo(1);
            assertThat(readDataProductsRes[0]).isEqualTo(createdDataProductRes);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to read data product: " + t.getMessage());
        }
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateDataProductWithOnlyFqnProperty() {

        DataProductResource dataProductRes = null, createdDataProductRes = null;

        dataProductRes = resourceBuilder.buildDataProduct(
            null, 
            "urn:org.opendatamesh:dataproducts:testProduct", 
            null, null);
        createdDataProductRes = createDataProduct(dataProductRes);

        assertThat(createdDataProductRes).isNotNull();
        dataProductRes.setId(IdentifierStrategyFactory.getDefault("org.opendatamesh").getId(dataProductRes.getFullyQualifiedName()));
        assertThat(createdDataProductRes.getId()).isEqualTo(dataProductRes.getId());
        assertThat(createdDataProductRes.getFullyQualifiedName()).isEqualTo(dataProductRes.getFullyQualifiedName());
        assertThat(createdDataProductRes.getDomain()).isNull();
        assertThat(createdDataProductRes.getDescription()).isNull();
        
        assertThat(createdDataProductRes).isEqualTo(dataProductRes);

        try {
            DataProductResource readDataProductRes = registryClient.readDataProduct(createdDataProductRes.getId());
            assertThat(readDataProductRes).isNotNull();
            assertThat(readDataProductRes).isEqualTo(createdDataProductRes);

            DataProductResource[] readDataProductsRes = registryClient.readAllDataProducts();
            assertThat(readDataProductsRes).isNotNull();
            assertThat(readDataProductsRes.length).isEqualTo(1);
            assertThat(readDataProductsRes[0]).isEqualTo(createdDataProductRes);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to read data product: " + t.getMessage());
        }
     }

    // ======================================================================================
    // READ Data Product
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testReadDataProduct() {

        DataProductResource dataProduct1Res, dataProduct2Res, dataProduct3Res;
    
        dataProduct1Res = resourceBuilder.buildDataProduct("urn:org.opendatamesh:dataproducts:prod-1", "marketing", "marketing product");
        dataProduct1Res = createDataProduct(dataProduct1Res);

        dataProduct2Res = resourceBuilder.buildDataProduct("urn:org.opendatamesh:dataproducts:prod-2", "sales", "sales product");
        dataProduct2Res = createDataProduct(dataProduct2Res);
        
        dataProduct3Res = resourceBuilder.buildDataProduct("urn:org.opendatamesh:dataproducts:prod-3", "hr", "hr product");
        dataProduct3Res = createDataProduct(dataProduct3Res);

        DataProductResource readDataProduct1Res = null, readDataProduct2Res = null, readDataProduct3Res = null;
        try {
            readDataProduct1Res = registryClient.readDataProduct(dataProduct1Res.getId());
            readDataProduct2Res = registryClient.readDataProduct(dataProduct2Res.getId());
            readDataProduct3Res = registryClient.readDataProduct(dataProduct3Res.getId());
        }  catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to read data products: " + t.getMessage());
        }
        
        assertThat(readDataProduct1Res).isNotNull();
        assertThat(readDataProduct1Res).isEqualTo(dataProduct1Res);
        assertThat(readDataProduct2Res).isEqualTo(dataProduct2Res);
        assertThat(readDataProduct3Res).isEqualTo(dataProduct3Res);
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testReadDataProducts() {

        DataProductResource dataProduct1Res, dataProduct2Res, dataProduct3Res;
    
        dataProduct1Res = resourceBuilder.buildDataProduct("urn:org.opendatamesh:dataproducts:prod-1", "marketing", "marketing product");
        dataProduct1Res = createDataProduct(dataProduct1Res);

        dataProduct2Res = resourceBuilder.buildDataProduct("urn:org.opendatamesh:dataproducts:prod-2", "sales", "sales product");
        dataProduct2Res = createDataProduct(dataProduct2Res);
        
        dataProduct3Res = resourceBuilder.buildDataProduct("urn:org.opendatamesh:dataproducts:prod-3", "hr", "hr product");
        dataProduct3Res = createDataProduct(dataProduct3Res);

        DataProductResource[] readDataProductsRes = null;
        try {
            readDataProductsRes = registryClient.readAllDataProducts();
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to read data products: " + t.getMessage());
        }
        assertThat(readDataProductsRes).isNotNull();
        assertThat(readDataProductsRes.length).isEqualTo(3);
       
        Map<String,DataProductResource> readDataProductsResMap = new HashMap<String,DataProductResource>();
        readDataProductsResMap.put(readDataProductsRes[0].getId(), readDataProductsRes[0]);
        readDataProductsResMap.put(readDataProductsRes[1].getId(), readDataProductsRes[1]);
        readDataProductsResMap.put(readDataProductsRes[2].getId(), readDataProductsRes[2]);
        assertThat(readDataProductsResMap.get(dataProduct1Res.getId())).isEqualTo(dataProduct1Res);
        assertThat(readDataProductsResMap.get(dataProduct2Res.getId())).isEqualTo(dataProduct2Res);
        assertThat(readDataProductsResMap.get(dataProduct3Res.getId())).isEqualTo(dataProduct3Res);
    
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testSearchDataProductsWithAllFilters() {
        DataProductResource dataProduct1Res, dataProduct2Res, dataProduct3Res;
    
        dataProduct1Res = resourceBuilder.buildDataProduct("urn:org.opendatamesh:dataproducts:prod-1", "marketing", "marketing product");
        dataProduct1Res = createDataProduct(dataProduct1Res);

        dataProduct2Res = resourceBuilder.buildDataProduct("urn:org.opendatamesh:dataproducts:prod-2", "sales", "sales product");
        dataProduct2Res = createDataProduct(dataProduct2Res);
        
        dataProduct3Res = resourceBuilder.buildDataProduct("urn:org.opendatamesh:dataproducts:prod-3", "hr", "hr product");
        dataProduct3Res = createDataProduct(dataProduct3Res);

        DataProductResource[] searchedDataProducts1Res = null,  searchedDataProducts2Res = null,  searchedDataProducts3Res = null;
        try {
            searchedDataProducts1Res = registryClient.searchDataProducts(dataProduct1Res.getFullyQualifiedName(), dataProduct1Res.getDomain());
            searchedDataProducts2Res = registryClient.searchDataProducts(dataProduct2Res.getFullyQualifiedName(), dataProduct2Res.getDomain());
            searchedDataProducts3Res = registryClient.searchDataProducts(dataProduct3Res.getFullyQualifiedName(), dataProduct3Res.getDomain());
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to read data products: " + t.getMessage());
        }

        assertThat(searchedDataProducts1Res).isNotNull();
        assertThat(searchedDataProducts1Res.length).isEqualTo(1); 
        assertThat(searchedDataProducts1Res[0]).isEqualTo(dataProduct1Res);

        assertThat(searchedDataProducts2Res).isNotNull();
        assertThat(searchedDataProducts2Res.length).isEqualTo(1); 
        assertThat(searchedDataProducts2Res[0]).isEqualTo(dataProduct2Res);

        assertThat(searchedDataProducts3Res).isNotNull();
        assertThat(searchedDataProducts3Res.length).isEqualTo(1); 
        assertThat(searchedDataProducts3Res[0]).isEqualTo(dataProduct3Res);
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testSearchDataProductsByFqn() {
        DataProductResource dataProduct1Res, dataProduct2Res, dataProduct3Res;
    
        dataProduct1Res = resourceBuilder.buildDataProduct("urn:org.opendatamesh:dataproducts:prod-1", "marketing", "marketing product");
        dataProduct1Res = createDataProduct(dataProduct1Res);

        dataProduct2Res = resourceBuilder.buildDataProduct("urn:org.opendatamesh:dataproducts:prod-2", "sales", "sales product 3");
        dataProduct2Res = createDataProduct(dataProduct2Res);
        
        dataProduct3Res = resourceBuilder.buildDataProduct("urn:org.opendatamesh:dataproducts:prod-3", "sales", "sales product 3");
        dataProduct3Res = createDataProduct(dataProduct3Res);

        DataProductResource[] searchedDataProducts1Res = null,  searchedDataProducts2Res = null,  searchedDataProducts3Res = null;
        try {
            searchedDataProducts1Res = registryClient.searchDataProducts(dataProduct1Res.getFullyQualifiedName(), null);
            searchedDataProducts2Res = registryClient.searchDataProducts(dataProduct2Res.getFullyQualifiedName(), null);
            searchedDataProducts3Res = registryClient.searchDataProducts(dataProduct3Res.getFullyQualifiedName(), null);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to read data products: " + t.getMessage());
        }

        assertThat(searchedDataProducts1Res).isNotNull();
        assertThat(searchedDataProducts1Res.length).isEqualTo(1); 
        assertThat(searchedDataProducts1Res[0]).isEqualTo(dataProduct1Res);

        assertThat(searchedDataProducts2Res).isNotNull();
        assertThat(searchedDataProducts2Res.length).isEqualTo(1); 
        assertThat(searchedDataProducts2Res[0]).isEqualTo(dataProduct2Res);

        assertThat(searchedDataProducts3Res).isNotNull();
        assertThat(searchedDataProducts3Res.length).isEqualTo(1); 
        assertThat(searchedDataProducts3Res[0]).isEqualTo(dataProduct3Res);
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testSearchDataProductsByDomain() {
        DataProductResource dataProduct1Res, dataProduct2Res, dataProduct3Res;
    
        dataProduct1Res = resourceBuilder.buildDataProduct("urn:org.opendatamesh:dataproducts:prod-1", "marketing", "marketing product");
        dataProduct1Res = createDataProduct(dataProduct1Res);

        dataProduct2Res = resourceBuilder.buildDataProduct("urn:org.opendatamesh:dataproducts:prod-2", "sales", "sales product 3");
        dataProduct2Res = createDataProduct(dataProduct2Res);
        
        dataProduct3Res = resourceBuilder.buildDataProduct("urn:org.opendatamesh:dataproducts:prod-3", "sales", "sales product 3");
        dataProduct3Res = createDataProduct(dataProduct3Res);

        DataProductResource[] searchedMarketingDataProductsRes = null,  searchedSalesDataProductsRes = null;
        try {
            searchedMarketingDataProductsRes = registryClient.searchDataProducts(null, "marketing");
            searchedSalesDataProductsRes = registryClient.searchDataProducts(null, "sales");
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to read data products: " + t.getMessage());
        }

        assertThat(searchedMarketingDataProductsRes).isNotNull();
        assertThat(searchedMarketingDataProductsRes.length).isEqualTo(1); 
        assertThat(searchedMarketingDataProductsRes[0]).isEqualTo(dataProduct1Res);

        assertThat(searchedSalesDataProductsRes).isNotNull();
        assertThat(searchedSalesDataProductsRes.length).isEqualTo(2); 
        Map<String,DataProductResource> readDataProductsResMap = new HashMap<String,DataProductResource>();
        readDataProductsResMap.put(searchedSalesDataProductsRes[0].getId(), searchedSalesDataProductsRes[0]);
        readDataProductsResMap.put(searchedSalesDataProductsRes[1].getId(), searchedSalesDataProductsRes[1]);
        assertThat(readDataProductsResMap.get(dataProduct2Res.getId())).isEqualTo(dataProduct2Res);
        assertThat(readDataProductsResMap.get(dataProduct3Res.getId())).isEqualTo(dataProduct3Res);
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testSearchNotExixtingDataProducts() {
        DataProductResource dataProduct1Res, dataProduct2Res, dataProduct3Res;
    
        dataProduct1Res = resourceBuilder.buildDataProduct("urn:org.opendatamesh:dataproducts:prod-1", "marketing", "marketing product");
        dataProduct1Res = createDataProduct(dataProduct1Res);

        dataProduct2Res = resourceBuilder.buildDataProduct("urn:org.opendatamesh:dataproducts:prod-2", "sales", "sales product 3");
        dataProduct2Res = createDataProduct(dataProduct2Res);
        
        dataProduct3Res = resourceBuilder.buildDataProduct("urn:org.opendatamesh:dataproducts:prod-3", "sales", "sales product 3");
        dataProduct3Res = createDataProduct(dataProduct3Res);

        DataProductResource[] searchedHrDataProductsRes = null,  searchedWrongFqnDataProductsRes = null;
        try {
            searchedWrongFqnDataProductsRes = registryClient.searchDataProducts("wrong-fqn", "marketing");
            searchedHrDataProductsRes = registryClient.searchDataProducts(dataProduct2Res.getFullyQualifiedName(), "hr");
           
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to read data products: " + t.getMessage());
        }

        assertThat(searchedWrongFqnDataProductsRes).isNotNull();
        assertThat(searchedWrongFqnDataProductsRes.length).isEqualTo(0); 

        assertThat(searchedHrDataProductsRes).isNotNull();
        assertThat(searchedHrDataProductsRes.length).isEqualTo(0); 
        
    }

    // ======================================================================================
    // UPDATE Data Product
    // ======================================================================================
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDataProductUpdateWithAllProperties() {

        DataProductResource createdDataProductRes = null;
        createdDataProductRes = resourceBuilder.buildDataProduct(
            "a9228eb7-3179-3628-ae64-aa5dbb1fcb28", 
            "urn:org.opendatamesh:dataproducts:testProduct", 
            "Test Domain",
            "This is test product #1");
        createdDataProductRes = createDataProduct(createdDataProductRes);

        DataProductResource updatedDataProductRes = null;
        createdDataProductRes.setDescription("This is the updated version of test product #1");
        createdDataProductRes.setDomain("Updated Domain");
        try {
            updatedDataProductRes = registryClient.updateDataProduct(createdDataProductRes);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to read data products: " + t.getMessage());
        }

        assertThat(updatedDataProductRes).isNotNull();
        assertThat(updatedDataProductRes.getId()).isEqualTo(createdDataProductRes.getId());
        assertThat(updatedDataProductRes.getFullyQualifiedName()).isEqualTo(createdDataProductRes.getFullyQualifiedName());
        assertThat(updatedDataProductRes.getDomain()).isEqualTo(createdDataProductRes.getDomain());
        assertThat(updatedDataProductRes.getDescription()).isEqualTo(createdDataProductRes.getDescription());
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDataProductUpdateWithMissingFqn() {

        DataProductResource createdDataProductRes = null;
        createdDataProductRes = resourceBuilder.buildDataProduct(
            "a9228eb7-3179-3628-ae64-aa5dbb1fcb28", 
            "urn:org.opendatamesh:dataproducts:testProduct", 
            "Test Domain",
            "This is test product #1");
        createdDataProductRes = createDataProduct(createdDataProductRes);

        DataProductResource updatedDataProductRes = null;
        createdDataProductRes.setFullyQualifiedName(null);
        createdDataProductRes.setDescription("This is the updated version of test product #1");
        createdDataProductRes.setDomain("Updated Domain");
        try {
            updatedDataProductRes = registryClient.updateDataProduct(createdDataProductRes);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to read data products: " + t.getMessage());
        }

        assertThat(updatedDataProductRes).isNotNull();
        assertThat(updatedDataProductRes.getId()).isEqualTo(createdDataProductRes.getId());
        assertThat(updatedDataProductRes.getFullyQualifiedName()).isEqualTo("urn:org.opendatamesh:dataproducts:testProduct");
        assertThat(updatedDataProductRes.getDomain()).isEqualTo(createdDataProductRes.getDomain());
        assertThat(updatedDataProductRes.getDescription()).isEqualTo(createdDataProductRes.getDescription());
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDataProductUpdateWithMissingId() {

        DataProductResource createdDataProductRes = null;
        createdDataProductRes = resourceBuilder.buildDataProduct(
            "a9228eb7-3179-3628-ae64-aa5dbb1fcb28", 
            "urn:org.opendatamesh:dataproducts:testProduct", 
            "Test Domain",
            "This is test product #1");
        createdDataProductRes = createDataProduct(createdDataProductRes);

        DataProductResource updatedDataProductRes = null;
        createdDataProductRes.setId(null);
        createdDataProductRes.setDescription("This is the updated version of test product #1");
        createdDataProductRes.setDomain("Updated Domain");
        try {
            updatedDataProductRes = registryClient.updateDataProduct(createdDataProductRes);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to read data products: " + t.getMessage());
        }

        assertThat(updatedDataProductRes).isNotNull();
        assertThat(updatedDataProductRes.getId()).isEqualTo("a9228eb7-3179-3628-ae64-aa5dbb1fcb28");
        assertThat(updatedDataProductRes.getFullyQualifiedName()).isEqualTo(createdDataProductRes.getFullyQualifiedName());
        assertThat(updatedDataProductRes.getDomain()).isEqualTo(createdDataProductRes.getDomain());
        assertThat(updatedDataProductRes.getDescription()).isEqualTo(createdDataProductRes.getDescription());
    }

    // ----------------------------------------
    // DELETE Data product
    // ----------------------------------------
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDataProductDelete() {

        DataProductResource createdDataProductRes = null;
        
        createdDataProductRes = resourceBuilder.buildDataProduct(
            "a9228eb7-3179-3628-ae64-aa5dbb1fcb28", 
            "urn:org.opendatamesh:dataproducts:testProduct", 
            "Test Domain",
            "This is test product #1");
        createdDataProductRes = createDataProduct(createdDataProductRes);

        DataProductResource delatedDataProductRes = null;
        try {
            delatedDataProductRes = registryClient.deleteDataProduct(createdDataProductRes.getId());
        } catch (Throwable t) {
            fail("Impossible to delete data product");
        }
        assertThat(delatedDataProductRes).isNotNull();
        assertThat(delatedDataProductRes).isEqualTo(createdDataProductRes);

        DataProductResource[] dataProductsRes = null;
        try {
            dataProductsRes = registryClient.readAllDataProducts();
        } catch (Throwable t) {
            fail("Impossible to delete data product");
        }
        assertThat(dataProductsRes).isNotNull();
        assertThat(dataProductsRes.length).isEqualTo(0);
    }

}