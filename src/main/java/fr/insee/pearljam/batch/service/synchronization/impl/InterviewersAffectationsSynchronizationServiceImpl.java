package fr.insee.pearljam.batch.service.synchronization.impl;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.insee.pearljam.batch.dao.ClosingCauseDao;
import fr.insee.pearljam.batch.dao.InterviewerTypeDao;
import fr.insee.pearljam.batch.dao.OrganizationalUnitTypeDao;
import fr.insee.pearljam.batch.dao.SurveyUnitDao;
import fr.insee.pearljam.batch.dto.InterviewerAffectationsDto;
import fr.insee.pearljam.batch.dto.SimpleIdDto;
import fr.insee.pearljam.batch.enums.ContextReferentialSyncLogIds;
import fr.insee.pearljam.batch.exception.BatchException;
import fr.insee.pearljam.batch.exception.SynchronizationException;
import fr.insee.pearljam.batch.exception.TooManyReaffectationsException;
import fr.insee.pearljam.batch.service.ContextReferentialService;
import fr.insee.pearljam.batch.service.synchronization.InterviewersAffectationsSynchronizationService;
import fr.insee.pearljam.batch.template.CreatedInterviewersAffectations;
import fr.insee.pearljam.batch.template.InterviewerAffectation;
import fr.insee.pearljam.batch.template.InterviewerAffectationSynchronizationError;
import fr.insee.pearljam.batch.template.InterviewerAffectationsSynchronizationErrors;
import fr.insee.pearljam.batch.template.InterviewersAffectationsSynchronizationResult;
import fr.insee.pearljam.batch.template.InterviewersReaffectations;
import fr.insee.pearljam.batch.utils.BatchErrorCode;
import fr.insee.pearljam.batch.utils.Utils;
import fr.insee.pearljam.batch.utils.XmlUtils;



@Service
@Transactional(rollbackFor=Exception.class)
public class InterviewersAffectationsSynchronizationServiceImpl implements InterviewersAffectationsSynchronizationService {
	private static final Logger logger = LogManager.getLogger(InterviewersAffectationsSynchronizationServiceImpl.class);

	@Value("${fr.insee.pearljam.context.synchronization.log.elements:#{null}}")
	private String logIds;
	
	@Value("${fr.insee.pearljam.context.synchronization.interviewers.reaffectation.threshold.absolute:#{null}}")
	private Float absThreshold;
	
	@Value("${fr.insee.pearljam.context.synchronization.interviewers.reaffectation.threshold.relative:#{null}}")
	private Float relThreshold;
	
	@Autowired
	@Qualifier("pilotageConnection")
	Connection pilotageConnection;
	
	@Autowired
	ContextReferentialService opaleService;
	
	@Autowired
	InterviewerTypeDao interviewerTypeDao;
	
	@Autowired
	OrganizationalUnitTypeDao organizationalUnitTypeDao;
	
	@Autowired
	SurveyUnitDao surveyUnitDao;
	
	@Autowired
	ClosingCauseDao closingCauseDao;
	
	public BatchErrorCode synchronizeSurveyUnitInterviewerAffectation(String out) throws TooManyReaffectationsException, SynchronizationException, BatchException {
		logger.info("Interviewers affectations synchronization started");
		// processed, created, updated
		Long[] counters = {0L, 0L, 0L};
		List<InterviewerAffectation> createdIds = new ArrayList<>();
		List<InterviewerAffectation> updatedIds = new ArrayList<>();
		List<InterviewerAffectationSynchronizationError> errors = new ArrayList<>();
		BatchErrorCode code = BatchErrorCode.OK;
		
		
		List<InterviewerAffectationsDto> interviewersAffectations;
		interviewersAffectations = opaleService.getInterviewersAffectationsFromOpale();
		for(InterviewerAffectationsDto interviewerAffectations : interviewersAffectations) {
			code = processInterviewerAffectations(interviewerAffectations,
					counters, createdIds, updatedIds, errors, code);
		}

		

		if(absThreshold != null && absThreshold > 0 && counters[2] > absThreshold) {
			throw new TooManyReaffectationsException(
					"This synchronization would have reaffected " 
					+ counters[2].toString()
					+ " survey units to an other interviewer, "
					+ "but the maximum number of reaffectations allowed is "
					+ absThreshold.toString()
					+". (Defined in properties file: fr.insee.pearljam.context.synchronization.interviewers.reaffectation.threshold.absolute)"
					);
		}
		
		if(relThreshold != null && relThreshold > 0 && (float)counters[2]/counters[0] > relThreshold/100) {
			throw new TooManyReaffectationsException(
					"This synchronization would have reaffected " 
					+ ((float)counters[2]*100/counters[0])
					+ "% of survey units processed to an other interviewer, "
					+ "but the maximum percentage of reaffectation allowed is "
					+ relThreshold.toString()
					+"%. (Defined in properties file: fr.insee.pearljam.context.synchronization.interviewers.reaffectation.threshold.relative)"
					);
		}
		
		
		if(logIds != null && logIds.equals(ContextReferentialSyncLogIds.YES.getLabel())) {
			InterviewersAffectationsSynchronizationResult sync = new InterviewersAffectationsSynchronizationResult(
					code.getLabel(), 
					counters[0], counters[1], counters[2],  
					new CreatedInterviewersAffectations(createdIds),
					new InterviewersReaffectations(updatedIds),
					new InterviewerAffectationsSynchronizationErrors(errors)
				);
			XmlUtils.objectToXML(out+"/synchro/sync.SU_ITW." + Utils.getTimestamp() + ".xml", sync);
		}
		else {
			
			InterviewersAffectationsSynchronizationResult sync = new InterviewersAffectationsSynchronizationResult(
					code.getLabel(), 
					counters[0], counters[1], counters[2],  
					null,
					null,
					new InterviewerAffectationsSynchronizationErrors(errors)
				);

			String timestamp = Utils.getTimestamp();
			XmlUtils.objectToXML(out+"/synchro/sync.SU_ITW." + timestamp + ".xml", sync);
			if(logIds != null && logIds.equals(ContextReferentialSyncLogIds.IN_SEPARATE_FILES.getLabel())) {
				XmlUtils.objectToXML(out+"/synchro/sync.SU_ITW.created." + timestamp + ".xml", new CreatedInterviewersAffectations(createdIds));
				XmlUtils.objectToXML(out+"/synchro/sync.SU_ITW.reassigned." + timestamp + ".xml", new InterviewersReaffectations(updatedIds));
			}
		}

		
		logger.info("Interviewers affectations synchronization ended - result {}",code.getCode());
		logger.info("Created [{}] - Updated [{}] - Error [{}]",counters[1],counters[2],errors.size());
		return code;
	}
	
	private BatchErrorCode processInterviewerAffectations(
			InterviewerAffectationsDto interviewerAffectations,
			Long[] counters,
			List<InterviewerAffectation> createdIds,
			List<InterviewerAffectation> updatedIds,
			List<InterviewerAffectationSynchronizationError> errors,
			BatchErrorCode code) {
		BatchErrorCode returnCode = code;
		try {
			if(interviewerTypeDao.existInterviewer(interviewerAffectations.getIdep())) {
				List<String> ids = interviewerAffectations.getSurveyUnits().stream()
					.map(SimpleIdDto::getId)
					.collect(Collectors.toList());
				for(String id : ids) {
					returnCode = affectSurveyUnit(id, interviewerAffectations, 
							counters, createdIds, updatedIds, errors, returnCode);
				}
			}
			else {
				errors.add(new InterviewerAffectationSynchronizationError(
						interviewerAffectations.getIdep(),
						null,
						"Could not assign: missing interviewer",
						"Interviewer with id " + interviewerAffectations.getIdep() + " does not exist in Sabiane"
					));
				logger.error("Interviewer '{}' does not exist in Sabiane", interviewerAffectations.getIdep());
				if(returnCode == BatchErrorCode.OK) {
					returnCode = BatchErrorCode.OK_FONCTIONAL_WARNING;
				}
			}
		} catch (Exception e) {
			errors.add(new InterviewerAffectationSynchronizationError(
					interviewerAffectations.getIdep(),
					null,
					"Unexpected error while processing interviewer",
					"An exception occured while processing the affectations of interviewer with id " + interviewerAffectations.getIdep() + " : " + e.getMessage()
				));
			returnCode = BatchErrorCode.OK_TECHNICAL_WARNING;
		}
		return returnCode;
	}
	
	private BatchErrorCode affectSurveyUnit(String id, 
			InterviewerAffectationsDto interviewerAffectations, 
			Long[] counters,
			List<InterviewerAffectation> createdIds,
			List<InterviewerAffectation> updatedIds,
			List<InterviewerAffectationSynchronizationError> errors,
			BatchErrorCode code) {
		BatchErrorCode returnCode = code;
		if(surveyUnitDao.existSurveyUnit(id)) {
			// If reaffectation we delete closing causes
			String currentAffectation = surveyUnitDao.getSurveyUnitInterviewerAffectation(id);
			if(!interviewerAffectations.getIdep().equals(currentAffectation)) {
				if(currentAffectation != null) {
					closingCauseDao.deleteAllClosingCausesOfSurveyUnit(id);
					updatedIds.add(new InterviewerAffectation(interviewerAffectations.getIdep(), id));
					counters[2] += 1;
				}
				else {
					createdIds.add(new InterviewerAffectation(interviewerAffectations.getIdep(), id));
					counters[1] += 1;
				}
				surveyUnitDao.setSurveyUnitInterviewerAffectation(id, interviewerAffectations.getIdep());
			}
			
			counters[0] += 1;
		}
		else {
			errors.add(new InterviewerAffectationSynchronizationError(
					interviewerAffectations.getIdep(),
					id,
					"Could not assign: missing survey unit",
					"Survey unit with id " + id + " does not exist in Sabiane"
				));
			logger.error("Survey unit with id '{}' does not exist in Sabiane", id);
			if(returnCode == BatchErrorCode.OK) {
				returnCode = BatchErrorCode.OK_FONCTIONAL_WARNING;
			}
		}
		return returnCode;
	}

}
