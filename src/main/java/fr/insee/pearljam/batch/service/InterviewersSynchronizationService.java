package fr.insee.pearljam.batch.service;

import java.sql.SQLException;

import org.springframework.stereotype.Service;

import fr.insee.pearljam.batch.exception.BatchException;
import fr.insee.pearljam.batch.exception.SynchronizationException;
import fr.insee.pearljam.batch.utils.BatchErrorCode;



@Service
public interface InterviewersSynchronizationService {

	public BatchErrorCode synchronizeInterviewers(String out) throws SQLException, SynchronizationException, BatchException;
	
}
