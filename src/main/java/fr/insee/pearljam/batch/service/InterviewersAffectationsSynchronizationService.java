package fr.insee.pearljam.batch.service;

import java.sql.SQLException;

import org.springframework.stereotype.Service;

import fr.insee.pearljam.batch.exception.BatchException;
import fr.insee.pearljam.batch.exception.SynchronizationException;
import fr.insee.pearljam.batch.exception.TooManyReaffectationsException;
import fr.insee.pearljam.batch.utils.BatchErrorCode;



@Service

public interface InterviewersAffectationsSynchronizationService {

	public BatchErrorCode synchronizeSurveyUnitInterviewerAffectation(String out) throws SQLException, TooManyReaffectationsException, SynchronizationException, BatchException;
	
}
