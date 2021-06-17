package fr.insee.pearljam.batch.dao;

import java.util.List;

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
}
