package fr.insee.pearljam.batch.service;

import org.springframework.stereotype.Service;

import fr.insee.pearljam.batch.exception.SynchronizationException;

@Service
public interface HabilitationService {

    public void addInterviewerHabilitation(String interviewerIdep) throws SynchronizationException;
}
