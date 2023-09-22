package org.opendatamesh.platform.pp.devops.server.services;

import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiCommonErrors;
import org.opendatamesh.platform.pp.devops.server.database.entities.Activity;
import org.opendatamesh.platform.pp.devops.server.database.entities.Lifecycle;
import org.opendatamesh.platform.pp.devops.server.database.repositories.LifecycleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LifecycleService {

    @Autowired
    LifecycleRepository lifecycleRepository;

    public void createLifecycle(Activity activity) {
        Lifecycle previousLifecycle = null;
        previousLifecycle = getDataProductVersionCurrentLifecycle(activity.getDataProductId(), activity.getDataProductVersion());
        Lifecycle lifecycle = new Lifecycle();
        lifecycle.setDataProductId(activity.getDataProductId());
        lifecycle.setDataProductVersion(activity.getDataProductVersion());
        lifecycle.setStage(activity.getStage());
        try {
            lifecycle = lifecycleRepository.saveAndFlush(lifecycle);
        } catch (Throwable t) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while updating lifecycle",
                    t
            );
        }
        if(previousLifecycle != null) {
            updateLifecycle(previousLifecycle, lifecycle.getStartedAt());
        }
    }

    private void updateLifecycle(Lifecycle lifecycle, LocalDateTime finishedAt) {
        lifecycle.setFinishedAt(finishedAt.minusSeconds(1));
        try {
            lifecycleRepository.saveAndFlush(lifecycle);
        } catch (Throwable t) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while updating lifecycle",
                    t
            );
        }
    }

    public List<Lifecycle> getLifecycles() {
        try {
            //return lifecycleRepository.findAll();
            List<Lifecycle> lifecycles = lifecycleRepository.findAll();
            System.out.println(lifecycles.toString());
            return lifecycles;
        } catch (Throwable t) {
            throw new InternalServerException(
                ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                "An error occured in the backend database while loading lifecycles",
                t
            );
        }
    }

    public List<Lifecycle> getDataProductLifecycles(String dataProductId) {
        if (dataProductId == null) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_00_SERVICE_ERROR,
                    "Parameter dataProductId cannot be null");
        }
        try {
            return lifecycleRepository.findByDataProductId(dataProductId);
        } catch (Throwable t) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while loading lifecycles",
                    t
            );
        }
    }

    public List<Lifecycle> getDataProductVersionLifecycles(String dataProductId, String versionNumber) {
        try {
            return lifecycleRepository.findByDataProductIdAndDataProductVersion(dataProductId, versionNumber);
        } catch (Throwable t) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while loading lifecycles",
                    t
            );
        }
    }

    public Lifecycle getDataProductVersionCurrentLifecycle(String dataProductId, String versionNumber) {
        try {
            return lifecycleRepository.findByDataProductIdAndDataProductVersionAndFinishedAtIsNull(dataProductId, versionNumber);
        } catch (Throwable t) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while loading lifecycles",
                    t
            );
        }
    }

}
