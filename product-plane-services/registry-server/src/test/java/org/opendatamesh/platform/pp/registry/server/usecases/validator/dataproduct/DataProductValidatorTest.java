package org.opendatamesh.platform.pp.registry.server.usecases.validator.dataproduct;

import org.junit.jupiter.api.Test;

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
}
