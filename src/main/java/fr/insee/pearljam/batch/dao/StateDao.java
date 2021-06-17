package fr.insee.pearljam.batch.dao;

import java.util.List;

import fr.insee.pearljam.batch.campaign.StateType;
import fr.insee.pearljam.batch.campaign.SurveyUnitType;

/**
 * Interface for the State table
 * @author scorcaud
 *
 */
public interface StateDao {
	/**
     * Create a State in database
     * @param surveyUnit
     */
	public void createState(Long date, String type, String surveyUnitId);
	/**
     * Delete States for a list of SurveyUnit in database
     * @param list of SurveyUnit
     */
	void deleteStateForSurveyUnitList(List<SurveyUnitType> surveyUnitIdList);
	/**
     * Get States by SurveyUnit id in database
     * @param surveyUnitId
     * @return list of SurveyUnit
     */
	List<StateType> getStateBySurveyUnitId(String surveyUnitId);
	/**
     * Delete States by surveyUnitId in database
     * @param surveyUnitId
     */
	void deleteStateBySurveyUnitId(String surveyUnitId);
}
