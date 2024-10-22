package org.opendatamesh.platform.pp.registry.server.utils;

import org.opendatamesh.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.dpds.model.core.EntityTypeDPDS;
import org.opendatamesh.dpds.model.core.ExternalResourceDPDS;
import org.opendatamesh.dpds.model.core.StandardDefinitionDPDS;
import org.opendatamesh.dpds.model.definitions.DefinitionReferenceDPDS;
import org.opendatamesh.dpds.model.info.InfoDPDS;
import org.opendatamesh.dpds.model.interfaces.InterfaceComponentsDPDS;
import org.opendatamesh.dpds.model.interfaces.PortDPDS;
import org.opendatamesh.dpds.model.interfaces.PromisesDPDS;
import org.opendatamesh.dpds.model.internals.InternalComponentsDPDS;
import org.opendatamesh.dpds.model.internals.LifecycleInfoDPDS;
import org.opendatamesh.dpds.model.internals.LifecycleTaskInfoDPDS;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class DPDExampleObjectChecker implements ResourceObjectChecker {

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
        assertThat(info.getDataProductId()).isEqualTo("e85ee0e5-87d1-334c-80a5-3d9407463ef5");
        assertThat(info.getFullyQualifiedName()).isEqualTo("urn:org.opendatamesh:dataproducts:flightfrequency:1");
        assertThat(info.getEntityType()).isEqualTo(EntityTypeDPDS.DATAPRODUCT.propertyValue());
        assertThat(info.getName()).isEqualTo("flightFrequency");
        assertThat(info.getVersionNumber()).isEqualTo("1.0.0");
        assertThat(info.getDomain()).isEqualTo("Airline");
        assertThat(info.getOwner());
        assertThat(info.getOwner().getId()).isEqualTo("john.smith@example.com");

    }

    @Override
    public void verifyCoreInterfaces(DataProductVersionDPDS descriptor) {

        InterfaceComponentsDPDS interfaces = descriptor.getInterfaceComponents();
        assertThat(interfaces).isNotNull();

        PortDPDS port;
        PromisesDPDS promises;

        assertThat(interfaces.getInputPorts()).isNotNull();
        assertThat(interfaces.getInputPorts().size()).isEqualTo(0);

        assertThat(interfaces.getOutputPorts()).isNotNull();
        assertThat(interfaces.getOutputPorts().size()).isEqualTo(2);

        port = interfaces.getOutputPorts().get(0);
        assertThat(port.getId()).isEqualTo("7ccdc04a-d96e-3d52-804a-05b6ae2cf46c");
        assertThat(port.getFullyQualifiedName()).isEqualTo(
                "urn:org.opendatamesh:dataproducts:flightfrequency:1:1.0.0:outputports:flight_frequency_db:1.0.0"
        );
        assertThat(port.getEntityType()).isEqualTo(EntityTypeDPDS.OUTPUTPORT.propertyValue());
        assertThat(port.getName()).isEqualTo("flight_frequency_db");
        assertThat(port.getVersion()).isEqualTo("1.0.0");
        assertThat(port.getDisplayName()).isEqualTo("flight_frequency_db");
        assertThat(port.getDescription()).isEqualTo("Target database for airlines data. MySQL database.");
        promises = port.getPromises();
        assertThat(promises).isNotNull();
        assertThat(promises.getPlatform()).isEqualTo("westeurope.azure::mysql");
        assertThat(promises.getApi()).isNotNull();
        assertThat(promises.getApi().getName()).isEqualTo("flightFrequencyApi");
        assertThat(promises.getApi().getVersion()).isEqualTo("1.0.0");
        assertThat(promises.getApi().getSpecification()).isEqualTo("datastoreapi");
        assertThat(promises.getApi().getSpecificationVersion()).isEqualTo("1.0.0");
        assertThat(promises.getApi().getDefinition()).isNotNull();
        assertThat(promises.getApi().getDefinition().getRef()).contains(
                "/api/v1/pp/registry/apis/dd0e4a95-7e73-382a-bc6f-c6c9ca772d6a"
        );

        port = interfaces.getOutputPorts().get(1);
        assertThat(port.getId()).isEqualTo("20d42877-c448-3733-b490-700fd333608b");
        assertThat(port.getFullyQualifiedName()).isEqualTo(
                "urn:org.opendatamesh:dataproducts:flightfrequency:1:1.0.0:outputports:flight_frequency_api:1.0.0"
        );
        assertThat(port.getEntityType()).isEqualTo(EntityTypeDPDS.OUTPUTPORT.propertyValue());
        assertThat(port.getName()).isEqualTo("flight_frequency_api");
        assertThat(port.getVersion()).isEqualTo("1.0.0");
        assertThat(port.getDisplayName()).isEqualTo("flight_frequency_api");
        assertThat(port.getDescription()).isEqualTo("REST API to get airlines data.");
        promises = port.getPromises();
        assertThat(promises).isNotNull();
        assertThat(promises.getPlatform()).isEqualTo("westeurope.azure::vm");
        assertThat(promises.getApi()).isNotNull();
        assertThat(promises.getApi().getName()).isEqualTo("flightFrequencyRestApi");
        assertThat(promises.getApi().getVersion()).isEqualTo("1.0.0");
        assertThat(promises.getApi().getSpecification()).isEqualTo("restapi");
        assertThat(promises.getApi().getSpecificationVersion()).isEqualTo("1.0.0");
        assertThat(promises.getApi().getDefinition()).isNotNull();
        assertThat(promises.getApi().getDefinition().getRef()).contains(
                "/api/v1/pp/registry/apis/42fff3e1-05f1-3327-9e7e-76f20ce0af97"
        );

    }

    @Override
    public void verifyCoreApplicationComponents(DataProductVersionDPDS descriptor) {

        InternalComponentsDPDS internals = descriptor.getInternalComponents();
        assertThat(internals).isNotNull();

        assertThat(internals.getApplicationComponents()).isNotNull();
        assertThat(internals.getApplicationComponents().size()).isEqualTo(0);

    }

    @Override
    public void verifyCoreInfrastructuralComponents(DataProductVersionDPDS descriptor) {

        InternalComponentsDPDS internals = descriptor.getInternalComponents();
        assertThat(internals).isNotNull();

        assertThat(internals.getInfrastructuralComponents()).isNotNull();
        assertThat(internals.getInfrastructuralComponents().size()).isEqualTo(0);

    }

    @Override
    public void verifyLifecycle(DataProductVersionDPDS descriptor) {

        InternalComponentsDPDS internals = descriptor.getInternalComponents();
        assertThat(internals).isNotNull();

        LifecycleInfoDPDS lifecycle = internals.getLifecycleInfo();
        assertThat(lifecycle).isNotNull();

        List<LifecycleTaskInfoDPDS> activities = lifecycle.getTasksInfo();
        assertThat(activities).isNotNull();
        assertThat(activities.size()).isEqualTo(2);

        List<LifecycleTaskInfoDPDS> tasksInfo;
        LifecycleTaskInfoDPDS taskInfo;

        // DEPLOY_APP_DEV
        tasksInfo = lifecycle.getTasksInfo("deployAppDev");
        assertThat(tasksInfo).isNotNull();
        taskInfo = tasksInfo.get(0);
        assertThat(taskInfo).isNotNull();
        ExternalResourceDPDS service = taskInfo.getService();
        assertThat(service).isNotNull();
        assertThat(service.getHref()).isEqualTo("azure-devops");
        StandardDefinitionDPDS template = taskInfo.getTemplate();
        assertThat(template).isNotNull();
        assertThat(template.getSpecification()).isEqualTo("spec");
        assertThat(template.getSpecificationVersion()).isEqualTo("2.0");
        assertThat(template.getEntityType()).isEqualTo(EntityTypeDPDS.TEMPLATE.propertyValue());
        assertThat(template.getFullyQualifiedName()).isEqualTo(
                "urn:org.opendatamesh:templates:afe46172-3897-3deb-85db-513310d3fd06:1.0.0"
        );
        assertThat(template.getId()).isEqualTo("fa0ebacc-bee4-39f5-a782-900155eb3506");
        DefinitionReferenceDPDS definition = template.getDefinition();
        assertThat(definition).isNotNull();
        assertThat(definition.getRef()).contains("/api/v1/pp/registry/templates/fa0ebacc-bee4-39f5-a782-900155eb3506");
        Map<String, Object> configurations = taskInfo.getConfigurations();
        assertThat(configurations).isNotNull();
        assertThat(configurations.get("stagesToSkip")).isEqualTo(Arrays.asList());
        assertThat(configurations.get("params")).isNotNull();

        // PROVISION_INFRA_DEV
        tasksInfo = lifecycle.getTasksInfo("provisionInfraDev");
        assertThat(tasksInfo).isNotNull();
        taskInfo = tasksInfo.get(0);
        assertThat(taskInfo).isNotNull();
        service = taskInfo.getService();
        assertThat(service).isNotNull();
        assertThat(service.getHref()).isEqualTo("azure-devops");
        template = taskInfo.getTemplate();
        assertThat(template).isNotNull();
        assertThat(template.getSpecification()).isEqualTo("spec");
        assertThat(template.getSpecificationVersion()).isEqualTo("2.0");
        assertThat(template.getEntityType()).isEqualTo(EntityTypeDPDS.TEMPLATE.propertyValue());
        assertThat(template.getFullyQualifiedName()).isEqualTo(
                "urn:org.opendatamesh:templates:afe46172-3897-3deb-85db-513310d3fd06:1.0.0"
        );
        assertThat(template.getId()).isEqualTo("fa0ebacc-bee4-39f5-a782-900155eb3506");
        definition = template.getDefinition();
        assertThat(definition).isNotNull();
        assertThat(definition.getRef()).contains("/api/v1/pp/registry/templates/fa0ebacc-bee4-39f5-a782-900155eb3506");
        configurations = taskInfo.getConfigurations();
        assertThat(configurations).isNotNull();
        assertThat(configurations.get("stagesToSkip")).isEqualTo(Arrays.asList());
        assertThat(configurations.get("params")).isNotNull();

    }

}
