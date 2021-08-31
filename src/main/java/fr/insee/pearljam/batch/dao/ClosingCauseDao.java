package fr.insee.pearljam.batch.dao;

import java.util.List;

import fr.insee.pearljam.batch.campaign.ClosingCauseType;

/**
 * Interface for the State table
 * @author scorcaud
 *
 */
public interface ClosingCauseDao {
	/**
     * Delete all closing causes by surveyUnitId in database
     * @param surveyUnitId
     */
	void deleteAllClosingCausesOfSurveyUnit(String surveyUnitId);

	List<String> getClosingCausesBySuId(String suId);
	
	/**
     * Get ContactOutcome by Survey unit id in database
     * @param surveyUnitId
     * @return ContactOutcomeType
     */
	ClosingCauseType getClosingCauseTypeBySurveyUnitId(String surveyUnitId) throws Exception;
}
