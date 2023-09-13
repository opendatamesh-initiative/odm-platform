package org.opendatamesh.platform.core.dpds.utils;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.opendatamesh.platform.core.dpds.model.ApplicationComponentDPDS;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.dpds.model.EntityTypeDPDS;
import org.opendatamesh.platform.core.dpds.model.ExternalResourceDPDS;
import org.opendatamesh.platform.core.dpds.model.InfoDPDS;
import org.opendatamesh.platform.core.dpds.model.InfrastructuralComponentDPDS;
import org.opendatamesh.platform.core.dpds.model.InterfaceComponentsDPDS;
import org.opendatamesh.platform.core.dpds.model.InternalComponentsDPDS;
import org.opendatamesh.platform.core.dpds.model.LifecycleActivityInfoDPDS;
import org.opendatamesh.platform.core.dpds.model.LifecycleInfoDPDS;
import org.opendatamesh.platform.core.dpds.model.PortDPDS;
import org.opendatamesh.platform.core.dpds.model.PromisesDPDS;
import org.opendatamesh.platform.core.dpds.model.StandardDefinitionDPDS;
import org.opendatamesh.platform.core.dpds.model.definitions.DefinitionReferenceDPDS;


public class DPDCoreObjectChecker implements ResourceObjectChecker {

    @Override
    public void verifyAll(DataProductVersionDPDS descriptor) {
        assertThat(descriptor).isNotNull();
        assertThat(descriptor.getDataProductDescriptor()).isEqualTo("1.0.0");

        verifyCoreInfo(descriptor);
        verifyCoreInterfaces(descriptor);
        verifyCoreApplicationComponents(descriptor);
        verifyCoreInfrastructuralComponents(descriptor);
        verifyLifecycle(descriptor);
    }

     @Override
     public void verifyCoreInfo(DataProductVersionDPDS descriptor) {
        InfoDPDS info = descriptor.getInfo();
        assertThat(info).isNotNull();
        assertThat(info.getDataProductId()).isEqualTo("f350cab5-992b-32f7-9c90-79bca1bf10be");
        assertThat(info.getFullyQualifiedName()).isEqualTo("urn:org.opendatamesh:dataproducts:dpdCore");
        assertThat(info.getEntityType()).isEqualTo(EntityTypeDPDS.DATAPRODUCT.propertyValue());
        assertThat(info.getName()).isEqualTo("dpdCore");
        assertThat(info.getVersionNumber()).isEqualTo("1.0.0");
        assertThat(info.getDomain()).isEqualTo("testDomain");
        assertThat(info.getOwner());
        assertThat(info.getOwner().getId()).isEqualTo("john.doe@company-xyz.com");
        assertThat(info.getOwner().getName()).isEqualTo("John Doe");
    }

    @Override
    public void verifyCoreInterfaces(DataProductVersionDPDS descriptor) {

        InterfaceComponentsDPDS interfaces = descriptor.getInterfaceComponents();
        assertThat(interfaces).isNotNull();

        PortDPDS port = null;
        PromisesDPDS promises = null;

        assertThat(interfaces.getInputPorts()).isNotNull();
        assertThat(interfaces.getInputPorts().size()).isEqualTo(1);
        port = interfaces.getInputPorts().get(0);
        assertThat(port.getId()).isEqualTo("2915c611-317b-3464-b0b8-16569ef5b771");
        assertThat(port.getFullyQualifiedName())
                .isEqualTo("urn:org.opendatamesh:dataproducts:dpdCore:1.0.0:inputports:inputPortA:1.0.0");

        assertThat(port.getEntityType()).isEqualTo(EntityTypeDPDS.INPUTPORT.propertyValue());
        assertThat(port.getName()).isEqualTo("inputPortA");
        assertThat(port.getVersion()).isEqualTo("1.0.0");
        assertThat(port.getDisplayName()).isEqualTo("Input port A");
        assertThat(port.getDescription()).isEqualTo("Input port A of data product");
        assertThat(port.getComponentGroup()).isEqualTo("gruppoA");
        promises = port.getPromises();
        assertThat(promises).isNotNull();
        assertThat(promises.getPlatform()).isEqualTo("platformX");
        assertThat(promises.getServicesType()).isEqualTo("rest-services");
        assertThat(promises.getApi()).isNotNull();
        assertThat(promises.getApi().getName()).isEqualTo("restApi1");
        assertThat(promises.getApi().getVersion()).isEqualTo("1.3.2");
        assertThat(promises.getApi().getDescription()).isEqualTo("Rest input API");
        assertThat(promises.getApi().getSpecification()).isEqualTo("custom-api-spec");
        assertThat(promises.getApi().getSpecificationVersion()).isEqualTo("1.0.0");
        assertThat(promises.getApi().getDefinition()).isNotNull();

        assertThat(interfaces.getOutputPorts()).isNotNull();
        assertThat(interfaces.getOutputPorts().size()).isEqualTo(1);
        port = interfaces.getOutputPorts().get(0);
        assertThat(port.getId()).isEqualTo("7a6d1c54-e402-3a42-91fb-b793fae93153");
        assertThat(port.getFullyQualifiedName())
                .isEqualTo("urn:org.opendatamesh:dataproducts:dpdCore:1.0.0:outputports:outputPortA:1.0.0");
        assertThat(port.getEntityType()).isEqualTo(EntityTypeDPDS.OUTPUTPORT.propertyValue());
        assertThat(port.getName()).isEqualTo("outputPortA");
        assertThat(port.getVersion()).isEqualTo("1.0.0");
        assertThat(port.getDisplayName()).isEqualTo("Output port A");
        assertThat(port.getDescription()).isEqualTo("Output port A of data product");
        assertThat(port.getComponentGroup()).isEqualTo("gruppoA");
        promises = port.getPromises();
        assertThat(promises).isNotNull();
        assertThat(promises.getPlatform()).isEqualTo("platformX");
        assertThat(promises.getServicesType()).isEqualTo("rest-services");
        assertThat(promises.getApi()).isNotNull();
        assertThat(promises.getApi().getName()).isEqualTo("restApi2");
        assertThat(promises.getApi().getVersion()).isEqualTo("1.3.2");
        assertThat(promises.getApi().getDescription()).isEqualTo("Rest output API");
        assertThat(promises.getApi().getSpecification()).isEqualTo("custom-api-spec");
        assertThat(promises.getApi().getSpecificationVersion()).isEqualTo("1.0.0");
        assertThat(promises.getApi().getDefinition()).isNotNull();

    }

    @Override
    public void verifyCoreApplicationComponents(DataProductVersionDPDS descriptor) {
        InternalComponentsDPDS internals = descriptor.getInternalComponents();
        assertThat(internals).isNotNull();
        assertThat(internals.getApplicationComponents()).isNotNull();
        assertThat(internals.getApplicationComponents().size()).isEqualTo(1);

        ApplicationComponentDPDS app = internals.getApplicationComponents().get(0);
        assertThat(app.getId()).isEqualTo("33391f55-2127-391a-80c8-881c95ed7082");
        assertThat(app.getFullyQualifiedName())
                .isEqualTo("urn:org.opendatamesh:dataproducts:dpdCore:1.0.0:applications:appA:1.1.0");
        assertThat(app.getEntityType()).isEqualTo(EntityTypeDPDS.APPLICATION.propertyValue());
        assertThat(app.getName()).isEqualTo("appA");
        assertThat(app.getVersion()).isEqualTo("1.1.0");
        assertThat(app.getDisplayName()).isEqualTo("Application A");
        assertThat(app.getDescription()).isEqualTo("Internal application A of data product");
        assertThat(app.getPlatform()).isEqualTo("platformY");
        assertThat(app.getApplicationType()).isEqualTo("spring-boot-app");
        assertThat(app.getComponentGroup()).isEqualTo("gruppoB");

        assertThat(app.getDependsOn()).isNotNull();
        assertThat(app.getDependsOn().size()).isEqualTo(1);
        assertThat(app.getDependsOn().get(0))
                .isEqualTo("urn:org.opendatamesh:dataproducts:dpdCore:1.0.0:infrastructures:infraA:1.1.0");
    }

    @Override
    public  void verifyCoreInfrastructuralComponents(DataProductVersionDPDS descriptor) {
        InternalComponentsDPDS internals = descriptor.getInternalComponents();
        assertThat(internals).isNotNull();
        assertThat(internals.getInfrastructuralComponents()).isNotNull();
        assertThat(internals.getInfrastructuralComponents().size()).isEqualTo(1);

        InfrastructuralComponentDPDS infra = internals.getInfrastructuralComponents().get(0);
        assertThat(infra.getId()).isEqualTo("d1d74ccd-0a90-3c13-8bb5-6628fec44fbe");
        assertThat(infra.getFullyQualifiedName())
                .isEqualTo("urn:org.opendatamesh:dataproducts:dpdCore:1.0.0:infrastructures:infraA:1.1.0");
        assertThat(infra.getEntityType()).isEqualTo(EntityTypeDPDS.INFRASTRUCTURE.propertyValue());
        assertThat(infra.getName()).isEqualTo("infraA");
        assertThat(infra.getVersion()).isEqualTo("1.1.0");
        assertThat(infra.getDisplayName()).isEqualTo("Infra A");
        assertThat(infra.getDescription()).isEqualTo("Infrastructure component A of data product");
        assertThat(infra.getPlatform()).isEqualTo("platformX");
        assertThat(infra.getInfrastructureType()).isEqualTo("storage-resource");
        assertThat(infra.getComponentGroup()).isEqualTo("gruppoC");
    }

    @Override
    public void verifyLifecycle(DataProductVersionDPDS descriptor) {
        InternalComponentsDPDS internals = descriptor.getInternalComponents();
        assertThat(internals).isNotNull();

        LifecycleInfoDPDS lifecycle = internals.getLifecycleInfo();
        assertThat(lifecycle).isNotNull();

        List<LifecycleActivityInfoDPDS> activities = lifecycle.getActivityInfos();
        assertThat(activities).isNotNull();
        assertThat(activities.size()).isEqualTo(3);

        LifecycleActivityInfoDPDS activity = null;

        // TEST
        activity = lifecycle.getActivityInfo("test");
        assertThat(activity).isNotNull();
        ExternalResourceDPDS service = activity.getService();
        assertThat(service).isNotNull();
        assertThat(service.getHref()).isEqualTo("{azure-devops}");
        StandardDefinitionDPDS template = activity.getTemplate();
        assertThat(template).isNotNull();
        assertThat(template.getName()).isEqualTo("testPipeline");
        assertThat(template.getVersion()).isEqualTo("1.0.0");
        assertThat(template.getSpecification()).isEqualTo("azure-devops");
        assertThat(template.getSpecificationVersion()).isEqualTo("1.0.0");
        DefinitionReferenceDPDS definition = template.getDefinition();
        assertThat(definition).isNotNull();
        //assertThat(definition.getRef()).isEqualTo("http://localhost:80/templates/{templateId}");
        Map<String, Object> configurations = activity.getConfigurations();
        assertThat(configurations).isNotNull();
        assertThat(configurations.get("stagesToSkip")).isEqualTo(Arrays.asList("Deploy"));

        // PROD
        activity = lifecycle.getActivityInfo("prod");
        assertThat(activity).isNotNull();
        service = activity.getService();
        assertThat(service).isNotNull();
        assertThat(service.getHref()).isEqualTo("{azure-devops}");
        template = activity.getTemplate();
        assertThat(template).isNotNull();
        assertThat(template.getName()).isEqualTo("testPipeline");
        assertThat(template.getVersion()).isEqualTo("1.0.0");
        assertThat(template.getSpecification()).isEqualTo("azure-devops");
        assertThat(template.getSpecificationVersion()).isEqualTo("1.0.0");
        definition = template.getDefinition();
        assertThat(definition).isNotNull();
        //assertThat(definition.getRef()).isEqualTo("http://localhost:80/templates/{templateId}");
        configurations = activity.getConfigurations();
        assertThat(configurations).isNotNull();
        assertThat(configurations.get("stagesToSkip")).isEqualTo(Arrays.asList());

        // DEPRECATED
        activity = lifecycle.getActivityInfo("deprecated");
        assertThat(activity).isNotNull();
        assertThat(activity.getService()).isNull();
        assertThat(activity.getTemplate()).isNull();
        assertThat(activity.getConfigurations()).isNull();
    }
}
