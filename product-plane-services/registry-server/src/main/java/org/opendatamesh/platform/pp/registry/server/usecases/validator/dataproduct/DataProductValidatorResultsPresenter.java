package org.opendatamesh.platform.pp.registry.server.usecases.validator.dataproduct;

import java.util.List;

public interface DataProductValidatorResultsPresenter {
    void presentSyntaxValidationResult(DataProductValidatorResult syntaxValidationResult);

    void presentDataProductValidationResults(List<DataProductValidatorResult> dataProductPoliciesValidationResults);

    void presentDataProductVersionValidationResults(List<DataProductValidatorResult> dataProductVersionPoliciesValidationResults);

    void presentError(Exception e);
}
