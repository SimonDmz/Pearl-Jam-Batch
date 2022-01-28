package fr.insee.pearljam.batch.service;

import java.util.List;

import org.springframework.stereotype.Service;

import fr.insee.pearljam.batch.exception.SynchronizationException;

@Service
public interface HabilitationService {

    public void addInterviewerHabilitation(String interviewerIdep) throws SynchronizationException;

    public List<String> getHabilitatedInterviewers() throws SynchronizationException;

    public void isAvailable() throws SynchronizationException;
}
