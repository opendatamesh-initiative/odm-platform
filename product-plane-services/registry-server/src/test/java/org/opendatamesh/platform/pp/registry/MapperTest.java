package org.opendatamesh.platform.pp.registry;

import java.net.URI;

import org.opendatamesh.platform.core.dpds.DPDSParser;
import org.opendatamesh.platform.core.dpds.DataProductVersionSource;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;

public class MapperTest {
    
    public static void main(String[] args) throws Exception {
        String ROOT_DOC_LOACAL_FILEPATH = "/home/andrea.gioia/Sviluppi/quantyca/open-data-mesh/github/odm-platform-pp-services/product-plane-services/registry-server/src/test/resources/test/dataproduct-descriptor/dp1-v1.json";
        // DataProductVersionSource descriptorSource = new
        // DataProductVersionSource(Files.readString(Paths.get(ROOT_DOC_LOACAL_FILEPATH)));

        URI ROOT_DOC_REMOTE_URI = new URI(
                "https://raw.githubusercontent.com/opendatamesh-initiative/odm-specification-dpdescriptor/main/examples/tripexecution/data-product-descriptor.json#pippo?pippo=/xxx");
        DataProductVersionSource dataProductVersionSource = new DataProductVersionSource(ROOT_DOC_REMOTE_URI);

        DPDSParser parser = new DPDSParser(dataProductVersionSource,
                "http://localhost:80/");

        DataProductVersionDPDS dataProductVerion = parser.parse(true);
    }
}
