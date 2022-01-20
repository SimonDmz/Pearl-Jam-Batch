package fr.insee.pearljam.batch.service.synchronization.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.insee.pearljam.batch.exception.SynchronizationException;
import fr.insee.pearljam.batch.service.ContextReferentialService;
import fr.insee.pearljam.batch.service.HabilitationService;
import fr.insee.pearljam.batch.service.synchronization.SynchronizationUtilsService;

@Service
public class SynchronizationUtilsServiceImpl implements SynchronizationUtilsService {

    @Autowired
    ContextReferentialService contextReferentialService;

    @Autowired
    HabilitationService habilitationService;

    public void checkServices() throws SynchronizationException {
        // contextReferentialService.contextReferentialServiceIsAvailable();
        habilitationService.isAvailable();
    };
}
