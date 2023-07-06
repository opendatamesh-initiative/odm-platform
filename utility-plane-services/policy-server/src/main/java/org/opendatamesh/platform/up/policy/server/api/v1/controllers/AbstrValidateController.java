package org.opendatamesh.platform.up.policy.server.api.v1.controllers;

import org.opendatamesh.platform.up.policy.api.v1.controllers.AbstractValidateController;
import org.opendatamesh.platform.up.policy.server.database.repositories.SuiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public abstract class AbstrValidateController extends AbstractValidateController {

    @Autowired
    protected SuiteRepository suiteRepository;

    @Override
    public abstract ResponseEntity validateDocument(String[] ids, String[] suites, Object document);

}
