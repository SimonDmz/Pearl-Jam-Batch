package fr.insee.pearljam.batch.dao;

import java.sql.SQLException;

import fr.insee.pearljam.batch.campaign.ContactOutcomeType;

/**
 * Interface for the ContactOutcome table
 * @author scorcaud
 *
 */
public interface ContactOutcomeDao {
	/**
     * Get ContactOutcome by Survey unit id in database
     * @param surveyUnitId
     * @return ContactOutcomeType
     */
	ContactOutcomeType getContactOutcomeTypeBySurveyUnitId(String surveyUnitId) throws Exception;
	/**
     * Delete ContactOutcome by Survey unit id in database
     * @param surveyUnitId
     */
	void deleteContactOutcomeBySurveyUnitId(String surveyUnitId) throws SQLException;
}
