package org.opendatamesh.platform.pp.registry.server.usecases.validator.dataproduct;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.opendatamesh.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.pp.policy.api.resources.ValidationResponseResource;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.DataProductVersion;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproductversion.info.Info;
import org.opendatamesh.platform.pp.registry.server.database.mappers.DataProductVersionMapper;
import org.opendatamesh.platform.pp.registry.server.services.proxies.RegistryPolicyServiceProxy;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DataProductValidatorTest {
    @Test
    public void testDataProductValidatorOnlySyntax() {
        DataProductValidatorCommand command = new DataProductValidatorCommand();
        command.setValidatePolicies(false);
        DataProductValidatorParserOutboundPort parserOutboundPort = mock(DataProductValidatorParserOutboundPort.class);
        DataProductValidatorPolicyOutboundPort policyOutboundPort = mock(DataProductValidatorPolicyOutboundPort.class);
        DataProductValidatorRegistryOutboundPort registryOutboundPort = mock(DataProductValidatorRegistryOutboundPort.class);

        DataProductValidatorResultsPresenter presenter = mock(DataProductValidatorResultsPresenter.class);

        new DataProductValidatorImpl(
                parserOutboundPort,
                policyOutboundPort,
                registryOutboundPort,
                command,
                presenter
        ).validate();

        verify(presenter, times(1)).presentSyntaxValidationResult(any());
        verify(presenter, times(0)).presentDataProductValidationResults(any());
        verify(presenter, times(0)).presentDataProductVersionValidationResults(any());

        verify(presenter, times(0)).presentError(any());
    }

    @Test
    public void testDataProductValidatorOnlyPolicies() {
        DataProductValidatorCommand command = new DataProductValidatorCommand();
        command.setValidateSyntax(false);

        DataProductValidatorParserOutboundPort parserOutboundPort = mock(DataProductValidatorParserOutboundPort.class);
        DataProductValidatorPolicyOutboundPort policyOutboundPort = mock(DataProductValidatorPolicyOutboundPort.class);
        DataProductValidatorRegistryOutboundPort registryOutboundPort = mock(DataProductValidatorRegistryOutboundPort.class);

        DataProductValidatorResultsPresenter presenter = mock(DataProductValidatorResultsPresenter.class);

        new DataProductValidatorImpl(
                parserOutboundPort,
                policyOutboundPort,
                registryOutboundPort,
                command,
                presenter
        ).validate();

        verify(presenter, times(0)).presentSyntaxValidationResult(any());
        verify(presenter, times(1)).presentDataProductValidationResults(any());
        verify(presenter, times(1)).presentDataProductVersionValidationResults(any());

        verify(presenter, times(0)).presentError(any());
    }

    @Test
    public void testValidateDataProductVersionPublishWithMostRecentVersion() {
        // Create two different DataProductVersion objects with different version numbers
        DataProductVersion mostRecentVersion = new DataProductVersion();
        mostRecentVersion.setDataProductId("test-product");
        mostRecentVersion.setVersionNumber("1.0.0");
        Info mostRecentInfo = new Info();
        mostRecentInfo.setFullyQualifiedName("test:product");
        mostRecentInfo.setVersionNumber("1.0.0");
        mostRecentVersion.setInfo(mostRecentInfo);

        DataProductVersion newVersion = new DataProductVersion();
        newVersion.setDataProductId("test-product");
        newVersion.setVersionNumber("2.0.0");
        Info newInfo = new Info();
        newInfo.setFullyQualifiedName("test:product");
        newInfo.setVersionNumber("2.0.0");
        newVersion.setInfo(newInfo);

        // Create mock DPD objects to verify they are passed correctly
        DataProductVersionDPDS mostRecentVersionDPDS = new DataProductVersionDPDS();
        DataProductVersionDPDS newVersionDPDS = new DataProductVersionDPDS();

        // Mock dependencies
        RegistryPolicyServiceProxy policyServiceProxy = mock(RegistryPolicyServiceProxy.class);
        DataProductVersionMapper dataProductVersionMapper = mock(DataProductVersionMapper.class);
        
        // Configure mapper to return different DPD objects for different versions
        when(dataProductVersionMapper.toResource(mostRecentVersion)).thenReturn(mostRecentVersionDPDS);
        when(dataProductVersionMapper.toResource(newVersion)).thenReturn(newVersionDPDS);
        
        // Mock the validation response
        ValidationResponseResource validationResponse = new ValidationResponseResource();
        when(policyServiceProxy.testValidateDataProductVersion(any(), any())).thenReturn(validationResponse);

        // Create the implementation
        DataProductValidatorPolicyOutboundPortImpl validator = 
            new DataProductValidatorPolicyOutboundPortImpl(
                policyServiceProxy,
                dataProductVersionMapper,
                null // DataProductMapper not needed for this test
            );

        // Use ArgumentCaptor to capture the actual arguments passed to the method
        ArgumentCaptor<DataProductVersionDPDS> firstArgCaptor = ArgumentCaptor.forClass(DataProductVersionDPDS.class);
        ArgumentCaptor<DataProductVersionDPDS> secondArgCaptor = ArgumentCaptor.forClass(DataProductVersionDPDS.class);

        // Execute the method
        validator.validateDataProductVersionPublish(mostRecentVersion, newVersion);

        // Verify that testValidateDataProductVersion was called exactly once
        verify(policyServiceProxy, times(1)).testValidateDataProductVersion(
            firstArgCaptor.capture(),
            secondArgCaptor.capture()
        );
        
        // Verify that the first argument is the most recent version DPD
        DataProductVersionDPDS capturedFirstArg = firstArgCaptor.getValue();
        assertSame(mostRecentVersionDPDS, capturedFirstArg, 
            "First argument should be the most recent version DPD");
        
        // Verify that the second argument is the new version DPD (NOT the most recent version)
        DataProductVersionDPDS capturedSecondArg = secondArgCaptor.getValue();
        assertSame(newVersionDPDS, capturedSecondArg, 
            "Second argument should be the new version DPD");
        
        // Verify that the two arguments are different objects (not the same reference)
        assertNotSame(capturedFirstArg, capturedSecondArg, 
            "The two arguments should be different objects - the bug was passing the same object twice");
    }
}
