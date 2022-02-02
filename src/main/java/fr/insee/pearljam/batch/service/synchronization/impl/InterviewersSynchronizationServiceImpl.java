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

import fr.insee.pearljam.batch.dao.InterviewerTypeDao;
import fr.insee.pearljam.batch.dao.OrganizationalUnitTypeDao;
import fr.insee.pearljam.batch.dto.InterviewerDto;
import fr.insee.pearljam.batch.enums.ContextReferentialSyncLogIds;
import fr.insee.pearljam.batch.exception.BatchException;
import fr.insee.pearljam.batch.exception.SynchronizationException;
import fr.insee.pearljam.batch.service.ContextReferentialService;
import fr.insee.pearljam.batch.service.HabilitationService;
import fr.insee.pearljam.batch.service.synchronization.InterviewersSynchronizationService;
import fr.insee.pearljam.batch.template.CreatedInterviewers;
import fr.insee.pearljam.batch.template.InterviewerSynchronizationError;
import fr.insee.pearljam.batch.template.InterviewerSynchronizationErrors;
import fr.insee.pearljam.batch.template.InterviewersSynchronizationResult;
import fr.insee.pearljam.batch.template.UpdatedInterviewers;
import fr.insee.pearljam.batch.utils.BatchErrorCode;
import fr.insee.pearljam.batch.utils.Utils;
import fr.insee.pearljam.batch.utils.XmlUtils;

@Service
public class InterviewersSynchronizationServiceImpl implements InterviewersSynchronizationService {
	private static final Logger logger = LogManager.getLogger(InterviewersSynchronizationServiceImpl.class);

	@Value("${fr.insee.pearljam.context.synchronization.log.elements:#{null}}")
	private String logIds;

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
	HabilitationService habilitationService;

	public BatchErrorCode synchronizeInterviewers(String out) throws SynchronizationException, BatchException {
		logger.info("Interviewers synchronization started");
		// processed, created, updated
		Long[] counters = { 0L, 0L, 0L };
		List<String> createdIds = new ArrayList<>();
		List<String> updatedIds = new ArrayList<>();
		List<InterviewerSynchronizationError> errors = new ArrayList<>();
		BatchErrorCode code = BatchErrorCode.OK;

		List<InterviewerDto> interviewers = opaleService.getInterviewersFromOpale();
		List<String> habilitatedInterviewers = habilitationService.getHabilitatedInterviewers();

		for (InterviewerDto interviewer : interviewers) {
			code = processInterviewer(interviewer, counters,
					createdIds, updatedIds, habilitatedInterviewers, errors, code);
		}

		if (logIds != null && logIds.equals(ContextReferentialSyncLogIds.YES.getLabel())) {
			InterviewersSynchronizationResult sync = new InterviewersSynchronizationResult(
					code.getLabel(),
					counters[0], counters[1], counters[2],
					new CreatedInterviewers(createdIds),
					new UpdatedInterviewers(updatedIds),
					new InterviewerSynchronizationErrors(errors));
			XmlUtils.objectToXML(out + "/synchro/sync.ITW." + Utils.getTimestamp() + ".xml", sync);
		} else {
			InterviewersSynchronizationResult sync = new InterviewersSynchronizationResult(
					code.getLabel(),
					counters[0], counters[1], counters[2],
					null,
					null,
					new InterviewerSynchronizationErrors(errors));
			String timestamp = Utils.getTimestamp();
			XmlUtils.objectToXML(out + "/synchro/sync.ITW." + timestamp + ".xml", sync);
			if (logIds != null && logIds.equals(ContextReferentialSyncLogIds.IN_SEPARATE_FILES.getLabel())) {
				XmlUtils.objectToXML(out + "/synchro/sync.ITW.created." + timestamp + ".xml",
						new CreatedInterviewers(createdIds));
				XmlUtils.objectToXML(out + "/synchro/sync.ITW.updated." + timestamp + ".xml",
						new UpdatedInterviewers(updatedIds));
			}
		}
		logger.info("Created interviewers : {}", String.join(" ", createdIds));
		logger.info("Updated interviewers : {}", String.join(" ", updatedIds));
		logger.info("Errors : {}", String.join(" ",
				errors.stream().map(InterviewerSynchronizationError::getInterviewerId).collect(Collectors.toList())));
		logger.info("Interviewers synchronization ended - result {}", code.getCode());
		return code;
	}

	public BatchErrorCode processInterviewer(
			InterviewerDto interviewer,
			Long[] counters,
			List<String> createdIds,
			List<String> updatedIds,
			List<String> alreadyHabilitatedIds,
			List<InterviewerSynchronizationError> errors,
			BatchErrorCode code) {
		BatchErrorCode returnCode = code;
		try {
			if (interviewerTypeDao.existInterviewer(interviewer.getIdep())) {
				if (!alreadyHabilitatedIds.contains(interviewer.getIdep())) {
					habilitationService.addInterviewerHabilitation(interviewer.getIdep());
				}
				if (interviewerTypeDao.isDifferentFromDto(interviewer)) {
					interviewerTypeDao.updateInterviewerFromDto(interviewer);
					counters[2] += 1;
					updatedIds.add(interviewer.getIdep());
				}
			} else {
				if (!alreadyHabilitatedIds.contains(interviewer.getIdep())) {
					habilitationService.addInterviewerHabilitation(interviewer.getIdep());
				}
				interviewerTypeDao.createInterviewerFromDto(interviewer);
				counters[1] += 1;
				createdIds.add(interviewer.getIdep());
			}
			counters[0] += 1;
		} catch (Exception e) {
			errors.add(new InterviewerSynchronizationError(
					interviewer.getIdep(),
					"Unexpected error while processing interviewer",
					"An exception occured while processing the interviewer with id " + interviewer.getIdep() + " : "
							+ e.getMessage()));
			returnCode = BatchErrorCode.OK_TECHNICAL_WARNING;
		}
		return returnCode;
	}

}
